package org.ancestra.evolutive.kernel;

import com.typesafe.config.ConfigFactory;

import java.io.File;

import org.ancestra.evolutive.exchange.ExchangeServer;
import org.ancestra.evolutive.login.LoginServer;

public class Config {
	
    //Config
    private static final com.typesafe.config.Config configFile = ConfigFactory.parseFile(new File("config.conf"));
    public final long startTime = System.currentTimeMillis();

    //emulator
	private boolean isRunning;
	
	private LoginServer loginServer;
	private ExchangeServer exchangeServer;
	
	//database
	private String host, user, pass;
    private int port;
	private String databaseName;
	
	//network
	private String loginIp, exchangeIp, version;
	private int loginPort, exchangePort;
	
	public void initialize() {	
		try {
			//database
			this.host = configFile.getString("database.host");
			this.user = configFile.getString("database.user");
			this.pass = configFile.getString("database.password");
			this.databaseName = configFile.getString("database.loginName");
            this.port = configFile.getInt("database.port");
			
			//network
			this.loginIp = configFile.getString("network.loginIp");
			this.exchangeIp = configFile.getString("network.exchangeIp");
			this.version = configFile.getString("network.version");
			this.loginPort = configFile.getInt("network.loginPort");
			this.exchangePort = configFile.getInt("network.exchangePort");	
		} catch(Exception e) {
			System.out.println(" <> Config illisible ou champs manquants: "+e.getMessage());
			System.exit(1);
		}
	}	
	
	public com.typesafe.config.Config getConfigFile() {
		return configFile;
	}
	
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public LoginServer getLoginServer() {
		return loginServer;
	}

	public void setLoginServer(LoginServer loginServer) {
		this.loginServer = loginServer;
	}

	public ExchangeServer getExchangeServer() {
		return exchangeServer;
	}

	public void setExchangeServer(ExchangeServer exchangeServer) {
		this.exchangeServer = exchangeServer;
	}

	public String getHost() {
		return host;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public int getPort() {
		return port;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public String getExchangeIp() {
		return exchangeIp;
	}

	public String getVersion() {
		return version;
	}
	
	public int getLoginPort() {
		return loginPort;
	}

	public int getExchangePort() {
		return exchangePort;
	}
}
