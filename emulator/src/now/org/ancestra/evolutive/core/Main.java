package org.ancestra.evolutive.core;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.enums.EmulatorInfos;
import org.ancestra.evolutive.game.GameServer;
import org.ancestra.evolutive.login.LoginServer;
import org.ancestra.evolutive.tool.plugin.PluginLoader;
import org.slf4j.LoggerFactory;

public class Main {
    static {System.setProperty("logback.configurationFile", "logback.xml");}
	private static Logger logger = (Logger) LoggerFactory.getLogger(Main.class);

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
			public void run() {
                closeServers();
            }
        });
	}
	
	public static void main(String[] args) {

		//creation de la console
		Console console = new Console();
		Console.instance = console;

		logger.info(EmulatorInfos.SOFT_NAME.toString());
		logger.debug(EmulatorInfos.HARD_NAME.toString());

		Server.config.initialize();
		if(!World.database.initializeConnection()) {
			console.writeln("> Identifiants de connexion invalides");
			console.writeln("> Redemarrage...");
			Main.closeServers();
			System.exit(0);
		}		
		
		console.writeln(" > Creation du monde");
		int time = World.data.initialize();

		Server.config.setRunning(true);
		
		//gameserver
		GameServer gameServer = new GameServer();
		gameServer.initialize();
		gameServer.scheduleActions();
		Server.config.setGameServer(gameServer);
		
		//realmserver
		LoginServer realmServer = new LoginServer();
		realmServer.initialize();
		Server.config.setRealmServer(realmServer);
			
		//serveur lance
		console.writeln(" > Il y a " + World.data.getOtherPlugins().size() + " plug-in(s) charger.");
		console.writeln(" > Lancement du serveur termine : "+ time +" ms");
		console.writeln(" > HELP pour la liste de commandes");
		
		//lancement de la console
		console.initialize();
	}

	public static void closeServers() {
        Console.instance.writeln(" <> Fermeture du jeu <>");
        Server.config.setRunning(false);
        if(Server.config.getRealmServer() != null)Server.config.getRealmServer().close();
        if(Server.config.getGameServer() != null)Server.config.getGameServer().close();
        World.data.saveData(-1);
        World.database.getAccountData().updateState(false);
        for(PluginLoader pl: World.data.getOtherPlugins().values())
            pl.disable();
        Console.instance.writeln(" <> Redemmarage <>");

	}
}