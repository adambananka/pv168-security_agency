package cz.muni.fi.pv168.app.mission;

import cz.muni.fi.pv168.app.agent.Agent;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public class Mission {
    private long id;
    private String name;
    private Agent agent;
    private MissionStatus status;
    private String info;

    public Mission() {
    }
}
