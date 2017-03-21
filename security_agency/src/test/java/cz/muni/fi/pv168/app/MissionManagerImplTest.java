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
                .requiredRank(2);
    }

    private MissionBuilder sampleHardMissionBuilder() {
        return new MissionBuilder()
                .id(null)
                .name("hard_mission")
                .agent(null)
                .status(MissionStatus.FAILED)
                .requiredRank(7);
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
        Mission easyMission = sampleEasyMissionBuilder().name(null).build();
        assertThatThrownBy(() -> manager.createMission(easyMission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createMissionWithEmptyName() {
        Mission easyMission = sampleEasyMissionBuilder().name("").build();
        assertThatThrownBy(() -> manager.createMission(easyMission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createMissionWithExistingName() {
        Mission easyMission = sampleEasyMissionBuilder().build();
        manager.createMission(easyMission);
        Mission hardMission = sampleHardMissionBuilder().name("easy_mission").build();
        assertThatThrownBy(() -> manager.createMission(hardMission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createMissionWithTooBigRequiredRank() {
        Mission easyMission = sampleEasyMissionBuilder().requiredRank(11).build();
        assertThatThrownBy(() -> manager.createMission(easyMission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createMissionWithZeroRequiredRank() {
        Mission easyMission = sampleEasyMissionBuilder().requiredRank(0).build();
        assertThatThrownBy(() -> manager.createMission(easyMission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createMissionWithNegativeRequiredRank() {
        Mission easyMission = sampleEasyMissionBuilder().requiredRank(-1).build();
        assertThatThrownBy(() -> manager.createMission(easyMission)).isInstanceOf(IllegalArgumentException.class);
    }



    private void updateMission(Consumer<Mission> updateOperation) {
        Mission easyMission = sampleEasyMissionBuilder().build();
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(easyMission);
        manager.createMission(hardMission);

        updateOperation.accept(easyMission);
        manager.updateMission(easyMission);
        assertThat(manager.findMission(easyMission.getId())).isEqualToComparingFieldByField(easyMission);
        assertThat(manager.findMission(hardMission.getId())).isEqualToComparingFieldByField(hardMission);
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
    public void updateMissionRequiredRank() {
        updateMission((mission) -> mission.setRequiredRank(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullMission() {
        manager.updateMission(null);
    }

    @Test
    public void updateMissionWithNullName() {
        Mission easyMission = sampleEasyMissionBuilder().build();
        manager.createMission(easyMission);
        easyMission.setName(null);
        assertThatThrownBy(() -> manager.updateMission(easyMission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateMissionWithEmptyName() {
        Mission easyMission = sampleEasyMissionBuilder().build();
        manager.createMission(easyMission);
        easyMission.setName("");
        assertThatThrownBy(() -> manager.updateMission(easyMission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateMissionWithExistingName() {
        Mission easyMission = sampleEasyMissionBuilder().build();
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(easyMission);
        manager.createMission(hardMission);
        easyMission.setName("hard_mission");
        assertThatThrownBy(() -> manager.updateMission(easyMission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateMissionWithPreviousStatus() {
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(hardMission);
        hardMission.setStatus(MissionStatus.IN_PROGRESS);
        assertThatThrownBy(() -> manager.updateMission(hardMission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateMissionWithTooBigRequiredRank() {
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(hardMission);
        hardMission.setRequiredRank(11);
        assertThatThrownBy(() -> manager.updateMission(hardMission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateMissionWithZeroRequiredRank() {
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(hardMission);
        hardMission.setRequiredRank(0);
        assertThatThrownBy(() -> manager.updateMission(hardMission)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateMissionWithNegativeRequiredRank() {
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(hardMission);
        hardMission.setRequiredRank(-1);
        assertThatThrownBy(() -> manager.updateMission(hardMission)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void deleteMission() {
        Mission easyMission = sampleEasyMissionBuilder().build();
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(easyMission);
        manager.createMission(hardMission);

        assertThat(manager.findMission(easyMission.getId())).isNotNull();
        assertThat(manager.findMission(hardMission.getId())).isNotNull();
        manager.deleteMission(easyMission);
        assertThat(manager.findMission(easyMission.getId())).isNull();
        assertThat(manager.findMission(hardMission.getId())).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullMission() {
        manager.deleteMission(null);
    }

    @Test
    public void deleteNonexistentMission() {
        Mission hardMission = sampleHardMissionBuilder().build();
        assertThatThrownBy(() -> manager.deleteMission(hardMission)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void findAvailableMissions() {
        assertThat(manager.findAllMissions()).isEmpty();

        Mission easyMission = sampleEasyMissionBuilder().build();
        Mission hardMission = sampleHardMissionBuilder().status(MissionStatus.NOT_ASSIGNED).build();
        manager.createMission(easyMission);
        manager.createMission(hardMission);

        assertThat(manager.findAvailableMissions())
                .usingFieldByFieldElementComparator()
                .containsOnly(hardMission);
    }

    @Test
    public void findAllMissions() {
        assertThat(manager.findAllMissions()).isEmpty();

        Mission easyMission = sampleEasyMissionBuilder().build();
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(easyMission);
        manager.createMission(hardMission);

        assertThat(manager.findAllMissions())
                .usingFieldByFieldElementComparator()
                .containsOnly(easyMission,hardMission);
    }
}
