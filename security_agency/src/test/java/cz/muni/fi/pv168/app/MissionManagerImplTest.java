package cz.muni.fi.pv168.app;

import cz.muni.fi.pv168.app.mission.Mission;
import cz.muni.fi.pv168.app.mission.MissionManagerImpl;
import cz.muni.fi.pv168.app.mission.MissionStatus;
import org.junit.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Adam Baňanka
 */
public class MissionManagerImplTest {
    private MissionManagerImpl manager = new MissionManagerImpl();

    private MissionBuilder sampleEasyMissionBuilder() {
        return new MissionBuilder()
                .id(null)
                .name("easy_mission")
                .agent(null)
                .status(MissionStatus.IN_PROGRESS)
                .info("Really easy mission.");
    }

    private MissionBuilder sampleHardMissionBuilder() {
        return new MissionBuilder()
                .id(null)
                .name("hard_mission")
                .agent(null)
                .status(MissionStatus.FAILED)
                .info("Really hard mission.");
    }



    @Test
    public void createMission() {
        Mission mission = sampleEasyMissionBuilder().build();
        manager.createMission(mission);
        Long missionId = mission.getId();
        assertThat(manager.findMission(missionId))
                .isNotSameAs(mission)
                .isEqualToComparingFieldByField(mission);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullMission() {
        manager.createMission(null);
    }

    @Test
    public void createMissionWithNullName() {
        Mission mission = sampleEasyMissionBuilder().name(null).build();
        assertThatThrownBy(() -> manager.createMission(mission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createMissionWithExistingName() {
        Mission mission1 = sampleEasyMissionBuilder().build();
        manager.createMission(mission1);
        Mission mission2 = sampleHardMissionBuilder().name("easy_mission").build();
        assertThatThrownBy(() -> manager.createMission(mission2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createMissionWithNullInfo() {
        Mission mission = sampleEasyMissionBuilder().info(null).build();
        assertThatThrownBy(() -> manager.createMission(mission)).isInstanceOf(IllegalArgumentException.class);
    }



    private void updateMission(Consumer<Mission> updateOperation) {
        Mission mainMission = sampleEasyMissionBuilder().build();
        Mission anotherMission = sampleHardMissionBuilder().build();
        manager.createMission(mainMission);
        manager.createMission(anotherMission);

        updateOperation.accept(mainMission);
        manager.updateMission(mainMission);
        assertThat(manager.findMission(mainMission.getId())).isEqualToComparingFieldByField(mainMission);
        assertThat(manager.findMission(anotherMission.getId())).isEqualToComparingFieldByField(anotherMission);
    }

    @Test
    public void updateMissionName() {
        updateMission((mission) -> mission.setName("main_mission"));
    }

    @Test
    public void updateMissionStatus() {
        updateMission((mission) -> mission.setStatus(MissionStatus.ACCOMPLISHED));
    }

    @Test
    public void updateMissionInfo() {
        updateMission((mission) -> mission.setInfo("Main mission, not so easy."));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullMission() {
        manager.updateMission(null);
    }

    @Test
    public void updateMissionWithNullName() {
        Mission mission = sampleEasyMissionBuilder().build();
        manager.createMission(mission);
        mission.setName(null);
        assertThatThrownBy(() -> manager.updateMission(mission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateMissionWithExistingName() {
        Mission mission = sampleEasyMissionBuilder().build();
        Mission anotherMission = sampleHardMissionBuilder().build();
        manager.createMission(mission);
        manager.createMission(anotherMission);
        mission.setName("hard_mission");
        assertThatThrownBy(() -> manager.updateMission(mission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateMissionWithPreviousStatus() {
        Mission mission = sampleHardMissionBuilder().build();
        manager.createMission(mission);
        mission.setStatus(MissionStatus.IN_PROGRESS);
        assertThatThrownBy(() -> manager.updateMission(mission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateMissionWithNullInfo() {
        Mission mission = sampleHardMissionBuilder().build();
        manager.createMission(mission);
        mission.setInfo(null);
        assertThatThrownBy(() -> manager.updateMission(mission)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void deleteMission() {
        Mission mission1 = sampleEasyMissionBuilder().build();
        Mission mission2 = sampleHardMissionBuilder().build();
        manager.createMission(mission1);
        manager.createMission(mission2);

        assertThat(manager.findMission(mission1.getId())).isNotNull();
        assertThat(manager.findMission(mission2.getId())).isNotNull();
        manager.deleteMission(mission1);
        assertThat(manager.findMission(mission1.getId())).isNull();
        assertThat(manager.findMission(mission2.getId())).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullMission() {
        manager.deleteMission(null);
    }


    @Test
    public void findFreeMissions() {
        assertThat(manager.findAllMissions()).isEmpty();

        Mission m1 = sampleEasyMissionBuilder().build();
        Mission m2 = sampleHardMissionBuilder().status(MissionStatus.NOT_ASSIGNED).build();
        manager.createMission(m1);
        manager.createMission(m2);

        assertThat(manager.findAllMissions())
                .usingFieldByFieldElementComparator()
                .containsOnly(m2);
    }

    @Test
    public void findAllMissions() {
        assertThat(manager.findAllMissions()).isEmpty();

        Mission m1 = sampleEasyMissionBuilder().build();
        Mission m2 = sampleHardMissionBuilder().build();
        manager.createMission(m1);
        manager.createMission(m2);

        assertThat(manager.findAllMissions())
                .usingFieldByFieldElementComparator()
                .containsOnly(m1,m2);
    }
}
