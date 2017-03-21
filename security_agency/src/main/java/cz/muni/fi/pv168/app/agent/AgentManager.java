package cz.muni.fi.pv168.app.agent;

import cz.muni.fi.pv168.app.common.IllegalEntityException;
import cz.muni.fi.pv168.app.common.ServiceFailureException;
import cz.muni.fi.pv168.app.common.ValidationException;

import java.util.List;

/**
 * This service allows to manipulate with agent.
 *
 * @author Adam Baňanka, Daniel Homola
 */
public interface AgentManager {

    /**
     * Stores new agent into database. Id for the new agent is automatically
     * generated and stored into id attribute.
     *
     * @param agent agent to be created.
     * @throws IllegalArgumentException when agent is null.
     * @throws IllegalEntityException when agent has already assigned id.
     * @throws ValidationException when agent breaks validation rules (name is
     * null, rank is negative or higher than 10.
     * @throws ServiceFailureException when db operation fails.
     */
    void createAgent(Agent agent) throws ServiceFailureException, ValidationException, IllegalEntityException;

    /**
     * Returns agent with given id.
     *
     * @param id primary key of requested body.
     * @return agent with given id or null if such agent does not exist.
     * @throws IllegalArgumentException when given id is null.
     * @throws ServiceFailureException when db operation fails.
     */
    Agent findAgent(long id) throws ServiceFailureException;

    /**
     * Updates agent in database.
     *
     * @param agent updated agent to be stored into database.
     * @throws IllegalArgumentException when agent is null.
     * @throws IllegalEntityException when agent has null id or does not exist in the database
     * @throws ValidationException when agent breaks validation rules (name is
     * null, rank is negative or higher than 10.
     * @throws ServiceFailureException when db operation fails.
     */
    void updateAgent(Agent agent) throws ServiceFailureException, ValidationException, IllegalEntityException;

    /**
     * Deletes agent from database.
     *
     * @param agent agent to be deleted from db.
     * @throws IllegalArgumentException when agent is null.
     * @throws IllegalEntityException when given agent has null id or does not exist in the database
     * @throws ServiceFailureException when db operation fails.
     */
    void deleteAgent(Agent agent) throws ServiceFailureException, IllegalEntityException;

    /**
     * Returns list of all agents in the database.
     *
     * @return list of all agents in database.
     * @throws ServiceFailureException when db operation fails.
     */
    List<Agent> findAllAgents() throws ServiceFailureException;
}
