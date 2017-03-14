package cz.muni.fi.pv168.app;

import cz.muni.fi.pv168.app.agent.Agent;

/**
 * @author Daniel Homola
 */
public class AgentBuilder {
    private Long id;
    private String name;
    private boolean alive;


    public AgentBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public AgentBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AgentBuilder alive(boolean alive) {
        this.alive = alive;
        return this;
    }

    public Agent build() {
        Agent agent = new Agent();
        agent.setId(id);
        agent.setName(name);
        agent.setAlive(alive);
        return agent;
    }
}
