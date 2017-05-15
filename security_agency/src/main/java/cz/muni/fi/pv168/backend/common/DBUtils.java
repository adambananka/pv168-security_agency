package cz.muni.fi.pv168.backend.common;

import cz.muni.fi.pv168.frontend.MainWindow;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public class DBUtils {

    private static final Logger logger = LoggerFactory.getLogger(DBUtils.class);

    public static DataSource getDataSource() throws SQLException {
        Properties prop = new Properties();
        try (InputStream input = MainWindow.class.getResourceAsStream("config.properties")){
            prop.load(input);
        } catch (IOException e) {
            logger.error("error reading properties", e);
        }
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(EmbeddedDriver.class.getName());
        ds.setUrl(prop.getProperty("url"));
        ds.setUsername(prop.getProperty("user"));
        ds.setPassword(prop.getProperty("password"));
        return ds;
    }

    public static DataSource createMemoryDatabase() {
        Properties prop = new Properties();
        try (InputStream input = MainWindow.class.getResourceAsStream("config.properties")){
            prop.load(input);
        } catch (IOException e) {
            logger.error("error reading properties", e);
        }
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setCreateDatabase("create");
        ds.setDatabaseName(prop.getProperty("name"));
        ds.setUser(prop.getProperty("user"));
        ds.setPassword(prop.getProperty("password"));
        return ds;
    }

    /**
     * Reads SQL statements from file. SQL commands in file must be separated by
     * a semicolon.
     *
     * @param url url of the file
     * @return array of command  strings
     */
    private static String[] readSqlStatements(URL url) {
        try {
            char buffer[] = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                }
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        } catch (IOException ex) {
            String msg = "Cannot read " + url;
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    /**
     * Executes SQL script.
     *
     * @param ds datasource
     * @param scriptUrl url of sql script to be executed
     * @throws SQLException when operation fails
     */
    public static void executeSqlScript(DataSource ds, URL scriptUrl) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            for (String sqlStatement : readSqlStatements(scriptUrl)) {
                if (!sqlStatement.trim().isEmpty()) {
                    conn.prepareStatement(sqlStatement).executeUpdate();
                }
            }
        }
    }

    /**
     * Extract key from given ResultSet.
     *
     * @param key resultSet with key
     * @return key from given result set
     * @throws SQLException when operation fails
     */
    public static Long getId(ResultSet key) throws SQLException {
        if (key.getMetaData().getColumnCount() != 1) {
            String msg = "Given ResultSet contains more columns.";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (key.next()) {
            Long result = key.getLong(1);
            if (key.next()) {
                String msg = "Given ResultSet contains more rows.";
                logger.error(msg);
                throw new IllegalArgumentException(msg);
            }
            return result;
        } else {
            String msg = "Given ResultSet contain no rows.";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
