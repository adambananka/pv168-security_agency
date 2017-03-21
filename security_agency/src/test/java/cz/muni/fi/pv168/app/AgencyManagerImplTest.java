package cz.muni.fi.pv168.app;

import cz.muni.fi.pv168.app.agent.Agent;
import cz.muni.fi.pv168.app.agent.AgentManagerImpl;
import cz.muni.fi.pv168.app.common.IllegalEntityException;
import cz.muni.fi.pv168.app.common.ValidationException;
import cz.muni.fi.pv168.app.mission.Mission;
import cz.muni.fi.pv168.app.mission.MissionManagerImpl;
import cz.muni.fi.pv168.app.mission.MissionStatus;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public class AgencyManagerImplTest {
    private AgencyManagerImpl manager = new AgencyManagerImpl();
    private AgentManagerImpl agentManager = new AgentManagerImpl();
    private MissionManagerImpl missionManager = new MissionManagerImpl();

    private Agent flash, superman, batman, dano, adam, agentWithNullId, agentNotInDb;
    private Mission easyMission, secondaryMission, mainMission, hardMission, missionWithNullId, missionNotInDb;

    private void prepareTestData() {
        flash = new AgentBuilder().name("Flash").rank(10).alive(true).build();
        superman = new AgentBuilder().name("Superman").rank(7).alive(true).build();
        batman = new AgentBuilder().name("Batman").rank(3).alive(true).build();
        dano = new AgentBuilder().name("Dano").rank(1).alive(false).build();
        adam = new AgentBuilder().name("Adam").rank(1).alive(true).build();

        easyMission = new MissionBuilder().name("EasyMission").status(MissionStatus.NOT_ASSIGNED)
                .requiredRank(1).build();
        secondaryMission = new MissionBuilder().name("SecondaryMission").status(MissionStatus.NOT_ASSIGNED)
                .requiredRank(3).build();
        mainMission = new MissionBuilder().name("MainMission").status(MissionStatus.NOT_ASSIGNED)
                .requiredRank(5).build();
        hardMission = new MissionBuilder().name("HardMission").status(MissionStatus.NOT_ASSIGNED)
                .requiredRank(9).build();

        agentManager.createAgent(flash);
        agentManager.createAgent(superman);
        agentManager.createAgent(batman);
        agentManager.createAgent(dano);
        agentManager.createAgent(adam);

        missionManager.createMission(easyMission);
        missionManager.createMission(secondaryMission);
        missionManager.createMission(mainMission);
        missionManager.createMission(hardMission);

        agentWithNullId = new AgentBuilder().id(null).build();
        agentNotInDb = new AgentBuilder().id(dano.getId() + 100).build();
        assertThat(agentManager.findAgent(agentNotInDb.getId())).isNull();

        missionWithNullId = new MissionBuilder().name("Mission_with_null_id").id(null).build();
        missionNotInDb = new MissionBuilder().name("Mission_not_in_DB").id(hardMission.getId() + 100).build();
        assertThat(missionManager.findMission(missionNotInDb.getId())).isNull();
    }

    @Before
    public void setup() {
        prepareTestData();
    }

    @Test
    public void assignAgent() {
        assertThat(easyMission.getAgent()).isNull();
        assertThat(secondaryMission.getAgent()).isNull();
        assertThat(mainMission.getAgent()).isNull();
        assertThat(hardMission.getAgent()).isNull();

        manager.assignAgent(superman, mainMission);
        manager.assignAgent(batman, secondaryMission);

        assertThat(manager.findMissionsOfAgent(flash)).isEmpty();
        assertThat(manager.findMissionsOfAgent(superman))
                .usingFieldByFieldElementComparator()
                .containsOnly(mainMission);
        assertThat(manager.findMissionsOfAgent(batman))
                .usingFieldByFieldElementComparator()
                .containsOnly(secondaryMission);
        assertThat(manager.findMissionsOfAgent(dano)).isEmpty();
        assertThat(manager.findMissionsOfAgent(adam)).isEmpty();

        assertThat(easyMission.getAgent()).isNull();
        assertThat(secondaryMission.getAgent()).isEqualToComparingFieldByField(batman);
        assertThat(mainMission.getAgent()).isEqualToComparingFieldByField(superman);
        assertThat(hardMission.getAgent()).isNull();
    }

    @Test
    public void assignAgentAlreadyOnMission() {
        manager.assignAgent(superman, secondaryMission);
        assertThatThrownBy(() -> manager.assignAgent(superman, easyMission)).isInstanceOf(IllegalEntityException.class);
    }

    @Test
    public void assignAgentToAssignedMission() {
        manager.assignAgent(superman, mainMission);
        assertThatThrownBy(() -> manager.assignAgent(flash, mainMission)).isInstanceOf(IllegalEntityException.class);
    }

    @Test(expected = ValidationException.class)
    public void assignDeadAgent() {
        manager.assignAgent(dano, easyMission);
    }


    @Test(expected = ValidationException.class)
    public void assignAgentWithLowRank() {
        manager.assignAgent(adam, hardMission);
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


    @Test
    public void findMissionsOfAgent() {
        assertThat(manager.findMissionsOfAgent(flash)).isEmpty();

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
                .containsOnly(flash, superman, batman, adam);

        manager.assignAgent(batman, secondaryMission);
        manager.assignAgent(superman, mainMission);
        assertThat(manager.findAvailableAgents())
                .usingFieldByFieldElementComparator()
                .containsOnly(flash, adam);

        secondaryMission.setStatus(MissionStatus.ACCOMPLISHED);
        missionManager.updateMission(secondaryMission);
        assertThat(manager.findAvailableAgents())
                .usingFieldByFieldElementComparator()
                .containsOnly(flash, adam, batman);

        adam.setAlive(false);
        assertThat(mainMission.getStatus().equals(MissionStatus.FAILED));
        assertThat(manager.findAvailableAgents())
                .usingFieldByFieldElementComparator()
                .containsOnly(flash, batman);
    }
}
