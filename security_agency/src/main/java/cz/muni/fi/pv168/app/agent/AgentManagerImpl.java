package cz.muni.fi.pv168.app.agent;

import javax.sql.DataSource;
import java.util.List;

/**
 * This class implements AgentManager service.
 *
 * @author Adam Baňanka, Daniel Homola
 */
public class AgentManagerImpl implements AgentManager {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createAgent(Agent agent) {

    }

    @Override
    public void updateAgent(Agent agent) {

    }

    @Override
    public void deleteAgent(Agent agent) {

    }

    @Override
    public Agent findAgent(long id) {
        return null;
    }



    @Override
    public List<Agent> findAllAgents() {
        return null;
    }
}
