package org.ancestra.evolutive.database.data;

import java.sql.PreparedStatement;

import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.database.AbstractDAO;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.zaxxer.hikari.HikariDataSource;

public class LoginOtherData extends AbstractDAO<Object> {

	public LoginOtherData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.LoginOther");
	}

	@Override
	public boolean create(Object obj) {
		return false;
	}

	@Override
	public boolean delete(Object obj) {
		return false;
	}

	@Override
	public boolean update(Object obj) {
		return false;
	}

	@Override
	public Object load(int id) {
		return null;
	}
	
	public void setUptime(long uptime) {
		try {
			String baseQuery = "UPDATE `servers` SET `uptime`= ? WHERE `id` = ?;";
			
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
			statement.setLong(1, uptime);
			statement.setInt(2, Server.config.getServerId());
			
			execute(statement);
		} catch(Exception e) {}
	}	
}