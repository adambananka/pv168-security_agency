package cz.muni.fi.pv168.backend;

import cz.muni.fi.pv168.backend.mission.Mission;
import cz.muni.fi.pv168.backend.mission.MissionStatus;

/**
 * @author Adam Baňanka
 */
public class MissionBuilder {
    private Long id;
    private String name;
    private Long agentId;
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

    public MissionBuilder agentId(Long agentId) {
        this.agentId = agentId;
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

    /**
     * Creates new instance of {@link Mission} with configured properties.
     *
     * @return new instance of {@link Mission} with configured properties.
     */
    public Mission build() {
        Mission mission = new Mission();
        mission.setId(id);
        mission.setName(name);
        mission.setAgentId(agentId);
        mission.setStatus(status);
        mission.setRequiredRank(requiredRank);
        return mission;
    }
}
