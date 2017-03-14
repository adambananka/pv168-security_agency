package cz.muni.fi.pv168.app.mission;

import cz.muni.fi.pv168.app.agent.Agent;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public class Mission {
    private Long id;
    private String name;
    private Agent agent;
    private MissionStatus status;
    private String info;

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

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", agent=" + agent +
                ", status=" + status +
                ", info='" + info + '\'' +
                '}';
    }
}
