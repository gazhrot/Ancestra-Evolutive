package org.ancestra.evolutive.database;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.ancestra.evolutive.database.data.AccountData;
import org.ancestra.evolutive.database.data.PlayerData;
import org.ancestra.evolutive.database.data.ServerData;
import org.ancestra.evolutive.kernel.Main;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class Database {
	//connection
	private HikariDataSource dataSource; 
    private static Logger logger = (Logger) LoggerFactory.getLogger(Database.class);
	//data
	private AccountData accountData;
	private PlayerData playerData;
	private ServerData serverData;
	
	public void initializeData() {
		this.accountData = new AccountData(dataSource);
		this.playerData = new PlayerData(dataSource);
		this.serverData = new ServerData(dataSource);
	}
	
	public boolean initializeConnection() {
        logger.trace("Reading database config");
        HikariConfig config = new HikariConfig();
        
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", Main.config.getHost());
        config.addDataSourceProperty("port", Main.config.getPort());
        config.addDataSourceProperty("databaseName", Main.config.getDatabaseName());
        config.addDataSourceProperty("user", Main.config.getUser());
        config.addDataSourceProperty("password", Main.config.getPass());

        dataSource = new HikariDataSource(config);
        if(!testConnection(dataSource)){
            logger.error("Pleaz check your username and password and database connection");
            System.exit(0);
        }
        logger.info("Database connection established");
        this.initializeData();
        return true;
	}
	
	public HikariDataSource getDataSource() {
		return dataSource;
	}
	
	public AccountData getAccountData() {
		return accountData;
	}
	
	public PlayerData getPlayerData() {
		return playerData;
	}
	
	public ServerData getServerData() {
		return serverData;
	}

    private boolean testConnection(HikariDataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
