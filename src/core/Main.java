package core;

import login.LoginServer;

import common.World;

import enums.EmulatorInfos;
import game.GameServer;

public class Main {
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                closeServers();
            }
        });
	}
	
	public static void main(String[] args) {
		//creation de la console		
		Console console = new Console();
		Console.instance = console;
	
		//demarrage de l'emualteur
		console.writeln(EmulatorInfos.SOFT_NAME.toString());
		console.writeln(" ~ Initialisation des variables : config.conf");
		Server.config.initialize();
		console.writeln(" ~ Connexion a la base de donnees : "+Server.config.getHost());
		
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
		console.writeln(" > Il y a "+World.data.getPluginParsers().size()+ " plug-in(s) charger.");
		console.writeln(" > Lancement du serveur termine : "+ time +" ms");
		console.writeln(" > HELP pour la liste de commandes");
		
		//lancement de la console
		console.initialize();
	}

	public static void closeServers() {
		World.data.getWorker().execute(new Runnable() {
			public void run() {
				if(Server.config.isRunning()) {
					Console.instance.writeln(" <> Fermeture du jeu <>");
					Server.config.setRunning(false);
					Console.instance.writeln("Close RealmServer");
					Server.config.getRealmServer().close();
					Console.instance.writeln("CloseGameServer");
					Server.config.getGameServer().close();
					Console.instance.writeln("1");
					World.data.saveData(-1);
					Console.instance.writeln("1");
					World.database.getAccountData().updateState(false);
					Console.instance.writeln("1");
					World.database.close();
					Console.instance.writeln("1");
					Console.instance.writeln(" <> Redemmarage <>");
				}
			}
		});
	}
}