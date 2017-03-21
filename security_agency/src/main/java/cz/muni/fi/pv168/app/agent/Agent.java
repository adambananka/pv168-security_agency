package cz.muni.fi.pv168.app.agent;

/**
 *
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
        return "Agent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alive=" + alive +
                '}';
    }
}
