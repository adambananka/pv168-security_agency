package cz.muni.fi.pv168.backend;

import cz.muni.fi.pv168.backend.agent.Agent;
import cz.muni.fi.pv168.backend.agent.AgentManagerImpl;
import cz.muni.fi.pv168.backend.common.IllegalEntityException;
import cz.muni.fi.pv168.backend.common.ServiceFailureException;
import cz.muni.fi.pv168.backend.common.ValidationException;
import cz.muni.fi.pv168.backend.mission.Mission;
import cz.muni.fi.pv168.backend.mission.MissionManagerImpl;
import cz.muni.fi.pv168.backend.mission.MissionStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AgencyManagerImpl.class);

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void assignAgent(Agent agent, Mission mission) {
        logger.info("Assigning agent to mission...");
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
                    String msg = mission + "not in DB.";
                    logger.error(msg);
                    throw new IllegalEntityException(msg);
                }
                conn.commit();
                conn.setAutoCommit(true);
                logger.info("Agent successfully assigned to mission.");
            }
        } catch (SQLException ex) {
            String msg = "Error when assigning agent to mission.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
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
            String msg = "Error when getting missions of agent from DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public List<Agent> findAvailableAgents() {
        checkDataSource();
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT Agent.id, Agent.name, rank, alive " +
                    "FROM Agent LEFT JOIN Mission ON Agent.id = Mission.agentId " +
                    "WHERE Agent.alive = ? AND Mission.status IS NULL OR Mission.status IN (?, ?)")) {
                    //"FROM  ( SELECT * FROM Mission WHERE status = ? ) RIGHT JOIN Agent ON Agent.id = Mission.agentId " +
                    //"WHERE Agent.alive = ? AND Mission.status IS NULL")) {
                st.setBoolean(1, true);
                st.setString(2, MissionStatus.ACCOMPLISHED.toString());
                st.setString(3, MissionStatus.FAILED.toString());
                //st.setString(1, MissionStatus.IN_PROGRESS.toString());
                //st.setBoolean(2, true);
                return AgentManagerImpl.executeQueryForMultipleAgents(st);
            }
        } catch (SQLException ex) {
            String msg = "Error when getting available agents from DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private void checkDataSource() {
        if (dataSource == null) {
            String msg = "DataSource is not set.";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
    }

    private void checkAgent(Agent agent) {
        if (agent == null) {
            String msg = "Agent is null.";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (agent.getId() == null) {
            String msg = "Agent has null id.";
            logger.error(msg);
            throw new IllegalEntityException(msg);
        }
    }

    private void validate(Agent agent, Mission mission) {
        if (mission == null) {
            String msg = "Mission is null.";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        checkAgent(agent);
        if (mission.getId() == null) {
            String msg = "Mission has null id.";
            logger.error(msg);
            throw new IllegalEntityException(msg);
        }
        if (mission.getStatus() != MissionStatus.NOT_ASSIGNED) {
            String msg = "Mission already assigned.";
            logger.error(msg);
            throw new IllegalEntityException(msg);
        }
        checkAgentInDb(agent);
        if (!agent.isAlive()) {
            String msg = "Agent is dead.";
            logger.error(msg);
            throw new ValidationException(msg);
        }
        if (agent.getRank() < mission.getRequiredRank()) {
            String msg = "Agent's rank is too low for this mission.";
            logger.error(msg);
            throw new ValidationException(msg);
        }
        for (Mission m : findMissionsOfAgent(agent)) {
            if (m.getStatus() == MissionStatus.IN_PROGRESS) {
                String msg = "Agent is already on mission.";
                logger.error(msg);
                throw new IllegalEntityException(msg);
            }
        }
    }

    private void checkAgentInDb(Agent agent) {
        try (Connection conn = dataSource.getConnection()){
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM Agent WHERE id = ?")) {
                st.setLong(1, agent.getId());
                ResultSet rs = st.executeQuery();
                if (!rs.next()) {
                    String msg = agent + "agent not in DB.";
                    logger.error(msg);
                    throw new IllegalEntityException(msg);
                }
            }
        } catch (SQLException ex) {
            String msg = "Error when getting agent from DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }
}