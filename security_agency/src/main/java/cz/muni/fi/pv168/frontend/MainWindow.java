package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.backend.agent.Agent;
import cz.muni.fi.pv168.backend.mission.Mission;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 07-May-17.
 */
public class MainWindow {
    private JList agentList;
    private JList missionList;
    private JButton editAgentButton;
    private JButton editMissionButton;
    private JButton deleteAgentButton;
    private JButton deleteMissionButton;
    private JButton addAgentButton;
    private JButton addMissionButton;
    private JButton assignAgentButton;
    private JRadioButton allAgentsRadioButton;
    private JRadioButton availableAgentsRadioButton;
    private JRadioButton availableMissionsRadioButton;
    private JRadioButton agentsMissionsRadioButton;
    private JRadioButton allMissionsRadioButton;
    private JLabel missionRequiredRankInfo;
    private JLabel missionStatusInfo;
    private JLabel agentAliveInfo;
    private JLabel agentRankInfo;
    private JPanel ContentPane;

    public MainWindow() {
        JFrame frame = new JFrame();
        frame.setContentPane(ContentPane);

        addAgentButton.addActionListener(e -> onAddAgent());
        editAgentButton.addActionListener(e -> onEditAgent());
        deleteAgentButton.addActionListener(e -> onDeleteAgent());
        addMissionButton.addActionListener(e -> onAddMission());
        editMissionButton.addActionListener(e -> onEditMission());
        deleteMissionButton.addActionListener(e -> onDeleteMission());
        assignAgentButton.addActionListener(e -> onAssignAgent());
        allAgentsRadioButton.addActionListener(e -> onAllAgentsShow());
        availableAgentsRadioButton.addActionListener(e -> onAvailableAgentsShow());
        allMissionsRadioButton.addActionListener(e -> onAllMissionsShow());
        availableMissionsRadioButton.addActionListener(e -> onAvailableMissionsShow());
        agentsMissionsRadioButton.addActionListener(e -> onAgentsMissionsShow());
        agentList.addListSelectionListener(e -> onAgentSelection((Agent) agentList.getSelectedValue()));
        missionList.addListSelectionListener(e -> onMissionSelection((Mission) missionList.getSelectedValue()));

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 500));

        test();

        frame.pack();
        frame.setVisible(true);
    }

    private void test() {
        List<Agent> agents = new ArrayList<>();
        Agent agent1 = new Agent();
        agent1.setName("Adam Bananka");
        agent1.setId(1L);
        agent1.setRank(5);
        agent1.setAlive(true);
        agents.add(agent1);
        Agent agent2 = new Agent();
        agent2.setName("Dano Homola");
        agent2.setId(2L);
        agent2.setRank(3);
        agent2.setAlive(false);
        agents.add(agent2);
        agentList.setListData(agents.toArray());
    }

    private void onAddAgent() {
        AddAgentDialog dialog = new AddAgentDialog();
        dialog.pack();
        dialog.setVisible(true);
        //TODO
    }

    private void onEditAgent() {
        EditAgentDialog dialog = new EditAgentDialog();
        dialog.pack();
        dialog.setVisible(true);
        //TODO
    }

    private void onDeleteAgent() {
        //TODO
    }

    private void onAddMission() {
        AddMissionDialog dialog = new AddMissionDialog();
        dialog.pack();
        dialog.setVisible(true);
        //TODO
    }

    private void onEditMission() {
        EditMissionDialog dialog = new EditMissionDialog();
        dialog.pack();
        dialog.setVisible(true);
        //TODO
    }

    private void onDeleteMission() {
        //TODO
    }

    private void onAssignAgent() {
        //TODO
    }

    private void onAllAgentsShow() {
        if (!allAgentsRadioButton.isSelected()) {
            //TODO refresh agent list
        }
        allAgentsRadioButton.setSelected(true);
        availableAgentsRadioButton.setSelected(false);
    }

    private void onAvailableAgentsShow() {
        if (!availableAgentsRadioButton.isSelected()) {
            //TODO refresh agent list
        }
        availableAgentsRadioButton.setSelected(true);
        allAgentsRadioButton.setSelected(false);
    }

    private void onAllMissionsShow() {
        if (!allMissionsRadioButton.isSelected()) {
            //TODO refresh mission list
        }
        allMissionsRadioButton.setSelected(true);
        availableMissionsRadioButton.setSelected(false);
        agentsMissionsRadioButton.setSelected(false);
    }

    private void onAvailableMissionsShow() {
        if (!availableMissionsRadioButton.isSelected()) {
            //TODO refresh mission list
        }
        allMissionsRadioButton.setSelected(false);
        availableMissionsRadioButton.setSelected(true);
        agentsMissionsRadioButton.setSelected(false);
    }

    private void onAgentsMissionsShow() {
        if (!agentsMissionsRadioButton.isSelected()) {
            //TODO refresh mission list
        }
        allMissionsRadioButton.setSelected(false);
        availableMissionsRadioButton.setSelected(false);
        agentsMissionsRadioButton.setSelected(true);
    }

    private void onAgentSelection(Agent agent) {
        agentAliveInfo.setText(agent.isAlive() ? "localized alive" : "localized dead");
        agentRankInfo.setText(String.valueOf(agent.getRank()));
    }

    private void onMissionSelection(Mission mission) {
        missionStatusInfo.setText(String.valueOf(mission.getStatus()));//TODO localize
        missionRequiredRankInfo.setText(String.valueOf(mission.getRequiredRank()));
    }
}
