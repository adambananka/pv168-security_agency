package cz.muni.fi.pv168.app;

import cz.muni.fi.pv168.app.agent.Agent;
import cz.muni.fi.pv168.app.agent.AgentManagerImpl;
import cz.muni.fi.pv168.app.common.IllegalEntityException;
import cz.muni.fi.pv168.app.common.ServiceFailureException;
import cz.muni.fi.pv168.app.common.ValidationException;
import cz.muni.fi.pv168.app.mission.Mission;
import cz.muni.fi.pv168.app.mission.MissionManagerImpl;
import cz.muni.fi.pv168.app.mission.MissionStatus;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class implement AgencyManager service
 *
 * @author Adam Baňanka, Daniel Homola
 */
public class AgencyManagerImpl implements AgencyManager {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void assignAgent(Agent agent, Mission mission) {
        checkDataSource();
        validate(agent, mission);
        try (Connection conn = dataSource.getConnection()){
            try(PreparedStatement st = conn.prepareStatement("UPDATE Mission SET agentId = ?, status = ? WHERE id = ?")) {
                conn.setAutoCommit(false);
                st.setLong(1, agent.getId());
                mission.setAgentId(agent.getId());
                st.setString(2, MissionStatus.IN_PROGRESS.toString());
                mission.setStatus(MissionStatus.IN_PROGRESS);
                st.setLong(3, mission.getId());

                int count = st.executeUpdate();
                if (count == 0) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    throw new IllegalEntityException(mission + "not in DB");
                }
                conn.commit();
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when assigning agent on mission", ex);
        }
    }

    @Override
    public List<Mission> findMissionsOfAgent(Agent agent) {
        checkDataSource();
        checkAgent(agent);

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM Mission WHERE agentId = ?")) {
                st.setLong(1, agent.getId());
                return MissionManagerImpl.executeQueryForMultipleMissions(st);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when getting missions of agent from DB", ex);
        }
    }

    @Override
    public List<Agent> findAvailableAgents() {
        checkDataSource();
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT Agent.id, Agent.name, rank, alive " +
                    "FROM Agent LEFT JOIN Mission ON Agent.id = Mission.agentId " +
                    "WHERE Agent.alive = ? AND Mission.status IS NULL OR Mission.status IN (?, ?)")) {
                st.setBoolean(1, true);
                st.setString(2, MissionStatus.ACCOMPLISHED.toString());
                st.setString(3, MissionStatus.FAILED.toString());
                return AgentManagerImpl.executeQueryForMultipleAgents(st);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when getting available agents from DB", ex);
        }
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    private void checkAgent(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (agent.getId() == null) {
            throw new IllegalEntityException("agent has null id");
        }
    }

    private void validate(Agent agent, Mission mission) {
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        checkAgent(agent);
        if (mission.getId() == null) {
            throw new IllegalEntityException("mission has null id");
        }
        if (mission.getStatus() != MissionStatus.NOT_ASSIGNED) {
            throw new IllegalEntityException("mission already assigned");
        }
        checkAgentInDb(agent);
        if (!agent.isAlive()) {
            throw new ValidationException("agent is dead");
        }
        if (agent.getRank() < mission.getRequiredRank()) {
            throw new ValidationException("agent's rank is too low for this mission");
        }
        for (Mission m : findMissionsOfAgent(agent)) {
            if (m.getStatus() == MissionStatus.IN_PROGRESS) {
                throw new IllegalEntityException("agent already on mission");
            }
        }
    }

    private void checkAgentInDb(Agent agent) {
        try (Connection conn = dataSource.getConnection()){
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM Agent WHERE id = ?")) {
                st.setLong(1, agent.getId());
                ResultSet rs = st.executeQuery();
                if (!rs.next()) {
                    throw new IllegalEntityException(agent + "agent not in DB");
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when getting agent from DB", ex);
        }
    }
}