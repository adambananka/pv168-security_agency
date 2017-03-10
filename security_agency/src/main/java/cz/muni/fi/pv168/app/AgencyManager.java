package cz.muni.fi.pv168.app;

import cz.muni.fi.pv168.app.agent.Agent;
import cz.muni.fi.pv168.app.mission.Mission;

import java.util.List;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public interface AgencyManager {

    void AssignAgent(Agent agent, Mission mission);

    List<Mission> findMissionsOfAgent(Agent agent);
}
