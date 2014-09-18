package org.ancestra.evolutive.database.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.object.Server;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.zaxxer.hikari.HikariDataSource;

public class ServerData extends AbstractDAO<Server>{

	public ServerData(HikariDataSource dataSource) {
		super(dataSource);
		logger = (Logger)LoggerFactory.getLogger("factory.Server");
	}

	@Override
	public Server load(Object obj) {
		try {
			String query = "SELECT * FROM servers;";
			Result result = super.getData(query); 
            ResultSet resultSet = result.resultSet;			
			
			while(resultSet.next())
				new Server(resultSet.getInt("id"), 
						resultSet.getString("key"), 
						resultSet.getInt("population"), 
						resultSet.getInt("isSubscriberServer"));
			
			close(result);
			logger.debug("Servers successfully loaded");
		} catch (SQLException e) { 
			logger.debug("Can't load server");
		}
		return null;
	}
	
	@Override
	public boolean update(Server obj) {
		// TODO Auto-generated method stub
		return false;
	}
}
