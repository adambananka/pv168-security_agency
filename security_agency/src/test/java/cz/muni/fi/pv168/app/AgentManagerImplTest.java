package cz.muni.fi.pv168.app;

import cz.muni.fi.pv168.app.agent.Agent;
import cz.muni.fi.pv168.app.agent.AgentManagerImpl;
import org.junit.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Daniel Homola
 */
public class AgentManagerImplTest {

    private AgentManagerImpl manager = new AgentManagerImpl();

    private AgentBuilder supermanBuilder() {
        return new AgentBuilder()
                .id(null)
                .name("Superman")
                .rank(8)
                .alive(true);
    }

    private AgentBuilder jackSparrowBuilder() {
        return new AgentBuilder()
                .id(null)
                .name("JackSparrow")
                .rank(3)
                .alive(true);
    }

    @Test
    public void createAgent() {
        Agent agent = supermanBuilder().build();
        manager.createAgent(agent);

        Long agentId = agent.getId();
        assertThat(agentId).isNotNull();

        assertThat(manager.findAgent(agentId))
                .isNotSameAs(agent)
                .isEqualToComparingFieldByField(agent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullAgent() {
        manager.createAgent(null);
    }

    @Test
    public void createAgentWithNullName() {
        Agent superman = supermanBuilder()
                .name(null)
                .build();
        assertThatThrownBy(() -> manager.createAgent(superman))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createAgentWithExistingName() {
        Agent superman = supermanBuilder().build();
        manager.createAgent(superman);
        Agent jackSparrow = jackSparrowBuilder()
                .name("Superman")
                .build();
        assertThatThrownBy(() -> manager.createAgent(jackSparrow)).isInstanceOf(IllegalArgumentException.class);
    }

    private void updateAgent(Consumer<Agent> updateOperation) {
        Agent superman = supermanBuilder().build();
        Agent jackSparrow = jackSparrowBuilder().build();
        manager.createAgent(superman);
        manager.createAgent(jackSparrow);

        updateOperation.accept(superman);

        manager.updateAgent(superman);
        assertThat(manager.findAgent(superman.getId()))
                .isEqualToComparingFieldByField(superman);
        assertThat(manager.findAgent(jackSparrow.getId()))
                .isEqualToComparingFieldByField(jackSparrow);
    }

    @Test
    public void updateAgentName() {
        updateAgent((agent) -> agent.setName("Joker"));
    }

    @Test
    public void updateAliveStatus() {
        updateAgent((agent) -> agent.setAlive(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullAgent() {
        manager.updateAgent(null);
    }

    @Test
    public void updateAgentWithNullName() {
        Agent agent = supermanBuilder().build();
        manager.createAgent(agent);
        agent.setName(null);
        assertThatThrownBy(() -> manager.updateAgent(agent)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateAgentWithExistingName() {
        Agent agent = supermanBuilder().build();
        Agent anotherAgent = jackSparrowBuilder().build();
        manager.createAgent(agent);
        manager.createAgent(anotherAgent);
        agent.setName("JackSparrow");
        assertThatThrownBy(() -> manager.updateAgent(agent)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteAgent() {

        Agent superman = supermanBuilder().build();
        Agent jackSparrow = jackSparrowBuilder().build();
        manager.createAgent(superman);
        manager.createAgent(jackSparrow);

        assertThat(manager.findAgent(superman.getId())).isNotNull();
        assertThat(manager.findAgent(jackSparrow.getId())).isNotNull();

        manager.deleteAgent(superman);

        assertThat(manager.findAgent(superman.getId())).isNull();
        assertThat(manager.findAgent(jackSparrow.getId())).isNotNull();

    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullAgent() {
        manager.deleteAgent(null);
    }

    @Test
    public void findAllAgents() {

        assertThat(manager.findAllAgents()).isEmpty();

        Agent superman = supermanBuilder().build();
        Agent jackSparrow = jackSparrowBuilder().build();

        manager.createAgent(superman);
        manager.createAgent(jackSparrow);

        assertThat(manager.findAllAgents())
                .usingFieldByFieldElementComparator()
                .containsOnly(superman,jackSparrow);

    }

/*
    @Test
    public void findAvailableAgents() {
    }
*/
}
