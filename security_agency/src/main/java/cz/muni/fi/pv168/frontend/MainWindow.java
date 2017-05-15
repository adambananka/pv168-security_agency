package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.backend.AgencyManager;
import cz.muni.fi.pv168.backend.agent.Agent;
import cz.muni.fi.pv168.backend.agent.AgentManager;
import cz.muni.fi.pv168.backend.common.IllegalEntityException;
import cz.muni.fi.pv168.backend.common.ValidationException;
import cz.muni.fi.pv168.backend.mission.Mission;
import cz.muni.fi.pv168.backend.mission.MissionManager;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

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
    private ResourceBundle bundle;

    public MainWindow(MissionManager missionManager, AgentManager agentManager, AgencyManager agencyManager) {
        this.missionManager = missionManager;
        this.agentManager = agentManager;
        this.agencyManager = agencyManager;
        bundle = ResourceBundle.getBundle("locale", Locale.getDefault());

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
        frame.setTitle(bundle.getString("MainWindowTitle"));
        frame.setLocation(600, 300); //some relative location?
        frame.pack();
        frame.setVisible(true);

        refreshAgentList();
        refreshMissionList();
    }

    private void onAddAgent() {
        AddAgentDialog dialog = new AddAgentDialog(agentManager, bundle);
        dialog.pack();
        dialog.setVisible(true);
        refreshAgentList();
        //TODO check
    }

    private void onEditAgent() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessage"), bundle.getString("Message"), 0);
            return;
        }
        EditAgentDialog dialog = new EditAgentDialog(agentManager, (Agent) agentList.getSelectedValue(), bundle);
        dialog.pack();
        dialog.setVisible(true);
        refreshAgentList();
        //TODO check
    }

    private void onDeleteAgent() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessage"), bundle.getString("Message"), 0);
            return;
        }
        agentManager.deleteAgent((Agent) agentList.getSelectedValue());
        agentList.clearSelection();
        refreshAgentList();
        //TODO check
    }

    private void onAddMission() {
        AddMissionDialog dialog = new AddMissionDialog(missionManager, bundle);
        dialog.pack();
        dialog.setVisible(true);
        refreshMissionList();
        //TODO check
    }

    private void onEditMission() {
        if (missionList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessage"), bundle.getString("Message"), 0);
            return;
        }
        EditMissionDialog dialog = new EditMissionDialog(missionManager, (Mission) missionList.getSelectedValue(),
                bundle);
        dialog.pack();
        dialog.setVisible(true);
        refreshMissionList();
        //TODO check
    }

    private void onDeleteMission() {
        if (missionList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessage"), bundle.getString("Message"), 0);
            return;
        }
        missionManager.deleteMission((Mission) missionList.getSelectedValue());
        missionList.clearSelection();
        refreshMissionList();
        //TODO check
    }

    private void onAssignAgent() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessage"), bundle.getString("Message"), 0);
            return;
        }
        if (missionList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessage"), bundle.getString("Message"), 0);
            return;
        }
        try {
            agencyManager.assignAgent((Agent) agentList.getSelectedValue(), (Mission) missionList.getSelectedValue());
        } catch (ValidationException | IllegalEntityException ex) {
            JOptionPane.showMessageDialog(null, bundle.getString(ex.getMessage()) + bundle.getString("Please, correct" +
                    " it."), bundle.getString("Message"), 0); //TODO localize
        }
        onAgentSelection((Agent) agentList.getSelectedValue());
        onMissionSelection((Mission) missionList.getSelectedValue());
        refreshAgentList();
        refreshMissionList();
    }

    private void onAllAgentsShow() {
        //if (!allAgentsRadioButton.isSelected()) {
        agentList.setListData(agentManager.findAllAgents().toArray());
        //}
        allAgentsRadioButton.setSelected(true);
        availableAgentsRadioButton.setSelected(false);
    }

    private void onAvailableAgentsShow() {
        //if (!availableAgentsRadioButton.isSelected()) {
        agentList.setListData(agencyManager.findAvailableAgents().toArray());
        //}
        availableAgentsRadioButton.setSelected(true);
        allAgentsRadioButton.setSelected(false);
    }

    private void onAllMissionsShow() {
        //if (!allMissionsRadioButton.isSelected()) {
        missionList.setListData(missionManager.findAllMissions().toArray());
        //}
        allMissionsRadioButton.setSelected(true);
        availableMissionsRadioButton.setSelected(false);
        agentsMissionsRadioButton.setSelected(false);
    }

    private void onAvailableMissionsShow() {
        //if (!availableMissionsRadioButton.isSelected()) {
        missionList.setListData(missionManager.findAvailableMissions().toArray());
        //}
        allMissionsRadioButton.setSelected(false);
        availableMissionsRadioButton.setSelected(true);
        agentsMissionsRadioButton.setSelected(false);
    }

    private void onAgentsMissionsShow() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessage"), bundle.getString("Message"), 0);
            agentsMissionsRadioButton.setSelected(false);
            return;
        }
        //if (!agentsMissionsRadioButton.isSelected()) {
        missionList.setListData(agencyManager.findMissionsOfAgent((Agent) agentList.getSelectedValue()).toArray());
        //}
        allMissionsRadioButton.setSelected(false);
        availableMissionsRadioButton.setSelected(false);
        agentsMissionsRadioButton.setSelected(true);
    }

    private void onAgentSelection(Agent agent) {
        if (agent == null) {
            agentAliveInfo.setText("");
            agentRankInfo.setText("");
            return;
        }
        agentAliveInfo.setText(agent.isAlive() ? bundle.getString("Alive") : bundle.getString("Dead")); //TODO localize
        agentRankInfo.setText(String.valueOf(agent.getRank()));
    }

    private void onMissionSelection(Mission mission) {
        if (mission == null) {
            missionStatusInfo.setText("");
            missionRequiredRankInfo.setText("");
            missionsAgentInfo.setText("");
            return;
        }
        missionStatusInfo.setText(bundle.getString(String.valueOf(mission.getStatus())));//TODO localize
        missionRequiredRankInfo.setText(String.valueOf(mission.getRequiredRank()));
        if (mission.getAgentId() > 0L) {
            missionsAgentInfo.setText(agentManager.findAgent(mission.getAgentId()).getName());
        }
        else {
            missionsAgentInfo.setText("");
        }
    }

    private void refreshAgentList() {
        if (availableAgentsRadioButton.isSelected()) {
            agentList.setListData(agencyManager.findAvailableAgents().toArray());
            return;
        }
        agentList.setListData(agentManager.findAllAgents().toArray());
    }

    private void refreshMissionList() {
        if (agentsMissionsRadioButton.isSelected()) {
            if (!agentList.isSelectionEmpty()) {
                missionList.setListData(agencyManager.findMissionsOfAgent((Agent) agentList.getSelectedValue())
                        .toArray());
                return;
            }
        }
        if (availableMissionsRadioButton.isSelected()) {
            missionList.setListData(missionManager.findAvailableMissions().toArray());
            return;
        }
        missionList.setListData(missionManager.findAllMissions().toArray());
    }
}
