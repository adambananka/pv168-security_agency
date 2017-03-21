package cz.muni.fi.pv168.app;

import cz.muni.fi.pv168.app.agent.Agent;
import cz.muni.fi.pv168.app.common.IllegalEntityException;
import cz.muni.fi.pv168.app.common.ServiceFailureException;
import cz.muni.fi.pv168.app.common.ValidationException;
import cz.muni.fi.pv168.app.mission.Mission;

import java.util.List;

/**
 * This service allows to manipulate with associations between agents and missions.
 *
 * @author Adam Baňanka, Daniel Homola
 */
public interface AgencyManager {

    /**
     * Assigns agent to mission.
     *
     * @param agent to be assigned
     * @param mission which will have an assigned agent
     * @throws IllegalArgumentException when agent or mission is null
     * @throws IllegalEntityException when agent is already on mission, when mission is already assigned
     *  or when agent or mission have null id or do not exist in database
     * @throws ValidationException when agent's rank is not sufficient for the mission
     *  or agent is dead
     * @throws ServiceFailureException when db operation fails
     */
    void assignAgent(Agent agent, Mission mission)
            throws IllegalEntityException, ValidationException, ServiceFailureException;

    /**
     * Returns all agent's missions.
     *
     * @param agent chosen agent
     * @return list all missions of particular agent
     * @throws IllegalArgumentException when agent is null
     * @throws IllegalEntityException when agent has null id
     * @throws ServiceFailureException when db operation fails
     */
    List<Mission> findMissionsOfAgent(Agent agent) throws IllegalEntityException, ServiceFailureException;

    /**
     * Returns list of agents, those aren't assigned to any IN_PROGRESS mission.
     *
     * @return list of available agents in database
     * @throws ServiceFailureException when db operation fails
     */
    List<Agent> findAvailableAgents() throws ServiceFailureException;
}
