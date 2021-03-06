package cz.muni.fi.pv168.backend;

import cz.muni.fi.pv168.backend.agent.Agent;

/**
 * @author Daniel Homola
 */
public class AgentBuilder {
    private Long id;
    private String name;
    private int rank;
    private boolean alive;


    public AgentBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public AgentBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AgentBuilder rank(int rank) {
        this.rank = rank;
        return this;
    }

    public AgentBuilder alive(boolean alive) {
        this.alive = alive;
        return this;
    }

    /**
     * Creates new instance of {@link Agent} with configured properties.
     *
     * @return new instance of {@link Agent} with configured properties.
     */
    public Agent build() {
        Agent agent = new Agent();
        agent.setId(id);
        agent.setName(name);
        agent.setRank(rank);
        agent.setAlive(alive);
        return agent;
    }
}
