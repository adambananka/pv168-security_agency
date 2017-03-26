package cz.muni.fi.pv168.app;

import cz.muni.fi.pv168.app.agent.Agent;
import cz.muni.fi.pv168.app.mission.Mission;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public class AgencyManagerImpl implements AgencyManager {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void assignAgent(Agent agent, Mission mission) {

    }

    @Override
    public List<Mission> findMissionsOfAgent(Agent agent) {
        return null;
    }

    @Override
    public List<Agent> findAvailableAgents() {
        return null;
    }
}
