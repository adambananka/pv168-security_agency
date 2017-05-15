package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.backend.common.ValidationException;
import cz.muni.fi.pv168.backend.mission.Mission;
import cz.muni.fi.pv168.backend.mission.MissionManager;
import cz.muni.fi.pv168.backend.mission.MissionStatus;

import javax.swing.*;
import java.awt.event.*;
import java.util.ResourceBundle;

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

        setTitle("Add Mission dialog"); //TODO localize
        setLocationRelativeTo(this);
    }

    private void onOK() {
        Mission mission = new Mission();
        mission.setStatus(MissionStatus.NOT_ASSIGNED);
        mission.setName(missionNameField.getText());
        mission.setRequiredRank(missionRequiredRankSlider.getValue());

        try {
            missionManager.createMission(mission);
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage() + " Please, correct it."); //TODO localize
            dispose();
            AddMissionDialog dialog = new AddMissionDialog(missionManager, bundle);
            dialog.pack();
            dialog.setVisible(true);
        }
        //TODO check
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
