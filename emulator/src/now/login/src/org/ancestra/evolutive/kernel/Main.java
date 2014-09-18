package org.ancestra.evolutive.kernel;

import org.ancestra.evolutive.database.Database;
import org.ancestra.evolutive.exchange.ExchangeServer;
import org.ancestra.evolutive.login.LoginServer;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class Main {
	
	static {System.setProperty("logback.configurationFile", "logback.xml");}
	private static Logger logger = (Logger) LoggerFactory.getLogger(Main.class);
	
	public static Database database = new Database();
	public static Config config = new Config();
	
	public static void main(String[] arg) {
		start();
	}
	
	public static void start() {
		//creation de la console
		Console console = new Console();
		Console.instance = console;
		
		logger.info(EmulatorInfos.SOFT_NAME.toString());
	
		Main.config.initialize();
		
		logger.debug(EmulatorInfos.HARD_NAME.toString());
		
		if(!Main.database.initializeConnection()) {
			console.write("> Identifiants de connexion invalides");
			console.write("> Redemarrage...");
			exit();
			System.exit(0);
		}
		
		console.write(" > Creation du server de connection");
		
		Main.database.getServerData().load(null);
		Main.database.getAccountData().update();

		Main.config.setExchangeServer(new ExchangeServer());
		Main.config.getExchangeServer().start();
		
		Main.config.setLoginServer(new LoginServer());
		Main.config.getLoginServer().start();
		
		console.write(" > Lancement du serveur termine : "+ (System.currentTimeMillis() - Main.config.startTime) +" ms");
		Main.config.setRunning(true);
		console.initialize();
	}
	
	public static void exit() {	
		Console.instance.write(" <> Fermeture du jeu <>");
		
		if(Main.config.isRunning()) {
			Main.config.setRunning(false);
			Main.config.getLoginServer().stop();
			Main.config.getExchangeServer().stop();	
		}
		
		System.exit(0);
		Console.instance.write(" <> Redemmarage <>");
	}
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				exit();
			}
		});
	}
}