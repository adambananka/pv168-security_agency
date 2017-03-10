package cz.muni.fi.pv168.app.mission;

import java.util.List;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public interface MissionManager {

    void createMission(Mission mission);

    void updateMission(Mission mission);

    void deleteMission(Mission mission);

    Mission findMission(long id);

    List<Mission> findFreeMissions();

    List<Mission> findAllMissions();
}
