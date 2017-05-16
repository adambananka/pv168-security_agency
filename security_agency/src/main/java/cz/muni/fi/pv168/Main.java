package cz.muni.fi.pv168;

import cz.muni.fi.pv168.backend.AgencyManager;
import cz.muni.fi.pv168.backend.AgencyManagerImpl;
import cz.muni.fi.pv168.backend.agent.AgentManagerImpl;
import cz.muni.fi.pv168.backend.common.DBUtils;
import cz.muni.fi.pv168.backend.mission.MissionManagerImpl;
import cz.muni.fi.pv168.frontend.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.awt.*;
import java.sql.SQLException;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        MissionManagerImpl missionManager = new MissionManagerImpl();
        AgentManagerImpl agentManager = new AgentManagerImpl();
        AgencyManagerImpl agencyManager = new AgencyManagerImpl();

        try {
            //DataSource dataSource = DBUtils.createMemoryDatabase();
            DataSource dataSource = DBUtils.getDataSource();
            DBUtils.executeSqlScript(dataSource,AgencyManager.class.getResource("createTables.sql"));
            DBUtils.executeSqlScript(dataSource, AgencyManager.class.getResource("testData.sql"));
            missionManager.setDataSource(dataSource);
            agentManager.setDataSource(dataSource);
            agencyManager.setDataSource(dataSource);
        } catch (SQLException e) {
            String msg  = "Error when setting data source";
            logger.error(msg, e);
            return;
        }

        EventQueue.invokeLater(() -> {
            MainWindow app = new MainWindow(missionManager, agentManager, agencyManager);
        });
    }
}
