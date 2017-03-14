package cz.muni.fi.pv168.app.agent;

/**
 *
 *
 * @author Adam Baňanka, Daniel Homola
 */
public class Agent {
    private Long id;
    private String name;
    private boolean alive;

    public Agent() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public long getId() {

        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAlive() {
        return alive;
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
