package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.backend.common.ValidationException;
import cz.muni.fi.pv168.backend.mission.Mission;
import cz.muni.fi.pv168.backend.mission.MissionManager;

import javax.swing.*;
import java.awt.event.*;

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

    public EditMissionDialog(MissionManager manager, Mission mission) {
        missionManager = manager;
        this.mission = mission;

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

        setTitle("Edit Mission dialog"); //TODO localize
        setLocationRelativeTo(this);
        missionNameField.setText(mission.getName());
        missionRequiredRankSlider.setValue(mission.getRequiredRank());
        switch (mission.getStatus()) {   //TODO localize
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
    }

    private void onOK() {
        mission.setName(missionNameField.getText());
        mission.setRequiredRank(missionRequiredRankSlider.getValue());
        //mission status set

        try {
            missionManager.updateMission(mission);
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage() + " Please, correct it."); //TODO localize
            onOK();
        }
        //TODO
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
