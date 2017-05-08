package cz.muni.fi.pv168.backend.agent;

/**
 * This entity represents Agent. Agent has name, rank and
 * and flag, if it is alive. All attributes are mandatory.
 *
 * @author Adam Baňanka, Daniel Homola
 */
public class Agent {
    private Long id;
    private String name;
    private int rank;
    private boolean alive;

    public Agent() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public String toString() {
        return name;
        /*return "Agent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alive=" + alive +
                '}';*/
    }

    /**
     * Returns true if obj represents the same agent. Two objects are considered
     * to represent the same agent when both are instances of {@link Agent} class,
     * both have assigned some id and this id is the same.
     *
     *
     * @param o the reference object with which to compare.
     * @return true if obj represents the same agent.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agent agent = (Agent) o;

        return id != null ? id.equals(agent.id) : agent.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
