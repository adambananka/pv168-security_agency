package cz.muni.fi.pv168.app.mission;

import cz.muni.fi.pv168.app.common.DBUtils;
import cz.muni.fi.pv168.app.common.IllegalEntityException;
import cz.muni.fi.pv168.app.common.ServiceFailureException;
import cz.muni.fi.pv168.app.common.ValidationException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements MissionManager service.
 *
 * @author Adam Baňanka, Daniel Homola
 */
public class MissionManagerImpl implements MissionManager {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createMission(Mission mission) throws IllegalEntityException{
        checkDataSource();
        validate(mission);
        if (mission.getId() != null) {
            throw new IllegalEntityException("mission id is already set");
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO Mission (name, status, required_rank) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, mission.getName());
            st.setString(2, mission.getStatus().toString());
            st.setInt(3, mission.getRequiredRank());

            st.executeUpdate();
            mission.setId(DBUtils.getId(st.getGeneratedKeys()));

            st.close();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting mission into DB", ex);
        }
    }

    @Override
    public void updateMission(Mission mission) {
        checkDataSource();
        validate(mission);
        if (mission.getId() == null) {
            throw new IllegalEntityException("mission id is null");
        }
        validateStatus(mission);
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement(
                    "UPDATE Mission SET name = ?, agentId = ?, status = ?, required_rank = ? WHERE id = ?");
            st.setString(1, mission.getName());
            st.setLong(2, mission.getAgentId());
            st.setString(3, mission.getStatus().toString());
            st.setInt(4, mission.getRequiredRank());
            st.setLong(5, mission.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new IllegalEntityException(mission + " does not exist in DB");
            }

            st.close();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            throw new ServiceFailureException("error when updating mission in DB", ex);
        }
    }

    @Override
    public void deleteMission(Mission mission) throws IllegalEntityException, ServiceFailureException{
        checkDataSource();
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getId() == null) {
            throw new IllegalEntityException("mission id is null");
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement("DELETE FROM Mission WHERE id = ?");
            st.setLong(1, mission.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new IllegalEntityException(mission + " does not exist in DB");
            }

            st.close();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when deleting mission from DB", ex);
        }
    }

    @Override
    public Mission findMission(Long id) throws IllegalArgumentException, ServiceFailureException{
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM Mission WHERE id = ?");
            st.setString(1, id.toString());
            return executeQueryForSingleMission(st);
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when getting mission from DB", ex);
        }
    }

    @Override
    public List<Mission> findAvailableMissions() throws ServiceFailureException{
        checkDataSource();
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM Mission WHERE status = ?");
            st.setString(1, MissionStatus.NOT_ASSIGNED.toString());
            return executeQueryForMultipleMissions(st);
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when getting available missions from DB", ex);
        }
    }

    @Override
    public List<Mission> findAllMissions() throws ServiceFailureException{
        checkDataSource();
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM Mission");
            return executeQueryForMultipleMissions(st);
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when getting all missions from DB", ex);
        }
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    private void validate(Mission mission) {
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getRequiredRank() < 1 || mission.getRequiredRank() > 10) {
            throw new ValidationException("required rank is out of range");
        }
        if (mission.getName() == null) {
            throw new ValidationException("mission name is null");
        }
        if (mission.getName().equals("")) {
            throw new ValidationException("mission name is empty");
        }
        List<Mission> all = findAllMissions();
        for (Mission m : all) {
            if (!m.getId().equals(mission.getId()) && mission.getName().equals(m.getName())) {
                throw new ValidationException("mission name is duplicate");
            }
        }
    }

    private void validateStatus(Mission mission) {
        Mission old = findMission(mission.getId());
        if (old != null && old.getStatus().ordinal() > mission.getStatus().ordinal()) {
            throw new ValidationException("mission status can't be changed to previous one");
        }
    }

    public static Mission executeQueryForSingleMission(PreparedStatement st)
            throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Mission result = rowToMission(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more missions with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    public static List<Mission> executeQueryForMultipleMissions(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Mission> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rowToMission(rs));
        }
        return result;
    }

    private static Mission rowToMission(ResultSet rs) throws SQLException {
        Mission result = new Mission();
        result.setId(rs.getLong("id"));
        result.setName(rs.getString("name"));
        result.setAgentId(rs.getLong("agentId"));
        result.setStatus(MissionStatus.valueOf(rs.getString("status")));
        result.setRequiredRank(rs.getInt("required_rank"));
        return result;
    }
}
