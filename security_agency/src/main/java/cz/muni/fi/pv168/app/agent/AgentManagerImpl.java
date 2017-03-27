package cz.muni.fi.pv168.app.agent;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    }

    @Override
    public void updateAgent(Agent agent) {

    }

    @Override
    public void deleteAgent(Agent agent) {

    }

    @Override
    public Agent findAgent(long id) {
        return null;
    }



    @Override
    public List<Agent> findAllAgents() {
        return null;
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
}
