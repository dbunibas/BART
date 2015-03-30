package bart.persistence.relational;

import bart.exceptions.DAOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDbConnectionFactory implements IConnectionFactory {

    private static Logger logger = LoggerFactory.getLogger(SimpleDbConnectionFactory.class);

    private void init(AccessConfiguration configuration) throws DAOException {
        try {
            Class.forName(configuration.getDriver());
            if (logger.isDebugEnabled()) logger.debug("Driver initialized: " + configuration.getDriver());
        } catch (Exception e) {
            logger.error(" Wrong parameter in driver configuration: " + e);
            logger.error("Requested driver: " + configuration.getDriver());
            throw new DAOException(e.getMessage());
        }
    }

    public Connection getConnection(AccessConfiguration configuration) throws DAOException {
        init(configuration);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(configuration.getUri(), configuration.getLogin(), configuration.getPassword());
        } catch (SQLException sqle) {
            close(connection);
            throw new DAOException(" getConnection: " + sqle + "\n\ndriver: " + configuration.getDriver() + " - uri: " + configuration.getUri() + " - login: " + configuration.getLogin() + " - password: " + configuration.getPassword() + "\n");
        }
        if (connection == null) {
            throw new DAOException("Connection is NULL !" + "\n\ndriver: " + configuration.getDriver() + " - uri: " + configuration.getUri() + " - login: " + configuration.getLogin() + " - password: " + configuration.getPassword() + "\n");
        }
        return connection;
    }

    public void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sqle) {
            logger.error(sqle.toString());
        }
    }

    public void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException sqle) {
            logger.error(sqle.toString());
        }
    }

    public void close(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException sqle) {
            logger.error(sqle.toString());
        }
    }
}
