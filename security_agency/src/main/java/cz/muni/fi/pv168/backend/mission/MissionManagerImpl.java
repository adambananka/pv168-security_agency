package cz.muni.fi.pv168.backend.mission;

import cz.muni.fi.pv168.backend.common.DBUtils;
import cz.muni.fi.pv168.backend.common.IllegalEntityException;
import cz.muni.fi.pv168.backend.common.ServiceFailureException;
import cz.muni.fi.pv168.backend.common.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(MissionManagerImpl.class);

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createMission(Mission mission) throws IllegalEntityException{
        logger.info("Creating new mission...");
        checkDataSource();
        validate(mission);
        if (mission.getId() != null) {
            String msg = "Mission id is already set.";
            logger.error(msg);
            throw new IllegalEntityException(msg);
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO Mission (name, status, required_rank) VALUES (?, ?, ?)",
                           Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, mission.getName());

                st.setString(2, mission.getStatus().toString());
                st.setInt(3, mission.getRequiredRank());

                st.executeUpdate();
                mission.setId(DBUtils.getId(st.getGeneratedKeys()));
                logger.info("New mission successfully created.");
            }
        } catch (SQLException ex) {
            String msg = "Error when inserting mission into DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void updateMission(Mission mission) {
        logger.info("Updating mission...");
        checkDataSource();
        validate(mission);
        if (mission.getId() == null) {
            String msg = "Mission id is null.";
            logger.error(msg);
            throw new IllegalEntityException(msg);
        }
        validateStatus(mission);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "UPDATE Mission SET name = ?, agentId = ?, status = ?, required_rank = ? WHERE id = ?")) {
                conn.setAutoCommit(false);
                st.setString(1, mission.getName());
                st.setLong(2, mission.getAgentId());
                st.setString(3, mission.getStatus().toString());
                st.setInt(4, mission.getRequiredRank());
                st.setLong(5, mission.getId());

                int count = st.executeUpdate();
                if (count == 0) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    String msg = mission + " does not exist in DB.";
                    logger.error(msg);
                    throw new IllegalEntityException(msg);
                }
                conn.commit();
                conn.setAutoCommit(true);
                logger.info("Mission successfully updated.");
            }
        } catch (SQLException ex) {
            String msg = "Error when updating mission in DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void deleteMission(Mission mission) throws IllegalEntityException, ServiceFailureException{
        logger.info("Deleting mission...");
        checkDataSource();
        if (mission == null) {
            String msg = "Mission is null.";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (mission.getId() == null) {
            String msg = "Mission id is null.";
            logger.error(msg);
            throw new IllegalEntityException(msg);
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM Mission WHERE id = ?")) {
                conn.setAutoCommit(false);
                st.setLong(1, mission.getId());

                int count = st.executeUpdate();
                if (count == 0) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    String msg = mission + " does not exist in DB.";
                    logger.error(msg);
                    throw new IllegalEntityException(msg);
                }
                conn.commit();
                conn.setAutoCommit(true);
                logger.info("Mission successfully deleted.");
            }
        } catch (SQLException ex) {
            String msg = "Error when deleting mission from DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public Mission findMission(Long id) throws IllegalArgumentException, ServiceFailureException{
        checkDataSource();
        if (id == null) {
            String msg = "Mission id is null.";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM Mission WHERE id = ?")) {
                st.setString(1, id.toString());
                return executeQueryForSingleMission(st);
            }
        } catch (SQLException ex) {
            String msg = "Error when getting mission from DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public List<Mission> findAvailableMissions() throws ServiceFailureException{
        checkDataSource();
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM Mission WHERE status = ?")) {
                st.setString(1, MissionStatus.NOT_ASSIGNED.toString());
                return executeQueryForMultipleMissions(st);
            }
        } catch (SQLException ex) {
            String msg = "Error when getting available missions from DB.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public List<Mission> findAllMissions() throws ServiceFailureException{
        checkDataSource();
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM Mission")) {
                return executeQueryForMultipleMissions(st);
            }
        } catch (SQLException ex) {
            String msg = "Error when getting all missions from DB.";
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

    private void validate(Mission mission) {
        if (mission == null) {
            String msg = "Mission is null.";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (mission.getRequiredRank() < 1 || mission.getRequiredRank() > 10) {
            String msg = "Mission required rank is out of range.";
            logger.error(msg);
            throw new ValidationException(msg);
        }
        if (mission.getName() == null) {
            String msg = "Mission name is null.";
            logger.error(msg);
            throw new ValidationException(msg);
        }
        if (mission.getName().equals("")) {
            String msg = "Mission name is empty.";
            logger.error(msg);
            throw new ValidationException(msg);
        }
        List<Mission> all = findAllMissions();
        for (Mission m : all) {
            if (!m.getId().equals(mission.getId()) && mission.getName().equals(m.getName())) {
                String msg = "Mission name is duplicate.";
                logger.error(msg);
                throw new ValidationException(msg);
            }
        }
    }

    private void validateStatus(Mission mission) {
        Mission old = findMission(mission.getId());
        if (old != null && old.getStatus().ordinal() > mission.getStatus().ordinal()) {
            String msg = "Mission status can't be changed to previous one.";
            logger.error(msg);
            throw new ValidationException(msg);
        }
    }

    public static Mission executeQueryForSingleMission(PreparedStatement st)
            throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Mission result = rowToMission(rs);
            if (rs.next()) {
                String msg = "Internal integrity error: more missions with the same id found.";
                logger.error(msg);
                throw new ServiceFailureException(msg);
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
