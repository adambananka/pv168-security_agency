package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.backend.common.ValidationException;
import cz.muni.fi.pv168.backend.mission.Mission;
import cz.muni.fi.pv168.backend.mission.MissionManager;
import cz.muni.fi.pv168.backend.mission.MissionStatus;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public class EditMissionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField missionNameField;
    private JSlider missionRequiredRankSlider;
    private JRadioButton notAssignedRadioButton;
    private JRadioButton inProgressRadioButton;
    private JRadioButton accomplishedRadioButton;
    private JRadioButton failedRadioButton;

    private MissionManager missionManager;
    private Mission mission;
    private ResourceBundle bundle;

    public EditMissionDialog(MissionManager manager, Mission mission, ResourceBundle bundle) {
        missionManager = manager;
        this.mission = mission;
        this.bundle = bundle;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        notAssignedRadioButton.addActionListener(e -> onNotAssignedRadioButton());
        inProgressRadioButton.addActionListener(e -> onInProgressRadioButton());
        accomplishedRadioButton.addActionListener(e -> onAccomplishedRadioButton());
        failedRadioButton.addActionListener(e -> onFailedRadioButton());
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setTitle(bundle.getString("EditMissionDialog"));
        setLocationRelativeTo(this);
        missionNameField.setText(mission.getName());
        missionRequiredRankSlider.setValue(mission.getRequiredRank());
        switch (mission.getStatus()) {
            case NOT_ASSIGNED:
                notAssignedRadioButton.setSelected(true);
                break;
            case IN_PROGRESS:
                inProgressRadioButton.setSelected(true);
                break;
            case ACCOMPLISHED:
                accomplishedRadioButton.setSelected(true);
                break;
            case FAILED:
                failedRadioButton.setSelected(true);
        }

        pack();
        setVisible(true);
    }

    private void onOK() {
        mission.setName(missionNameField.getText());
        mission.setRequiredRank(missionRequiredRankSlider.getValue());
        if (notAssignedRadioButton.isSelected()) {
            mission.setStatus(MissionStatus.NOT_ASSIGNED);
        } else if (inProgressRadioButton.isSelected()) {
            mission.setStatus(MissionStatus.IN_PROGRESS);
        } else if (accomplishedRadioButton.isSelected()) {
            mission.setStatus(MissionStatus.ACCOMPLISHED);
        } else {
            mission.setStatus(MissionStatus.FAILED);
        }

        new EditMissionWorker().execute();
    }

    private void onCancel() {
        dispose();
    }

    private void onNotAssignedRadioButton() {
        notAssignedRadioButton.setSelected(true);
        inProgressRadioButton.setSelected(false);
        accomplishedRadioButton.setSelected(false);
        failedRadioButton.setSelected(false);
    }

    private void onInProgressRadioButton() {
        notAssignedRadioButton.setSelected(false);
        inProgressRadioButton.setSelected(true);
        accomplishedRadioButton.setSelected(false);
        failedRadioButton.setSelected(false);
    }

    private void onAccomplishedRadioButton() {
        notAssignedRadioButton.setSelected(false);
        inProgressRadioButton.setSelected(false);
        accomplishedRadioButton.setSelected(true);
        failedRadioButton.setSelected(false);
    }

    private void onFailedRadioButton() {
        notAssignedRadioButton.setSelected(false);
        inProgressRadioButton.setSelected(false);
        accomplishedRadioButton.setSelected(false);
        failedRadioButton.setSelected(true);
    }

    public class EditMissionWorker extends SwingWorker<Exception, Void> {

        @Override
        protected Exception doInBackground() throws Exception {
            try {
                missionManager.updateMission(mission);
            } catch (ValidationException ex) {
                return ex;
            }
            return null;
        }

        @Override
        protected void done() {
            dispose();
            try {
                Exception ex = get();
                if (ex != null ) {
                    JOptionPane.showMessageDialog(null, bundle.getString(ex.getMessage()) + bundle.getString("Please, correct" +
                            " it."), bundle.getString("Message"), JOptionPane.ERROR_MESSAGE);
                    new EditMissionDialog(missionManager, mission, bundle);
                }
            } catch (InterruptedException | ExecutionException e) {
                LoggerFactory.getLogger(EditMissionDialog.class).error("Worker get() error.", e);
            }
        }
    }
}
