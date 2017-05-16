package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.backend.common.ValidationException;
import cz.muni.fi.pv168.backend.mission.Mission;
import cz.muni.fi.pv168.backend.mission.MissionManager;
import cz.muni.fi.pv168.backend.mission.MissionStatus;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public class AddMissionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField missionNameField;
    private JSlider missionRequiredRankSlider;

    private MissionManager missionManager;
    private ResourceBundle bundle;

    public AddMissionDialog(MissionManager manager, ResourceBundle bundle) {
        missionManager = manager;
        this.bundle = bundle;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setTitle(bundle.getString("AddMissionDialog")); //TODO localize
        setLocationRelativeTo(this);

        pack();
        setVisible(true);
    }

    private void onOK() {
        Mission mission = new Mission();
        mission.setStatus(MissionStatus.NOT_ASSIGNED);
        mission.setName(missionNameField.getText());
        mission.setRequiredRank(missionRequiredRankSlider.getValue());

        new AddMissionWorker(mission).execute();
        /*try {
            missionManager.createMission(mission);
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(null, bundle.getString(ex.getMessage()) + bundle.getString("Please, correct" +
                    " it."), bundle.getString("Message"), 0); //TODO localize
            dispose();
            new AddMissionDialog(missionManager, bundle);
        }*/
        //TODO check
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public class AddMissionWorker extends SwingWorker<Exception, Void> {
        private Mission mission;

        public AddMissionWorker(Mission mission) {
            this.mission = mission;
        }

        @Override
        protected Exception doInBackground() throws Exception {
            try {
                missionManager.createMission(mission);
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
                if (ex != null) {
                    JOptionPane.showMessageDialog(null, bundle.getString(ex.getMessage()) + bundle.getString("Please, correct" +
                            " it."), bundle.getString("Message"), 0); //TODO localize
                    new AddMissionDialog(missionManager, bundle);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
