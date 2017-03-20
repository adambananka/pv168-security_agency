package cz.muni.fi.pv168.app;

import cz.muni.fi.pv168.app.agent.Agent;
import cz.muni.fi.pv168.app.mission.Mission;
import cz.muni.fi.pv168.app.mission.MissionStatus;

/**
 * @author Adam Baňanka
 */
public class MissionBuilder {
    private Long id;
    private String name;
    private Agent agent;
    private MissionStatus status = MissionStatus.NOT_ASSIGNED;
    private int requiredRank;

    public MissionBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public MissionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MissionBuilder agent(Agent agent) {
        this.agent = agent;
        return this;
    }

    public MissionBuilder status(MissionStatus status) {
        this.status = status;
        return this;
    }

    public MissionBuilder requiredRank(int requiredRank) {
        this.requiredRank = requiredRank;
        return this;
    }

    public Mission build() {
        Mission mission = new Mission();
        mission.setId(id);
        mission.setName(name);
        mission.setAgent(agent);
        mission.setStatus(status);
        mission.setRequiredRank(requiredRank);
        return mission;
    }
}
