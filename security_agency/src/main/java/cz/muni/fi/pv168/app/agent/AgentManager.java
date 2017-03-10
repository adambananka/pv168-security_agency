package cz.muni.fi.pv168.app.agent;

import java.util.List;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public interface AgentManager {

    void createAgent(Agent agent);

    void updateAgent(Agent agent);

    void deleteAgent(Agent agent);

    Agent findAgent(long id);

    List<Agent> findFreeAgents();

    List<Agent> findAllAgents();
}
