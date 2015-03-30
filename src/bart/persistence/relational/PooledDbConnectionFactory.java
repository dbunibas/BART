package bart.persistence.relational;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import bart.exceptions.DAOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.LoggerFactory;

public class PooledDbConnectionFactory implements IConnectionFactory {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(PooledDbConnectionFactory.class);

    private ComboPooledDataSource cpds;
    private AccessConfiguration currentConfiguration;

    private void init(AccessConfiguration configuration) throws DAOException {
        if (cpds != null && isCompatible(currentConfiguration,configuration)) {
            return;
        }
        try {
            if(cpds!=null){
                closeFactory();
            }
            cpds = new ComboPooledDataSource();
            cpds.setDriverClass(configuration.getDriver());
            cpds.setJdbcUrl(configuration.getUri());
            cpds.setUser(configuration.getLogin());
            cpds.setPassword(configuration.getPassword());
            //       Pool configuration
//            cpds.setMaxStatements(180);  //If you want to turn on PreparedStatement pooling, you must also set maxStatements and/or maxStatementsPerConnection
//            cpds.setMinPoolSize(5);
//            cpds.setAcquireIncrement(5);
//            cpds.setMaxPoolSize(20);
            if (logger.isDebugEnabled()) logger.debug("Pool initialized: " + cpds.toString());
            currentConfiguration = configuration;
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
            if (logger.isTraceEnabled()) logger.trace("Getting connection from pool ");
            if (logger.isTraceEnabled()) logger.trace("    getMaxPoolSize: " + cpds.getMaxPoolSize());
            if (logger.isTraceEnabled()) logger.trace("    getNumConnections: " + cpds.getNumConnections());
            if (logger.isTraceEnabled()) logger.trace("    getNumBusyConnections: " + cpds.getNumBusyConnections());
            connection = cpds.getConnection();
            if (logger.isTraceEnabled()) logger.trace("Opened connections: " + cpds.getNumConnections());
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

    public void closeFactory() {
        try {
            DataSources.destroy(cpds);
        } catch (SQLException ex) {
            logger.error("Unable to close factory pool. " + ex.getLocalizedMessage());
            throw new DAOException(ex);
        }
    }

    private boolean isCompatible(AccessConfiguration currentConfiguration, AccessConfiguration configuration) {
        if(currentConfiguration == null){
            return false;
        }
        return currentConfiguration.getDatabaseName().equals(configuration.getDatabaseName()) && currentConfiguration.getDriver().equals(configuration.getDriver());
    }
}
