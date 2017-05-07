package cz.muni.fi.pv168.backend;

import cz.muni.fi.pv168.backend.agent.Agent;
import cz.muni.fi.pv168.backend.agent.AgentManagerImpl;
import cz.muni.fi.pv168.backend.common.DBUtils;
import cz.muni.fi.pv168.backend.common.IllegalEntityException;
import cz.muni.fi.pv168.backend.common.ServiceFailureException;
import cz.muni.fi.pv168.backend.common.ValidationException;
import cz.muni.fi.pv168.backend.mission.Mission;
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
 * @author Adam Baňanka, Daniel Homola
 */
public class AgencyManagerImplTest {
    private AgencyManagerImpl manager;
    private AgentManagerImpl agentManager;
    private MissionManagerImpl missionManager;
    private DataSource dataSource;

    private Agent flash, superman, batman, agentWithNullId, agentNotInDb;
    private Mission easyMission, mainMission, hardMission, missionWithNullId, missionNotInDb;


    private static DataSource prepareDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:agencymanager-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void setup() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource,AgencyManager.class.getResource("createTables.sql"));
        manager = new AgencyManagerImpl();
        manager.setDataSource(dataSource);
        agentManager = new AgentManagerImpl();
        agentManager.setDataSource(dataSource);
        missionManager = new MissionManagerImpl();
        missionManager.setDataSource(dataSource);
        prepareTestData();
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource,AgencyManager.class.getResource("dropTables.sql"));
    }

    private void prepareTestData() {
        flash = new AgentBuilder().name("Flash").rank(10).alive(true).build();
        superman = new AgentBuilder().name("Superman").rank(7).alive(false).build();
        batman = new AgentBuilder().name("Batman").rank(3).alive(true).build();

        easyMission = new MissionBuilder().name("EasyMission").status(MissionStatus.NOT_ASSIGNED)
                .requiredRank(1).build();
        mainMission = new MissionBuilder().name("MainMission").status(MissionStatus.NOT_ASSIGNED)
                .requiredRank(5).build();
        hardMission = new MissionBuilder().name("HardMission").status(MissionStatus.NOT_ASSIGNED)
                .requiredRank(9).build();

        agentManager.createAgent(flash);
        agentManager.createAgent(superman);
        agentManager.createAgent(batman);

        missionManager.createMission(easyMission);
        missionManager.createMission(mainMission);
        missionManager.createMission(hardMission);

        agentWithNullId = new AgentBuilder().id(null).build();
        agentNotInDb = new AgentBuilder().id(batman.getId() + 100).build();
        assertThat(agentManager.findAgent(agentNotInDb.getId()))
                .isNull();

        missionWithNullId = new MissionBuilder().name("Mission_with_null_id").id(null).build();
        missionNotInDb = new MissionBuilder().name("Mission_not_in_DB").id(hardMission.getId() + 100).build();
        assertThat(missionManager.findMission(missionNotInDb.getId()))
                .isNull();
    }

    //--------------------------------------------------------------------------
    // Tests for AgencyManager.AssignAgent(Agent, Mission) operation
    //--------------------------------------------------------------------------

    @Test
    public void assignAgent() {
        assertThat(easyMission.getAgentId()).isNull();
        assertThat(mainMission.getAgentId()).isNull();
        assertThat(hardMission.getAgentId()).isNull();

        manager.assignAgent(flash, mainMission);
        manager.assignAgent(batman, easyMission);

        assertThat(manager.findMissionsOfAgent(flash))
                .usingFieldByFieldElementComparator()
                .containsOnly(mainMission);
        assertThat(manager.findMissionsOfAgent(superman))
                .isEmpty();
        assertThat(manager.findMissionsOfAgent(batman))
                .usingFieldByFieldElementComparator()
                .containsOnly(easyMission);

        assertThat(easyMission.getAgentId())
                .isEqualTo(batman.getId());
        assertThat(mainMission.getAgentId())
                .isEqualTo(flash.getId());
        assertThat(hardMission.getAgentId())
                .isNull();
    }

    @Test
    public void assignAgentAlreadyOnMission() {
        manager.assignAgent(flash, mainMission);
        assertThatThrownBy(() -> manager.assignAgent(flash, easyMission))
                .isInstanceOf(IllegalEntityException.class);
    }

    @Test
    public void assignAgentToAssignedMission() {
        manager.assignAgent(batman, easyMission);
        assertThatThrownBy(() -> manager.assignAgent(flash, easyMission))
                .isInstanceOf(IllegalEntityException.class);
    }

    @Test(expected = ValidationException.class)
    public void assignDeadAgent() {
        manager.assignAgent(superman, easyMission);
    }


    @Test(expected = ValidationException.class)
    public void assignAgentWithLowRank() {
        manager.assignAgent(batman, hardMission);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assignNullAgent() {
        manager.assignAgent(null, hardMission);
    }

    @Test(expected = IllegalEntityException.class)
    public void assignAgentWithNullId() {
        manager.assignAgent(agentWithNullId, hardMission);
    }

    @Test(expected = IllegalEntityException.class)
    public void assignAgentNotInDB() {
        manager.assignAgent(agentNotInDb, hardMission);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assignAgentToNullMission() {
        manager.assignAgent(flash, null);
    }

    @Test(expected = IllegalEntityException.class)
    public void assignAgentToMissionWithNullId() {
        manager.assignAgent(flash, missionWithNullId);
    }

    @Test(expected = IllegalEntityException.class)
    public void assignAgentToMissionNotInDB() {
        manager.assignAgent(flash, missionNotInDb);
    }

    //--------------------------------------------------------------------------
    // Tests for find* operations of AgencyManager
    //--------------------------------------------------------------------------

    @Test
    public void findMissionsOfAgent() {
        assertThat(manager.findMissionsOfAgent(flash))
                .isEmpty();

        manager.assignAgent(flash, easyMission);
        assertThat(manager.findMissionsOfAgent(flash))
                .usingFieldByFieldElementComparator()
                .containsOnly(easyMission);

        easyMission.setStatus(MissionStatus.ACCOMPLISHED);
        missionManager.updateMission(easyMission);
        assertThat(manager.findMissionsOfAgent(flash))
                .usingFieldByFieldElementComparator()
                .containsOnly(easyMission);

        manager.assignAgent(flash, hardMission);
        assertThat(manager.findMissionsOfAgent(flash))
                .usingFieldByFieldElementComparator()
                .containsOnly(easyMission, hardMission);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findMissionsOfNullAgent() {
        manager.findMissionsOfAgent(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void findMissionsOfAgentWithNullId() {
        manager.findMissionsOfAgent(agentWithNullId);
    }

    @Test
    public void findAvailableAgents() {
        assertThat(manager.findAvailableAgents())
                .usingFieldByFieldElementComparator()
                .containsOnly(flash, batman);

        manager.assignAgent(batman, easyMission);
        manager.assignAgent(flash, mainMission);
        assertThat(manager.findAvailableAgents())
                .isEmpty();

        mainMission.setStatus(MissionStatus.ACCOMPLISHED);
        missionManager.updateMission(mainMission);
        assertThat(manager.findAvailableAgents())
                .usingFieldByFieldElementComparator()
                .containsOnly(flash);

        batman.setAlive(false);
        assertThat(easyMission.getStatus().equals(MissionStatus.FAILED));
        assertThat(manager.findAvailableAgents())
                .usingFieldByFieldElementComparator()
                .containsOnly(flash);
    }

    //--------------------------------------------------------------------------
    // Tests if AgencyManager methods throws ServiceFailureException in case of
    // DB operation failure
    //--------------------------------------------------------------------------

    private void testExpectedServiceFailureException(Consumer<AgencyManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        manager.setDataSource(failingDataSource);
        assertThatThrownBy(() -> operation.accept(manager))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }

    @Test
    public void assignAgentWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((agencyManager) -> agencyManager.assignAgent(flash, mainMission));
    }

    @Test
    public void findMissionsOfAgentWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((agencyManager) -> agencyManager.findMissionsOfAgent(flash));
    }

    @Test
    public void findAvailableAgentsWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((agencyManager) -> agencyManager.findAvailableAgents());
    }
}
