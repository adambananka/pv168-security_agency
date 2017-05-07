package cz.muni.fi.pv168.backend;

import cz.muni.fi.pv168.backend.common.DBUtils;
import cz.muni.fi.pv168.backend.common.IllegalEntityException;
import cz.muni.fi.pv168.backend.common.ServiceFailureException;
import cz.muni.fi.pv168.backend.common.ValidationException;
import cz.muni.fi.pv168.backend.mission.Mission;
import cz.muni.fi.pv168.backend.mission.MissionManager;
import cz.muni.fi.pv168.backend.mission.MissionManagerImpl;
import cz.muni.fi.pv168.backend.mission.MissionStatus;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Adam Baňanka
 */
public class MissionManagerImplTest {

    private MissionManagerImpl manager;
    private DataSource dataSource;

    //--------------------------------------------------------------------------
    // Test initialization
    //--------------------------------------------------------------------------

    private static DataSource prepareDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:agencymanager-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource,AgencyManager.class.getResource("createTables.sql"));
        manager = new MissionManagerImpl();
        manager.setDataSource(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource,AgencyManager.class.getResource("dropTables.sql"));
    }

    //--------------------------------------------------------------------------
    // Preparing test data
    //--------------------------------------------------------------------------

    private MissionBuilder sampleEasyMissionBuilder() {
        return new MissionBuilder()
                .id(null)
                .name("easy_mission")
                .agentId(0L)
                .status(MissionStatus.IN_PROGRESS)
                .requiredRank(2);
    }

    private MissionBuilder sampleHardMissionBuilder() {
        return new MissionBuilder()
                .id(null)
                .name("hard_mission")
                .agentId(0L)
                .status(MissionStatus.FAILED)
                .requiredRank(7);
    }

    //--------------------------------------------------------------------------
    // Tests for MissionManager.createMission(Mission) operation
    //--------------------------------------------------------------------------

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
    public void createMissionWithSetId() {
        Mission easyMission = sampleEasyMissionBuilder().id(1L).build();
        assertThatThrownBy(() -> manager.createMission(easyMission))
                .isInstanceOf(IllegalEntityException.class);
    }

    @Test
    public void createMissionWithNullName() {
        Mission easyMission = sampleEasyMissionBuilder().name(null).build();
        assertThatThrownBy(() -> manager.createMission(easyMission))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createMissionWithEmptyName() {
        Mission easyMission = sampleEasyMissionBuilder().name("").build();
        assertThatThrownBy(() -> manager.createMission(easyMission))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createMissionWithExistingName() {
        Mission easyMission = sampleEasyMissionBuilder().build();
        manager.createMission(easyMission);
        Mission hardMission = sampleHardMissionBuilder().name("easy_mission").build();
        assertThatThrownBy(() -> manager.createMission(hardMission))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createMissionWithTooBigRequiredRank() {
        Mission easyMission = sampleEasyMissionBuilder().requiredRank(11).build();
        assertThatThrownBy(() -> manager.createMission(easyMission))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createMissionWithZeroRequiredRank() {
        Mission easyMission = sampleEasyMissionBuilder().requiredRank(0).build();
        assertThatThrownBy(() -> manager.createMission(easyMission))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createMissionWithNegativeRequiredRank() {
        Mission easyMission = sampleEasyMissionBuilder().requiredRank(-1).build();
        assertThatThrownBy(() -> manager.createMission(easyMission))
                .isInstanceOf(ValidationException.class);
    }

    //--------------------------------------------------------------------------
    // Tests for MissionManager.updateMission(Mission) operation
    //--------------------------------------------------------------------------

    private void updateMission(Consumer<Mission> updateOperation) {
        Mission easyMission = sampleEasyMissionBuilder().build();
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(easyMission);
        manager.createMission(hardMission);

        updateOperation.accept(easyMission);
        manager.updateMission(easyMission);
        assertThat(manager.findMission(easyMission.getId()))
                .isEqualToComparingFieldByField(easyMission);
        assertThat(manager.findMission(hardMission.getId()))
                .isEqualToComparingFieldByField(hardMission);
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
    public void updateMissionWithNullId() {
        Mission easyMission = sampleEasyMissionBuilder().id(null).build();
        assertThatThrownBy(() -> manager.updateMission(easyMission))
                .isInstanceOf(IllegalEntityException.class);
    }

    @Test
    public void updateMissionWithNonExistingId() {
        Mission easyMission = sampleEasyMissionBuilder().id(1L).build();
        assertThatThrownBy(() -> manager.updateMission(easyMission))
                .isInstanceOf(IllegalEntityException.class);
    }

    @Test
    public void updateMissionWithNullName() {
        Mission easyMission = sampleEasyMissionBuilder().build();
        manager.createMission(easyMission);
        easyMission.setName(null);
        assertThatThrownBy(() -> manager.updateMission(easyMission))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateMissionWithEmptyName() {
        Mission easyMission = sampleEasyMissionBuilder().build();
        manager.createMission(easyMission);
        easyMission.setName("");
        assertThatThrownBy(() -> manager.updateMission(easyMission))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateMissionWithExistingName() {
        Mission easyMission = sampleEasyMissionBuilder().build();
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(easyMission);
        manager.createMission(hardMission);
        easyMission.setName("hard_mission");
        assertThatThrownBy(() -> manager.updateMission(easyMission))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateMissionWithPreviousStatus() {
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(hardMission);
        hardMission.setStatus(MissionStatus.IN_PROGRESS);
        assertThatThrownBy(() -> manager.updateMission(hardMission))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateMissionWithTooBigRequiredRank() {
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(hardMission);
        hardMission.setRequiredRank(11);
        assertThatThrownBy(() -> manager.updateMission(hardMission))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateMissionWithZeroRequiredRank() {
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(hardMission);
        hardMission.setRequiredRank(0);
        assertThatThrownBy(() -> manager.updateMission(hardMission))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateMissionWithNegativeRequiredRank() {
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(hardMission);
        hardMission.setRequiredRank(-1);
        assertThatThrownBy(() -> manager.updateMission(hardMission))
                .isInstanceOf(ValidationException.class);
    }

    //--------------------------------------------------------------------------
    // Tests for MissionManager.deleteMission(Mission) operation
    //--------------------------------------------------------------------------

    @Test
    public void deleteMission() {
        Mission easyMission = sampleEasyMissionBuilder().build();
        Mission hardMission = sampleHardMissionBuilder().build();
        manager.createMission(easyMission);
        manager.createMission(hardMission);

        assertThat(manager.findMission(easyMission.getId()))
                .isNotNull();
        assertThat(manager.findMission(hardMission.getId()))
                .isNotNull();
        manager.deleteMission(easyMission);
        assertThat(manager.findMission(easyMission.getId()))
                .isNull();
        assertThat(manager.findMission(hardMission.getId()))
                .isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullMission() {
        manager.deleteMission(null);
    }

    @Test
    public void deleteMissionWithNullId() {
        Mission hardMission = sampleHardMissionBuilder().id(null).build();
        assertThatThrownBy(() -> manager.deleteMission(hardMission))
                .isInstanceOf(IllegalEntityException.class);
    }

    @Test
    public void deleteMissionWithNonExistingId() {
        Mission hardMission = sampleHardMissionBuilder().id(1L).build();
        assertThatThrownBy(() -> manager.deleteMission(hardMission))
                .isInstanceOf(IllegalEntityException.class);
    }

    @Test
    public void deleteNonexistentMission() {
        Mission hardMission = sampleHardMissionBuilder().build();
        assertThatThrownBy(() -> manager.deleteMission(hardMission))
                .isInstanceOf(IllegalEntityException.class);
    }

    //--------------------------------------------------------------------------
    // Tests for find* operations of MissionManager
    //--------------------------------------------------------------------------

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

    //--------------------------------------------------------------------------
    // Tests if MissionManager methods throws ServiceFailureException in case of
    // DB operation failure
    //--------------------------------------------------------------------------

    private void testExpectedServiceFailureException(Consumer<MissionManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        manager.setDataSource(failingDataSource);
        assertThatThrownBy(() -> operation.accept(manager))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }

    @Test
    public void createMissionWithSqlExceptionThrown() throws SQLException {
        Mission mission = sampleEasyMissionBuilder().build();
        testExpectedServiceFailureException((missionManager) -> missionManager.createMission(mission));
    }

    @Test
    public void updateMissionWithSqlExceptionThrown() throws SQLException {
        Mission mission = sampleEasyMissionBuilder().build();
        manager.createMission(mission);
        testExpectedServiceFailureException((missionManager) -> missionManager.updateMission(mission));
    }

    @Test
    public void deleteMissionWithSqlExceptionThrown() throws SQLException {
        Mission mission = sampleEasyMissionBuilder().build();
        manager.createMission(mission);
        testExpectedServiceFailureException((missionManager) -> missionManager.deleteMission(mission));
    }

    @Test
    public void findMissionWithSqlExceptionThrown() throws SQLException {
        Mission mission = sampleEasyMissionBuilder().build();
        manager.createMission(mission);
        testExpectedServiceFailureException((missionManager) -> missionManager.findMission(mission.getId()));
    }

    @Test
    public void findAllMissionWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((missionManager) -> missionManager.findAllMissions());
    }

    @Test
    public void findAvailableMissionWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((missionManager) -> missionManager.findAvailableMissions());
    }
}
