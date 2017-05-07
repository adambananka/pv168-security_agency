package cz.muni.fi.pv168.backend.agent;

import cz.muni.fi.pv168.backend.common.DBUtils;
import cz.muni.fi.pv168.backend.common.IllegalEntityException;
import cz.muni.fi.pv168.backend.common.ServiceFailureException;
import cz.muni.fi.pv168.backend.common.ValidationException;
import cz.muni.fi.pv168.backend.mission.Mission;
import cz.muni.fi.pv168.backend.mission.MissionManagerImpl;
import cz.muni.fi.pv168.backend.mission.MissionStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements AgentManager service.
 *
 * @author Adam Baňanka, Daniel Homola
 */
public class AgentManagerImpl implements AgentManager {

    private static final Logger logger = LoggerFactory.getLogger(AgentManagerImpl.class);

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createAgent(Agent agent) {
        logger.info("Creating new agent...");
        checkDataSource();
        validate(agent);
        if (agent.getId() != null) {
            String msg = "Agent id is already set.";
            logger.error(msg);
            throw new IllegalEntityException(msg);
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO Agent (name, rank, alive) VALUES (?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, agent.getName());
                st.setInt(2, agent.getRank());
                st.setBoolean(3, agent.isAlive());

                st.executeUpdate();
                agent.setId(DBUtils.getId(st.getGeneratedKeys()));
                logger.info("New agent successfully created.");
            }
        } catch (SQLException ex) {
            String msg = "Error when inserting agent into DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void updateAgent(Agent agent) {
        logger.info("Updating agent...");
        checkDataSource();
        validate(agent);
        if (agent.getId() == null) {
            String msg = "Agent id is null.";
            logger.error(msg);
            throw new IllegalEntityException(msg);
        }
        validateAlive(agent);
        try (Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st = conn.prepareStatement(
                    "UPDATE Agent SET name = ?, rank = ?, alive = ? WHERE id = ?")) {
                conn.setAutoCommit(false);
                st.setString(1, agent.getName());
                st.setInt(2, agent.getRank());
                st.setBoolean(3, agent.isAlive());
                st.setLong(4, agent.getId());

                int count = st.executeUpdate();
                if (count == 0) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    String msg = agent + " does not exist in DB.";
                    logger.error(msg);
                    throw new IllegalEntityException(msg);
                }
                if (!agent.isAlive()) {
                    updateDeadAgentMission(agent);
                }

                conn.commit();
                conn.setAutoCommit(true);
                logger.info("Agent successfully updated.");
            }
        } catch (SQLException ex) {
            String msg = "Error when updating agent in DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void deleteAgent(Agent agent) {
        logger.info("Deleting agent...");
        checkDataSource();
        if (agent == null) {
            String msg = "Agent is null.";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (agent.getId() == null) {
            String msg = "Agent id is null.";
            logger.error(msg);
            throw new IllegalEntityException(msg);
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM Agent WHERE id = ?")) {
                conn.setAutoCommit(false);

                st.setLong(1, agent.getId());

                int count = st.executeUpdate();
                if (count == 0) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    String msg = agent + " does not exist in DB.";
                    logger.error(msg);
                    throw new IllegalEntityException(msg);
                }
                conn.commit();
                conn.setAutoCommit(true);
                logger.info("Agent successfully deleted.");
            }
        } catch (SQLException ex) {
            String msg = "Error when deleting agent from DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public Agent findAgent(Long id) {
        checkDataSource();
        if (id == null) {
            String msg = "Agent id is null.";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM Agent WHERE id = ?")) {
                st.setString(1, id.toString());
                return executeQueryForSingleAgent(st);
            }
        } catch (SQLException ex) {
            String msg = "Error when getting agent from DB.";
            logger.error(msg);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public List<Agent> findAllAgents() {
        checkDataSource();
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM Agent")) {
                return executeQueryForMultipleAgents(st);
            }
        } catch (SQLException ex) {
            String msg = "Error when getting all agents from DB.";
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

    private void validate(Agent agent) {
        if (agent == null) {
            String msg = "Agent is null.";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (agent.getRank() < 1 || agent.getRank() > 10) {
            String msg = "Agent rank is out of range.";
            logger.error(msg);
            throw new ValidationException(msg);
        }
        if (agent.getName() == null) {
            String msg = "Agent name is null.";
            logger.error(msg);
            throw new ValidationException(msg);
        }
        if (agent.getName().equals("")) {
            String msg = "Agent name is empty.";
            logger.error(msg);
            throw new ValidationException(msg);
        }
        List<Agent> all = findAllAgents();
        for (Agent a : all) {
            if (!a.getId().equals(agent.getId()) && agent.getName().equals(a.getName())) {
                String msg = "Agent name is duplicate.";
                logger.error(msg);
                throw new ValidationException(msg);
            }
        }
    }

    private void validateAlive(Agent agent) {
        if (agent.isAlive()) {
            Agent old = findAgent(agent.getId());
            if (old != null && !old.isAlive()) {
                String msg = "Agent can't be resurrected.";
                logger.error(msg);
                throw new ValidationException(msg);
            }
        }
    }

    private static Agent executeQueryForSingleAgent(PreparedStatement st)
            throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Agent result = rowToAgent(rs);
            if (rs.next()) {
                String msg = "Internal integrity error: more agents with the same id found.";
                logger.error(msg);
                throw new ServiceFailureException(msg);
            }
            return result;
        } else {
            return null;
        }
    }

    public static List<Agent> executeQueryForMultipleAgents(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Agent> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rowToAgent(rs));
        }
        return result;
    }

    private static Agent rowToAgent(ResultSet rs) throws SQLException {
        Agent result = new Agent();
        result.setId(rs.getLong("id"));
        result.setName(rs.getString("name"));
        result.setRank(rs.getInt("rank"));
        result.setAlive(rs.getBoolean("alive"));
        return result;
    }

    private void updateDeadAgentMission(Agent agent) {
        try (Connection conn = dataSource.getConnection()) {
            Mission mission;
            try (PreparedStatement findSt = conn.prepareStatement("SELECT * FROM Mission WHERE agentId = ? AND status = ?")) {
                findSt.setLong(1, agent.getId());
                findSt.setString(2, MissionStatus.IN_PROGRESS.toString());
                mission = MissionManagerImpl.executeQueryForSingleMission(findSt);
            }
            if (mission == null) {
                return;
            }
            try (PreparedStatement updateSt = conn.prepareStatement("UPDATE Mission SET status = ? WHERE id = ?")) {
                updateSt.setString(1, MissionStatus.FAILED.toString());
                updateSt.setLong(2, mission.getId());
                updateSt.execute();
            }
        } catch (SQLException ex) {
            String msg = "Error when updating agent in DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }
}
