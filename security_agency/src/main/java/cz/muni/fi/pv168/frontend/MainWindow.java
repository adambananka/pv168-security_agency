package cz.muni.fi.pv168.frontend;

import javax.swing.*;

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

    public static void main(String[] args) {
        MainWindow app = new MainWindow();

    }
}
