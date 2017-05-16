package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.backend.AgencyManager;
import cz.muni.fi.pv168.backend.agent.Agent;
import cz.muni.fi.pv168.backend.agent.AgentManager;
import cz.muni.fi.pv168.backend.common.IllegalEntityException;
import cz.muni.fi.pv168.backend.common.ValidationException;
import cz.muni.fi.pv168.backend.mission.Mission;
import cz.muni.fi.pv168.backend.mission.MissionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);
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
        frame.setLocation(600, 300);
        frame.pack();
        frame.setVisible(true);

        refreshAgentList();
        refreshMissionList();
    }

    private void onAddAgent() {
        new AddAgentDialog(agentManager, bundle);

        refreshAgentList();
    }

    private void onEditAgent() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessageAgent"), bundle.getString("Message"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        new EditAgentDialog(agentManager, (Agent) agentList.getSelectedValue(), bundle);

        refreshAgentList();
    }

    private void onDeleteAgent() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessageAgent"), bundle.getString("Message"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        new deleteAgentWorker((Agent) agentList.getSelectedValue()).execute();
    }

    private void onAddMission() {
        new AddMissionDialog(missionManager, bundle);

        refreshMissionList();
    }

    private void onEditMission() {
        if (missionList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessageMission"), bundle.getString("Message"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        new EditMissionDialog(missionManager, (Mission) missionList.getSelectedValue(), bundle);

        refreshMissionList();
    }

    private void onDeleteMission() {
        if (missionList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessageMission"), bundle.getString("Message"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        new deleteMissionWorker((Mission) missionList.getSelectedValue()).execute();
    }

    private void onAssignAgent() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessageAgent"), bundle.getString("Message"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (missionList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessageMission"), bundle.getString("Message"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        new assignAgentWorker((Agent) agentList.getSelectedValue(), (Mission) missionList.getSelectedValue()).execute();
    }

    private void onAllAgentsShow() {
        new findAgentsWorker(0).execute();
        allAgentsRadioButton.setSelected(true);
        availableAgentsRadioButton.setSelected(false);
    }

    private void onAvailableAgentsShow() {
        new findAgentsWorker(1).execute();
        availableAgentsRadioButton.setSelected(true);
        allAgentsRadioButton.setSelected(false);
    }

    private void onAllMissionsShow() {
        new findMissionsWorker(0, null).execute();
        allMissionsRadioButton.setSelected(true);
        availableMissionsRadioButton.setSelected(false);
        agentsMissionsRadioButton.setSelected(false);
    }

    private void onAvailableMissionsShow() {
        new findMissionsWorker(1, null).execute();
        allMissionsRadioButton.setSelected(false);
        availableMissionsRadioButton.setSelected(true);
        agentsMissionsRadioButton.setSelected(false);
    }

    private void onAgentsMissionsShow() {
        if (agentList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("ErrorMessageAgent"), bundle.getString("Message"), JOptionPane.ERROR_MESSAGE);
            agentsMissionsRadioButton.setSelected(false);
            return;
        }

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

        agentAliveInfo.setText(agent.isAlive() ? bundle.getString("Alive") : bundle.getString("Dead"));
        agentRankInfo.setText(String.valueOf(agent.getRank()));
        if (agentsMissionsRadioButton.isSelected()) {
            new findMissionsWorker(2, (Agent) agentList.getSelectedValue()).execute();
        }
    }

    private void onMissionSelection(Mission mission) {
        if (mission == null) {
            missionStatusInfo.setText("");
            missionRequiredRankInfo.setText("");
            missionsAgentInfo.setText("");
            return;
        }

        missionStatusInfo.setText(bundle.getString(String.valueOf(mission.getStatus())));
        missionRequiredRankInfo.setText(String.valueOf(mission.getRequiredRank()));
        if (mission.getAgentId() > 0L) {
            new findSingleAgentWorker(mission.getAgentId()).execute();
        } else {
            missionsAgentInfo.setText("");
        }
    }

    private void refreshAgentList() {
        if (availableAgentsRadioButton.isSelected()) {
            new findAgentsWorker(1).execute();
            return;
        }
        new findAgentsWorker(0).execute();
    }

    private void refreshMissionList() {
        if (agentsMissionsRadioButton.isSelected()) {
            if (!agentList.isSelectionEmpty()) {
                new findMissionsWorker(2, (Agent) agentList.getSelectedValue()).execute();
                return;
            }
        }
        if (availableMissionsRadioButton.isSelected()) {
            new findMissionsWorker(1, null).execute();
            return;
        }
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
                            " it."), bundle.getString("Message"), JOptionPane.ERROR_MESSAGE);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Worker get() error.", e);
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
                if (res != null) {
                    missionsAgentInfo.setText(res.getName());
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Worker get() error.", e);
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
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Worker get() error.", e);
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
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Worker get() error.", e);
            }
        }
    }
}
