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

    private AgentBuilder sampleAgentBuilder1() {
        return new AgentBuilder()
                .id(null)
                .name("Superman")
                .alive(true);
    }

    private AgentBuilder sampleAgentBuilder2() {
        return new AgentBuilder()
                .id(null)
                .name("JackSparrow")
                .alive(true);
    }

    @Test
    public void createAgent() {
        Agent agent = sampleAgentBuilder1().build();
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
        Agent agent = sampleAgentBuilder1()
                .name(null)
                .build();
        assertThatThrownBy(() -> manager.createAgent(agent))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createAgentWithExistingName() {
        Agent agent1 = sampleAgentBuilder1().build();
        manager.createAgent(agent1);
        Agent agent2 = sampleAgentBuilder2()
                .name("Superman")
                .build();
        assertThatThrownBy(() -> manager.createAgent(agent2)).isInstanceOf(IllegalArgumentException.class);
    }

    private void updateAgent(Consumer<Agent> updateOperation) {
        Agent mainAgent = sampleAgentBuilder1().build();
        Agent anotherAgent = sampleAgentBuilder2().build();
        manager.createAgent(mainAgent);
        manager.createAgent(anotherAgent);

        updateOperation.accept(mainAgent);

        manager.updateAgent(mainAgent);
        assertThat(manager.findAgent(mainAgent.getId()))
                .isEqualToComparingFieldByField(mainAgent);
        assertThat(manager.findAgent(anotherAgent.getId()))
                .isEqualToComparingFieldByField(anotherAgent);
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
        Agent agent = sampleAgentBuilder1().build();
        manager.createAgent(agent);
        agent.setName(null);
        assertThatThrownBy(() -> manager.updateAgent(agent)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateAgentWithExistingName() {
        Agent agent = sampleAgentBuilder1().build();
        Agent anotherAgent = sampleAgentBuilder2().build();
        manager.createAgent(agent);
        manager.createAgent(anotherAgent);
        agent.setName("JackSparrow");
        assertThatThrownBy(() -> manager.updateAgent(agent)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteAgent() {

        Agent agent1 = sampleAgentBuilder1().build();
        Agent agent2 = sampleAgentBuilder2().build();
        manager.createAgent(agent1);
        manager.createAgent(agent2);

        assertThat(manager.findAgent(agent1.getId())).isNotNull();
        assertThat(manager.findAgent(agent2.getId())).isNotNull();

        manager.deleteAgent(agent1);

        assertThat(manager.findAgent(agent1.getId())).isNull();
        assertThat(manager.findAgent(agent2.getId())).isNotNull();

    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullAgent() {
        manager.deleteAgent(null);
    }

    @Test
    public void findAllAgents() {

        assertThat(manager.findAllAgents()).isEmpty();

        Agent a1 = sampleAgentBuilder1().build();
        Agent a2 = sampleAgentBuilder2().build();

        manager.createAgent(a1);
        manager.createAgent(a2);

        assertThat(manager.findAllAgents())
                .usingFieldByFieldElementComparator()
                .containsOnly(a1,a2);

    }

/*
    @Test
    public void findFreeAgents() {
    }
*/
}
