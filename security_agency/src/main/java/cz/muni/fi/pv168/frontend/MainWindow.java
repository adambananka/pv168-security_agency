package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.backend.AgencyManager;
import cz.muni.fi.pv168.backend.agent.Agent;
import cz.muni.fi.pv168.backend.agent.AgentManager;
import cz.muni.fi.pv168.backend.mission.Mission;
import cz.muni.fi.pv168.backend.mission.MissionManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Baňanka, Daniel Homola
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
    private JLabel missionsAgentInfo;

    private MissionManager missionManager;
    private AgentManager agentManager;
    private AgencyManager agencyManager;

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
        frame.setTitle("Agency Manager"); //TODO localize
        frame.setLocation(600, 300); //some relative location?

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
        AddAgentDialog dialog = new AddAgentDialog(agentManager);
        dialog.pack();
        dialog.setVisible(true);
        //TODO check
    }

    private void onEditAgent() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, "You need to choose some agent from the list first."); //TODO localize
            return;
        }
        EditAgentDialog dialog = new EditAgentDialog(agentManager, (Agent) agentList.getSelectedValue());
        dialog.pack();
        dialog.setVisible(true);
        //TODO check
    }

    private void onDeleteAgent() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, "You need to choose some agent from the list first."); //TODO localize
            return;
        }
        //TODO
    }

    private void onAddMission() {
        AddMissionDialog dialog = new AddMissionDialog(missionManager);
        dialog.pack();
        dialog.setVisible(true);
        //TODO check
    }

    private void onEditMission() {
        if (missionList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, "You need to choose some mission from the list first."); //TODO localize
            return;
        }
        EditMissionDialog dialog = new EditMissionDialog(missionManager, (Mission) missionList.getSelectedValue());
        dialog.pack();
        dialog.setVisible(true);
        //TODO check
    }

    private void onDeleteMission() {
        if (missionList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, "You need to choose some mission from the list first."); //TODO localize
            return;
        }
        missionManager.deleteMission((Mission) missionList.getSelectedValue());
        missionList.clearSelection();
        if (agentsMissionsRadioButton.isSelected()) {
            if (!agentList.isSelectionEmpty()) {
                missionList.setListData(agencyManager.findMissionsOfAgent((Agent) agentList.getSelectedValue()).toArray());
                return;
            }
        }
        if (availableMissionsRadioButton.isSelected()) {
            missionList.setListData(missionManager.findAvailableMissions().toArray());
            return;
        }
        missionList.setListData(missionManager.findAllMissions().toArray());
        //TODO check
    }

    private void onAssignAgent() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, "You need to choose some agent from the list first."); //TODO localize
            return;
        }
        if (missionList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, "You need to choose some mission from the list first."); //TODO localize
            return;
        }
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
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, "You need to choose some agent from the list first."); //TODO localize
            agentsMissionsRadioButton.setSelected(false);
            return;
        }
        if (!agentsMissionsRadioButton.isSelected()) {
            //TODO refresh mission list
        }
        allMissionsRadioButton.setSelected(false);
        availableMissionsRadioButton.setSelected(false);
        agentsMissionsRadioButton.setSelected(true);
    }

    private void onAgentSelection(Agent agent) {
        agentAliveInfo.setText(agent.isAlive() ? "alive" : "dead"); //TODO localize
        agentRankInfo.setText(String.valueOf(agent.getRank()));
    }

    private void onMissionSelection(Mission mission) {
        missionStatusInfo.setText(String.valueOf(mission.getStatus()));//TODO localize
        missionRequiredRankInfo.setText(String.valueOf(mission.getRequiredRank()));
        missionsAgentInfo.setText(agentManager.findAgent(mission.getAgentId()).toString());
    }
}
