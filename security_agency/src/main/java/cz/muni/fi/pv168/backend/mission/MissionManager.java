package cz.muni.fi.pv168.backend.mission;

import cz.muni.fi.pv168.backend.common.IllegalEntityException;
import cz.muni.fi.pv168.backend.common.ServiceFailureException;
import cz.muni.fi.pv168.backend.common.ValidationException;

import java.util.List;

/**
 * This service allows to manipulate with missions.
 *
 * @author Adam Baňanka, Daniel Homola
 */
public interface MissionManager {

    /**
     * Stores new mission to the database.
     * Id for the new mission is automatically generated and stored into id attribute.
     * Name of the mission can't be empty or duplicate.
     * Required rank must be in range between 1 and 10.
     *
     * @param mission   mission to be created
     * @throws IllegalArgumentException when mission is null
     * @throws IllegalEntityException   when mission has already assigned id
     * @throws ValidationException      when mission breaks validation rules
     * @throws ServiceFailureException  when db operation fails
     */
    void createMission(Mission mission) throws ServiceFailureException, ValidationException, IllegalEntityException;

    /**
     * Updates mission in database.
     * Name of the mission can't be empty or duplicate.
     * Status can't be changed to previous one.
     *  (i.e. if status is IN_PROGRESS, it can't be changed back to NOT_ASSIGNED;
     *   if status is ACCOMPLISHED or FAILED, it can't be changed to any other)
     * Required rank must be in range between 1 and 10.
     *
     * @param mission   updated mission to be stored
     * @throws IllegalArgumentException when mission is null
     * @throws IllegalEntityException   when mission has null id or does not exist in the database
     * @throws ValidationException      when mission breaks validation rules
     * @throws ServiceFailureException  when db operation fails
     */
    void updateMission(Mission mission) throws ServiceFailureException, ValidationException, IllegalEntityException;

    /**
     * Deletes the mission from database.
     *
     * @param mission   mission to be deleted
     * @throws IllegalArgumentException when mission is null
     * @throws IllegalEntityException   when given mission has null id or does not exist in the database
     * @throws ServiceFailureException  when db operation fails
     */
    void deleteMission(Mission mission) throws ServiceFailureException, IllegalEntityException;

    /**
     * Returns mission with given id.
     *
     * @param id    primary key of requested mission
     * @return      mission with given id or null if such mission doesn't exist
     * @throws IllegalArgumentException when given id is null
     * @throws ServiceFailureException  when db operation fails
     */
    Mission findMission(Long id) throws ServiceFailureException;

    /**
     * Returns list of missions, which doesn't have any agent assigned,
     * i.e. has status NOT_ASSIGNED.
     *
     * @return  list of available missions in the database
     * @throws ServiceFailureException  when db operation fails
     */
    List<Mission> findAvailableMissions() throws ServiceFailureException;

    /**
     * Returns list of all missions in the database.
     *
     * @return  list of all missions in the database
     * @throws ServiceFailureException  when db operation fails
     */
    List<Mission> findAllMissions() throws ServiceFailureException;
}
