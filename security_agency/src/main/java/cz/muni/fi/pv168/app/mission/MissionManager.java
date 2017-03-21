package cz.muni.fi.pv168.app.mission;

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
     */
    void createMission(Mission mission);

    /**
     * Updates mission in database.
     * Name of the mission can't be empty or duplicate.
     * Status can't be changed to previous one.
     *  (i.e. if status is IN_PROGRESS, it can't be changed back to NOT_ASSIGNED;
     *   if status is ACCOMPLISHED or FAILED, it can't be changed to any other)
     * Required rank must be in range between 1 and 10.
     *
     * @param mission   updated mission to be stored
     */
    void updateMission(Mission mission);

    /**
     * Deletes the mission from database.
     *
     * @param mission   mission to be deleted
     */
    void deleteMission(Mission mission);

    /**
     * Returns mission with given id.
     *
     * @param id    primary key of requested mission
     * @return      mission with given id or null if such mission doesn't exist
     */
    Mission findMission(long id);

    /**
     * Returns list of missions, which doesn't have any agent assigned,
     * i.e. has status NOT_ASSIGNED.
     *
     * @return  list of available missions in the database
     */
    List<Mission> findAvailableMissions();

    /**
     * Returns list of all missions in the database.
     *
     * @return  list of all missions in the database
     */
    List<Mission> findAllMissions();
}
