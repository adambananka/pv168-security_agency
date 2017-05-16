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
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
        new AddAgentDialog(agentManager, bundle);

        refreshAgentList();
        //TODO check
    }

    private void onEditAgent() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessage"), bundle.getString("Message"), 0);
            return;
        }
        new EditAgentDialog(agentManager, (Agent) agentList.getSelectedValue(), bundle);

        refreshAgentList();
        //TODO check
    }

    private void onDeleteAgent() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessage"), bundle.getString("Message"), 0);
            return;
        }
        new deleteAgentWorker((Agent) agentList.getSelectedValue()).execute();
        //agentList.clearSelection();
        //refreshAgentList();
        //TODO check
    }

    private void onAddMission() {
        new AddMissionDialog(missionManager, bundle);

        refreshMissionList();
        //TODO check
    }

    private void onEditMission() {
        if (missionList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessage"), bundle.getString("Message"), 0);
            return;
        }

        new EditMissionDialog(missionManager, (Mission) missionList.getSelectedValue(), bundle);

        refreshMissionList();
        //TODO check
    }

    private void onDeleteMission() {
        if (missionList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessage"), bundle.getString("Message"), 0);
            return;
        }
        new deleteMissionWorker((Mission) missionList.getSelectedValue()).execute();
        //missionList.clearSelection();
        //refreshMissionList();
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
        new assignAgentWorker((Agent) agentList.getSelectedValue(), (Mission) missionList.getSelectedValue()).execute();
        /*try {
            agencyManager.assignAgent((Agent) agentList.getSelectedValue(), (Mission) missionList.getSelectedValue());
        } catch (ValidationException | IllegalEntityException ex) {
            JOptionPane.showMessageDialog(null, bundle.getString(ex.getMessage()) + bundle.getString("Please, correct" +
                    " it."), bundle.getString("Message"), 0); //TODO localize
        }
        onAgentSelection((Agent) agentList.getSelectedValue());
        onMissionSelection((Mission) missionList.getSelectedValue());
        refreshAgentList();
        refreshMissionList();*/
    }

    private void onAllAgentsShow() {
        //if (!allAgentsRadioButton.isSelected()) {
        //agentList.setListData(agentManager.findAllAgents().toArray());
        //}
        new findAgentsWorker(0).execute();
        allAgentsRadioButton.setSelected(true);
        availableAgentsRadioButton.setSelected(false);
    }

    private void onAvailableAgentsShow() {
        //if (!availableAgentsRadioButton.isSelected()) {
        //agentList.setListData(agencyManager.findAvailableAgents().toArray());
        //}
        new findAgentsWorker(1).execute();
        availableAgentsRadioButton.setSelected(true);
        allAgentsRadioButton.setSelected(false);
    }

    private void onAllMissionsShow() {
        //if (!allMissionsRadioButton.isSelected()) {
        //missionList.setListData(missionManager.findAllMissions().toArray());
        //}
        new findMissionsWorker(0, null).execute();
        allMissionsRadioButton.setSelected(true);
        availableMissionsRadioButton.setSelected(false);
        agentsMissionsRadioButton.setSelected(false);
    }

    private void onAvailableMissionsShow() {
        //if (!availableMissionsRadioButton.isSelected()) {
        //missionList.setListData(missionManager.findAvailableMissions().toArray());
        //}
        new findMissionsWorker(1, null).execute();
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
        //missionList.setListData(agencyManager.findMissionsOfAgent((Agent) agentList.getSelectedValue()).toArray());
        //}
        new findMissionsWorker(2, (Agent) agentList.getSelectedValue()).execute();
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
        refreshMissionList();
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
            //missionsAgentInfo.setText(agentManager.findAgent(mission.getAgentId()).getName());
            new findSingleAgentWorker(mission.getAgentId()).execute();
        }
        else {
            missionsAgentInfo.setText("");
        }
    }

    private void refreshAgentList() {
        if (availableAgentsRadioButton.isSelected()) {
            //agentList.setListData(agencyManager.findAvailableAgents().toArray());
            new findAgentsWorker(1).execute();
            return;
        }
        //agentList.setListData(agentManager.findAllAgents().toArray());
        new findAgentsWorker(0).execute();
    }

    private void refreshMissionList() {
        if (agentsMissionsRadioButton.isSelected()) {
            if (!agentList.isSelectionEmpty()) {
                //missionList.setListData(agencyManager.findMissionsOfAgent((Agent) agentList.getSelectedValue()).toArray());
                new findMissionsWorker(2, (Agent) agentList.getSelectedValue()).execute();
                return;
            }
        }
        if (availableMissionsRadioButton.isSelected()) {
            //missionList.setListData(missionManager.findAvailableMissions().toArray());
            new findMissionsWorker(1, null).execute();
            return;
        }
        //missionList.setListData(missionManager.findAllMissions().toArray());
        new findMissionsWorker(0, null).execute();
    }

    public class deleteAgentWorker extends SwingWorker<Void, Void> {
        private Agent agent;

        public deleteAgentWorker(Agent agent) {
            this.agent = agent;
        }

        @Override
        protected Void doInBackground() throws Exception {
            agentManager.deleteAgent(agent);
            return null;
        }

        @Override
        protected void done() {
            agentList.clearSelection();
            refreshAgentList();
        }
    }

    public class deleteMissionWorker extends SwingWorker<Void, Void> {
        private Mission mission;

        public deleteMissionWorker(Mission mission) {
            this.mission = mission;
        }

        @Override
        protected Void doInBackground() throws Exception {
            missionManager.deleteMission(mission);
            return null;
        }

        @Override
        protected void done() {
            missionList.clearSelection();
            refreshMissionList();
        }
    }

    public class assignAgentWorker extends SwingWorker<Exception, Void> {
        private Agent agent;
        private Mission mission;

        public assignAgentWorker(Agent agent, Mission mission) {
            this.agent = agent;
            this.mission = mission;
        }

        @Override
        protected Exception doInBackground() throws Exception {
            try {
                agencyManager.assignAgent(agent, mission);
            } catch (ValidationException | IllegalEntityException ex) {
                return ex;
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                Exception ex = get();
                if (ex != null) {
                    JOptionPane.showMessageDialog(null, bundle.getString(ex.getMessage()) + bundle.getString("Please, correct" +
                            " it."), bundle.getString("Message"), 0); //TODO localize
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            onAgentSelection((Agent) agentList.getSelectedValue());
            onMissionSelection((Mission) missionList.getSelectedValue());
            refreshAgentList();
            refreshMissionList();
        }
    }

    public class findSingleAgentWorker extends SwingWorker<Agent, Void> {
        private Long id;

        public findSingleAgentWorker(Long id) {
            this.id = id;
        }

        @Override
        protected Agent doInBackground() throws Exception {
            return agentManager.findAgent(id);
        }

        @Override
        protected void done() {
            try {
                Agent res = get();
                missionsAgentInfo.setText(res.getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public class findAgentsWorker extends SwingWorker<List<Agent>, Void> {
        //0 = all, 1 = available
        private int selection;

        public findAgentsWorker(int selection) {
            this.selection = selection;
        }

        @Override
        protected List<Agent> doInBackground() throws Exception {
            switch (selection) {
                case 0:
                    return agentManager.findAllAgents();
                case 1:
                    return agencyManager.findAvailableAgents();
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                List<Agent> res = get();
                agentList.setListData(res.toArray());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public class findMissionsWorker extends SwingWorker<List<Mission>, Void> {
        //0 = all, 1 = available, 2 = agent's
        private int selection;
        //set only if selection == 2, otherwise null and not used
        private Agent agent;

        public findMissionsWorker(int selection, Agent agent) {
            this.selection = selection;
            this.agent = agent;
        }

        @Override
        protected List<Mission> doInBackground() throws Exception {
            switch (selection) {
                case 0:
                    return missionManager.findAllMissions();
                case 1:
                    return missionManager.findAvailableMissions();
                case 2:
                    return agencyManager.findMissionsOfAgent(agent);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                List<Mission> res = get();
                missionList.setListData(res.toArray());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
