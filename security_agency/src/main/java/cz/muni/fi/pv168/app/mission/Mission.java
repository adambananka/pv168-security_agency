package cz.muni.fi.pv168.app.mission;

/**
 * This entity class represents Mission. Mission have some name, status
 * and required rank, that agent needs, to be assigned to the mission.
 * One mission can have assigned maximum one agent.
 * Not assigned agent is represented by value 0L in agentId.
 *
 * @author Adam Baňanka, Daniel Homola
 */
public class Mission {
    private Long id = null;
    private String name;
    private Long agentId = 0L;
    private MissionStatus status;
    private int requiredRank;

    public Mission() {
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

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public int getRequiredRank() {
        return requiredRank;
    }

    public void setRequiredRank(int requiredRank) {
        this.requiredRank = requiredRank;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", agentId=" + agentId +
                ", status=" + status +
                ", requiredRank=" + requiredRank +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mission mission = (Mission) o;

        return id != null ? id.equals(mission.id) : mission.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
