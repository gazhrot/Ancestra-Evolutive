package org.ancestra.evolutive.database;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;

import java.sql.*;


public abstract class AbstractDAO<T> implements DAO<T> {

    protected class Result{
        public final Connection connection;
        public final ResultSet resultSet;
        protected Result(Connection connection,ResultSet resultSet) {
            this.connection = connection;
            this.resultSet = resultSet;
        }
    }
	
	protected HikariDataSource dataSource;
    protected Logger logger = (Logger)LoggerFactory.getLogger("test");
    protected final Object locker = new Object();

	public AbstractDAO(HikariDataSource dataSource) {
         this.dataSource = dataSource;
	}
	
	protected void execute(String query) {
        synchronized (locker) {
            Connection connection = null;
            Statement statement = null;
            try {
                connection = dataSource.getConnection();
                statement = connection.createStatement();
                statement.execute(query);
                logger.debug("SQL request executed successfully {}",query);
            }catch(SQLException e) {
                logger.error("Can't execute SQL Request :"+ query,e);
                try {
                    if (connection != null) {
                        connection.rollback();
                    }
                }catch (Exception e1){
                    logger.error("Can't rollback",e1);
                }
            } finally {
                close(statement);
                close(connection);
            }
        }
    }
	
	protected void execute(PreparedStatement statement) {
        synchronized (locker) {
            Connection connection = null;
            try {
                connection = statement.getConnection();
                statement.execute();
                logger.debug("SQL request executed successfully {}", statement.toString());
            }catch(SQLException e) {
                logger.error("Can't execute SQL Request :"+ statement.toString(),e);
            } finally {
                close(statement);
                close(connection);
            }
        }
	}

	
	protected Result getData(String query) {
        synchronized (locker) {
            Connection connection = null;
            try {
                if (!query.endsWith(";")) query = query + ";";
                connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                Result result = new Result(connection,statement.executeQuery(query));
                logger.debug("SQL request executed successfully {}", query);
                return result;
            } catch (SQLException e) {
                logger.error("Can't execute SQL Request :" + query, e);
                try {
                    if (connection != null) {
                        connection.rollback();
                    }
                } catch (Exception e1) {
                    logger.error("Can't rollback", e1);
                }
            }
            return null;
        }
	}

    protected PreparedStatement getPreparedStatement(String query) throws SQLException {
        Connection connection = dataSource.getConnection();
        return connection.prepareStatement(query);
    }
	
	protected void close(PreparedStatement statement) {
        if (statement == null) 
        	return;
		try {
			statement.clearParameters();
	        statement.close();
		} catch (Exception e) {
            logger.error("Can't close statement", e);
		}
	}

    protected void close(Connection connection){
        if(connection == null) 
        	return;
        try {
            connection.close();
            logger.trace("{} released",connection);
        } catch (Exception e) {
            logger.error("Can't close connection", e);
        }
    }

    protected void close(Statement statement) {
        if(statement == null) 
        	return;
        try {
            statement.close();
        } catch (Exception e) {
            logger.error("Can't close statement", e);
        }
    }

    protected void close(Result result) {
        if(result != null) {
            try {
                if (result.resultSet != null)
                    result.resultSet.close();
                if(result.connection != null)
                    result.connection.close();
                logger.trace("Connection {} has been released",result.connection);
            } catch (SQLException e) {
                logger.error("Can't close result");
            }
        }
    }
}
