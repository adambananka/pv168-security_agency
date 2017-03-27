package cz.muni.fi.pv168.app.agent;

import cz.muni.fi.pv168.app.common.DBUtils;
import cz.muni.fi.pv168.app.common.IllegalEntityException;
import cz.muni.fi.pv168.app.common.ServiceFailureException;
import cz.muni.fi.pv168.app.common.ValidationException;
import cz.muni.fi.pv168.app.mission.Mission;
import cz.muni.fi.pv168.app.mission.MissionManagerImpl;
import cz.muni.fi.pv168.app.mission.MissionStatus;

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
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createAgent(Agent agent) {
        checkDataSource();
        validate(agent);
        if (agent.getId() != null) {
            throw new IllegalEntityException("agent id is already set");
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO Agent (name, rank, alive) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, agent.getName());
            st.setInt(2, agent.getRank());
            st.setBoolean(3, agent.isAlive());

            st.executeUpdate();
            agent.setId(DBUtils.getId(st.getGeneratedKeys()));

            st.close();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting agent into DB", ex);
        }
    }

    @Override
    public void updateAgent(Agent agent) {
        checkDataSource();
        validate(agent);
        if (agent.getId() == null) {
            throw new IllegalEntityException("agent id is null");
        }
        validateAlive(agent);
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement(
                    "UPDATE Agent SET name = ?, rank = ?, alive = ? WHERE id = ?");
            st.setString(1, agent.getName());
            st.setInt(2, agent.getRank());
            st.setBoolean(3, agent.isAlive());
            st.setLong(4, agent.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new IllegalEntityException(agent + " does not exist in DB");
            }
            if (!agent.isAlive()) {
                updateDeadAgentMission(agent);
            }

            st.close();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            throw new ServiceFailureException("error when updating agent in DB", ex);
        }
    }

    @Override
    public void deleteAgent(Agent agent) {
        checkDataSource();
        if (agent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (agent.getId() == null) {
            throw new IllegalEntityException("agent id is null");
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement("DELETE FROM Agent WHERE id = ?");
            st.setLong(1, agent.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new IllegalEntityException(agent + " does not exist in DB");
            }

            st.close();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when deleting agent from DB", ex);
        }
    }

    @Override
    public Agent findAgent(Long id) {
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM Agent WHERE id = ?");
            st.setString(1, id.toString());
            return executeQueryForSingleAgent(st);
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when getting mission from DB", ex);
        }
    }

    @Override
    public List<Agent> findAllAgents() {
        checkDataSource();
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM Agent");
            return executeQueryForMultipleAgents(st);
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when getting all missions from DB", ex);
        }
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    private void validate(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (agent.getRank() < 1 || agent.getRank() > 10) {
            throw new ValidationException("rank is out of range");
        }
        if (agent.getName() == null) {
            throw new ValidationException("name is null");
        }
        if (agent.getName().equals("")) {
            throw new ValidationException("name is empty");
        }
        List<Agent> all = findAllAgents();
        for (Agent a : all) {
            if (!a.getId().equals(agent.getId()) && agent.getName().equals(a.getName())) {
                throw new ValidationException("name is duplicate");
            }
        }
    }

    private void validateAlive(Agent agent) {
        if (agent.isAlive()) {
            Agent old = findAgent(agent.getId());
            if (old != null && !old.isAlive()) {
                throw new ValidationException("agent can't be resurrected");
            }
        }
    }

    private static Agent executeQueryForSingleAgent(PreparedStatement st)
            throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Agent result = rowToAgent(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more missions with the same id found!");
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
            PreparedStatement findSt = conn.prepareStatement("SELECT * FROM Mission WHERE agentId = ? AND status = ?");
            findSt.setLong(1, agent.getId());
            findSt.setString(2, MissionStatus.IN_PROGRESS.toString());
            Mission mission = MissionManagerImpl.executeQueryForSingleMission(findSt);
            findSt.close();
            if (mission == null) {
                return;
            }
            PreparedStatement updateSt = conn.prepareStatement("UPDATE Mission SET status = ? WHERE id = ?");
            updateSt.setString(1, MissionStatus.FAILED.toString());
            updateSt.setLong(2, mission.getId());
            updateSt.execute();
        } catch (SQLException ex) {
            throw new ServiceFailureException("error when updating mission in DB", ex);
        }
    }
}
