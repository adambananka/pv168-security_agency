package cz.muni.fi.pv168.app;

import cz.muni.fi.pv168.app.agent.Agent;
import cz.muni.fi.pv168.app.common.IllegalEntityException;
import cz.muni.fi.pv168.app.common.ValidationException;
import cz.muni.fi.pv168.app.mission.Mission;

import java.util.List;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public interface AgencyManager {

    /**
     * Assigns agent to mission.
     *
     * @throws IllegalEntityException when agent is already on mission.
     * @throws ValidationException when agent's rank is not sufficient
     * for the mission.
     * @param agent to be assigned
     * @param mission which will have an assigned agent
     */
    void assignAgent(Agent agent, Mission mission) throws IllegalEntityException, ValidationException;

    /**
     * Returns all agent's missions.
     *
     * @param agent chosen agent
     * @return list all missions of particular agent.
     */
    List<Mission> findMissionsOfAgent(Agent agent);
}
