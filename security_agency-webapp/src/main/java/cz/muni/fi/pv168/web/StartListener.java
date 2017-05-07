package cz.muni.fi.pv168.web;

import cz.muni.fi.pv168.backend.AgencyManager;
import cz.muni.fi.pv168.backend.agent.AgentManagerImpl;
import cz.muni.fi.pv168.backend.common.DBUtils;
import cz.muni.fi.pv168.backend.mission.MissionManagerImpl;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by Adam on 10-Apr-17
 */
@WebListener
public class StartListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        DataSource dataSource = createMemoryDatabase();
        AgentManagerImpl agentManager = new AgentManagerImpl();
        agentManager.setDataSource(dataSource);
        servletContext.setAttribute("agentManager", agentManager );
        MissionManagerImpl missionManager = new MissionManagerImpl();
        missionManager.setDataSource(dataSource);
        servletContext.setAttribute("missionManager", missionManager);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private DataSource createMemoryDatabase() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(EmbeddedDriver.class.getName());
        ds.setUrl("jdbc:derby:memory:agencyDB;create=true");
        try {
            DBUtils.executeSqlScript(ds,AgencyManager.class.getResource("createTables.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
