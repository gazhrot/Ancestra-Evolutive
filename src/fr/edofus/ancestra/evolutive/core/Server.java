package fr.edofus.ancestra.evolutive.core;

import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.common.Constants;
import fr.edofus.ancestra.evolutive.common.CryptManager;
import fr.edofus.ancestra.evolutive.event.Event;
import fr.edofus.ancestra.evolutive.event.EventListener;
import fr.edofus.ancestra.evolutive.event.player.PlayerJoinEvent;
import fr.edofus.ancestra.evolutive.game.GameServer;
import fr.edofus.ancestra.evolutive.login.LoginServer;
import fr.edofus.ancestra.evolutive.tool.command.Command;
import fr.edofus.ancestra.evolutive.tool.command.CommandAccess;
import fr.edofus.ancestra.evolutive.tool.plugin.PluginLoader;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;
import fr.edofus.ancestra.evolutive.tool.time.restricter.RestrictLevel;
import fr.edofus.ancestra.evolutive.tool.time.restricter.TimeRestricter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Server {
	
	public static Server config = new Server();
	
	//emulator
	private boolean isRunning;
	private boolean isSaving;
	
	private GameServer gameServer;
	private LoginServer realmServer;
	
	//console
	private boolean canLog;
	private boolean debug;
	
	//database
	private String host, user, pass;
	private String databaseName;
	
	//network
	private boolean ipLoopBack;
	private boolean useIp;
	private String ip;
	private int realmPort, gamePort;
	private boolean socketUseCompactData = false;
	private int socketTimeCompactData = 200;
	
	//on player connection
	private String motd, motdColor;
	private PrintStream ps;
	private boolean policy;
	
	//player
	private int maxPlayersPerAccount;
	private short startMap;
	private int startCell;
	private int startLevel, startKamas;
	
	private boolean multiAccount;
	private boolean allZaaps;
	private boolean mulePvp;
	private boolean customStartMap;
	private boolean auraSystem;
	private int maxIdleTime;
	
	//server
	private int saveTime;
	private long floodTime;
	private int loadDelay;
	private int reloadMobDelay;
	private int playerLimitOnServer;
	private boolean useMobs;
	
	//rates
	private int rateDrop;
	private int rateXpPvm;
	private int rateKamas;
	private int rateHonor;
	private int rateXpJob;
	private int rateXpPvp;
	private int averageLevelPvp;
	
	//arena
	private ArrayList<Integer> arenaMaps = new ArrayList<>();
	private int arenaTime;
	
	//hdv
	private ArrayList<Integer> noInHdv = new ArrayList<>();
	
	//marchand
	private ArrayList<Integer> marchandMaps = new ArrayList<>();
	
	//collector
	private ArrayList<Integer> collectorMaps = new ArrayList<>();
	
	//Config
	private Config configFile = ConfigFactory.parseFile(new File("config.conf"));
	
	public void initialize() {	
		
		World.events.add(World.events.getEventClass(PlayerJoinEvent.class), new EventListener() {
			@Override
			public void call(Event event, Object source, Object[] args) {
				if(event instanceof PlayerJoinEvent)
					System.out.println(((PlayerJoinEvent) event).getPlayer().get_name()+" vient de rejoindre le serveur !");
			}	
		});
		try {
			//console
			this.debug = configFile.getBoolean("console.debug");
			this.canLog = configFile.getBoolean("console.log");

			Log.initLogs();
			
			//database
			this.host = configFile.getString("database.host");
			this.user = configFile.getString("database.user");
			this.pass = configFile.getString("database.password");
			this.databaseName = configFile.getString("database.databaseName");
			
			//network
			this.ip = configFile.getString("network.ip");
			this.useIp = !configFile.getBoolean("network.local");
			this.realmPort = configFile.getInt("network.realmPort");
			this.gamePort = configFile.getInt("network.gamePort");
			
			//on player connected
			this.maxPlayersPerAccount = configFile.getInt("onClientConnected" +
					".maxPlayersPerAccount");
			this.startLevel = configFile.getInt("onClientConnected.startLevel");
			this.startMap = (short)configFile.getInt("onClientConnected.startMap");
			this.startCell = configFile.getInt("onClientConnected.startCell");
			this.startKamas = configFile.getInt("onClientConnected.startKamas");
			this.multiAccount = configFile.getBoolean("onClientConnected.multiAccount");
			this.allZaaps = configFile.getBoolean("onClientConnected.allZaaps");
			this.mulePvp = configFile.getBoolean("onClientConnected.mulePvp");
			this.customStartMap = configFile.getBoolean("onClientConnected.customStartMap");
			this.auraSystem = configFile.getBoolean("onClientConnected.auraSystem");
			this.maxIdleTime = configFile.getInt("onClientConnected.maxIdleTime");
			
			//server
			this.saveTime = configFile.getInt("server.saveTime");
			this.floodTime = configFile.getInt("server.floodTime");
			this.loadDelay = configFile.getInt("server.liveActionDelay");
			this.useMobs = configFile.getBoolean("server.useMob");
			this.reloadMobDelay = configFile.getInt("server.reloadMobDelay");
			this.playerLimitOnServer = configFile.getInt("server.playerLimit");
			this.motd = configFile.getString("server.welcomMessage.content");
			this.motdColor = configFile.getString("server.welcomMessage.color");
			
			//rates
			this.rateDrop = configFile.getInt("rates.drop");
			this.rateKamas = configFile.getInt("rates.kamas");
			this.rateHonor = configFile.getInt("rates.honor");
			this.rateXpPvm = configFile.getInt("rates.xpPvm");
			this.rateXpJob = configFile.getInt("rates.xpJob");
			this.rateXpPvp = configFile.getInt("rates.xpPvp");
			this.averageLevelPvp = configFile.getInt("rates.averageLevelPvp");
			
			//arena
			String maps = configFile.getString("arena.maps");
			if(!maps.isEmpty())
				for(String s: maps.split(","))
					this.arenaMaps.add(Integer.parseInt(s));
			
			this.arenaTime = configFile.getInt("arena.time");
		
			//hdvs
			String items = configFile.getString("hdvs.notInHdv");
			if(!items.isEmpty())
				for(String s: items.split(","))
					this.noInHdv.add(Integer.parseInt(s));
			
			//marchand
			maps = configFile.getString("marchand.maps");
			if(!maps.isEmpty())
				for(String s: maps.split(","))
					this.marchandMaps.add(Integer.parseInt(s));
			
			//collector
			maps = configFile.getString("collector.maps");
			if(!maps.isEmpty())
				for(String s: maps.split(","))
					this.collectorMaps.add(Integer.parseInt(s));
		
			//initialisation des commandes
			this.initializeCommands();
			//initialisation des packets
			try {
				this.initializePlugins();
			} catch(Exception e) { 
				System.out.println(" <> Erreur lors de l'initialisation des plugins : "+e.getMessage());
				System.exit(1);
			}
		} catch(Exception e) {
			System.out.println(" <> Config illisible ou champs manquants: "+e.getMessage());
			System.exit(1);
		}
	}
	
	public void initializeCommands() {
		//totalité des commandes
		Map<String, Command<Player>> playerCommands = new HashMap<>();
		Map<String, Command<Console>> consoleCommands = new HashMap<>();
		
		/**
		 * Commandes des joueurs
		 */
		
		//teleportation zone de départ
		if(configFile.getBoolean("commands.players.teleport.savePos.active")) {
			String name = configFile.getString("commands.players.teleport.savePos.name");
			
			//création de la commande
			Command<Player> command = new Command<Player>(name) {
				
				@Override
				public void action(Player player) {
					player.warpToSavePos();
				}
				
			};
			
			//ajout de condition
			command.addAccess(new CommandAccess<Player>() {
				@Override
				public boolean authorizes(Player player) {
					return player.get_fight() == null;
				}
				
				@Override
				public String getRequiertsMessage() {
					return "Action impossible en combat";
				}
			});
			
			//ajout message de succès
			command.addSuccessMessage("Vous avez bien été téléporté à votre dernière position sauvegardée");
			//ajout aux commmandes
			playerCommands.put(name, command);
		}
		
		//sauvegarde du personnage
		if(configFile.getBoolean("commands.players.save.playerSave.active")) {
			String name = configFile.getString("commands.players.save.playerSave.name");
			
			//création de la commande
			Command<Player> command = new Command<Player>(name) {
				
				@Override
				public void action(Player player) {
					player.save();
				}
				
			};
			
			//mise en place d'un restricteur de temps. 1 save/heure puis 5 minutes d'attente avant relance
			TimeRestricter restricter = 
					new TimeRestricter(RestrictLevel.ACCOUNT, 1, 1, TimeUnit.HOURS, 5, TimeUnit.MINUTES);
			command.attachRestricter(restricter).activeErrorMessage(); //indique le temps restant avant relance
			
			
			//ajout message de succès
			command.addSuccessMessage("Votre personnage a été sauvegardé avec succès.");
			//ajout aux commmandes
			playerCommands.put(name, command);
		}
		
		//informations du serveur
		if(configFile.getBoolean("commands.players.informations.serverInfos.active")) {
			String name = configFile.getString("commands.players.informations.serverInfos.name");
			
			//création de la commande
			Command<Player> command = new Command<Player>(name) {
				
				@Override
				public void action(Player player) {
					player.sendText(Constants.serverInfos());
				}
				
			};
			
			//ajout aux commmandes
			playerCommands.put(name, command);
		}
		
		//liste des commandes
		if(configFile.getBoolean("commands.players.list.commandList.active")) {
			String name = configFile.getString("commands.players.list.commandList.name");
			
			//création de la commande
			Command<Player> command = new Command<Player>(name) {
				
				@Override
				public void action(Player player) {
					StringBuilder commands = new StringBuilder("<b>Liste des commandes disponibles: </b>");
					for(Command<Player> command: World.data.getPlayerCommands().values())
						commands.append(command.getName()).append(", ");
					player.sendText(commands.toString().substring(0,
							commands.toString().length()-2));
				}
				
			};
			//ajout aux commmandes
			playerCommands.put(name, command);
		}
		
		/**
		 * Commandes console
		 */
		//informations du serveur
		if(configFile.getBoolean("commands.console.server.uptime.active")) {
			String name = configFile.getString("commands.console.server.uptime.name");
			
			//création de la commande
			Command<Console> command = new Command<Console>(name) {
				
				@Override
				public void action(Console console) {
					Console.instance.writeln(Constants.serverInfos());
				}
				
			};
			
			//ajout aux commmandes
			consoleCommands.put(name, command);
		}
		
		if(configFile.getBoolean("commands.console.server.reboot.active")) {
			String name = configFile.getString("commands.console.server.reboot.name");
			
			//création de la commande
			Command<Console> command = new Command<Console>(name) {
				
				@Override
				public void action(Console console) {
					World.data.saveData(-1);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {}
					System.exit(0);
				}
				
			};
			
			//ajout aux commmandes
			consoleCommands.put(name, command);
		}
		
		if(configFile.getBoolean("commands.console.server.save.active")) {
			String name = configFile.getString("commands.console.server.save.name");
			
			//création de la commande
			Command<Console> command = new Command<Console>(name) {
				
				@Override
				public void action(Console console) {
					World.data.saveData(-1);
					Console.instance.write(" <> Sauvegarde terminee");
				}
				
			};
			
			//ajout aux commmandes
			consoleCommands.put(name, command);
		}

		//Commande fixe HELP
		Command<Console> command = new Command<Console>("HELP") {
			
			@Override
			public void action(Console console) {
				StringBuilder commands = new StringBuilder("Liste des commandes disponibles: \n");
				for(Command<Console> command: World.data.getConsoleCommands().values())
					commands.append(command.getName()).append(", ");
				Console.instance.writeln(commands.toString().substring(0,
						commands.toString().length()-2));
			}
			
		};
		
		//ajout aux commmandes
		consoleCommands.put("HELP", command);
		
		//ajout des commandes dans les données du serveur
		World.data.getPlayerCommands().putAll(playerCommands);
		World.data.getConsoleCommands().putAll(consoleCommands);
	}
		
	public void initializePlugins() throws Exception {
		try {
			String path = getPathOfJarFile();
			this.loadPackets(new File(path), true);
		} catch(NullPointerException e) {
			this.loadPacketsIntoTheJar();
		} catch(Exception e) {
			this.loadPacketsIntoTheJar();
	    }
	   
		FileFilter filter = new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".jar");
			}
		};

		File[] files = new File("./plugins/").listFiles(filter);
		
		if(files != null) {		
			for(File file : files) {
				if(file != null) {
					try {
					World.data.getOtherPlugins()
						.put(file.getName(), new PluginLoader(file));
					} catch(Exception e) {
						throw new Exception(file.getName() + " : " + e.getMessage());
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void loadPacketsIntoTheJar() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		String i = "fr.edofus.ancestra.evolutive.game.packet.";
		String[] packages = {"account", "basic", "channel", "dialog", "enemy", "environement", 
							 "exchange", "fight", "friend", "game", "group", "guild", "house", 
							 "house.kode", "mount", "object", "spell", "waypoint"};

		for (String packge : packages) {
			for (Class<?> clas : getClasses(i + packge)) {
				Annotation annotation = clas.getAnnotation(Packet.class); 
				if(annotation instanceof Packet) {
					Packet name = (Packet) annotation;
					World.data.getPacketJar().put(name.value(), (PacketParser) clas.newInstance());
				}
			}
		}	
	}
	
	private String getPathOfJarFile() throws Exception {
	    String path = Main.class.getResource(Main.class.getSimpleName() + ".class").getFile();
	    if(ClassLoader.getSystemClassLoader().getResource(path) != null) 
	    	path = ClassLoader.getSystemClassLoader().getResource(path).getFile();
	    File file = new File(path.substring(0, path.lastIndexOf('!')));
	    return new File("").getAbsolutePath()+(new File("").getAbsolutePath().startsWith("/")?"/":"\\")+file.getName();
	}
		
	@SuppressWarnings("deprecation")
	public void loadPackets(File file, boolean who) throws IOException, 
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		if(file == null)
			return;

		JarFile jarFile = new JarFile(new File(file.getPath())); 

		ClassLoader loader = URLClassLoader.newInstance(
		    new URL[] { file.toURI().toURL() },
		    getClass().getClassLoader()
		);

		Enumeration<JarEntry> enumeration = jarFile.entries();

		while(enumeration.hasMoreElements()) {
			JarEntry jarEntry = enumeration.nextElement();
			if(jarEntry.getName().endsWith(".class")) {
				Class<?> localClass = loader.loadClass(jarEntry.getName()
						.replaceAll(".class", "").replaceAll("/", "."));
				Annotation annotation = localClass.getAnnotation(Packet.class); 
				if(annotation instanceof Packet) {
					 Packet name = (Packet) annotation;
					 if(name.value() != null)
						 if(!name.value().equals(""))
							 World.data.getPacketJar().put(name.value(), (PacketParser) localClass.newInstance());
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
	
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
	
		ArrayList<Class> classes = new ArrayList<>();
	
		for (File directory : dirs) 
			classes.addAll(findClasses(directory, packageName));

		return classes.toArray(new Class[classes.size()]);
	}
	
	@SuppressWarnings("rawtypes")
	private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class> classes = new ArrayList<Class>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}
	
	public String getIp() {
		return ip;
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
	public long getFloodTime() {
		return floodTime;
	}

	public String getGameServerIp() {
		return getGameServerIpCrypted();
	}

	public String getMotd() {
		return motd;
	}

	public String getMotdColor() {
		return motdColor;
	}

	public boolean isDebug() {
		return debug;
	}

	public PrintStream getPs() {
		return ps;
	}

	public boolean isPolicy() {
		return policy;
	}

	public int getRealmPort() {
		return realmPort;
	}

	public int getGamePort() {
		return gamePort;
	}

	public int getMaxPlayersPerAccount() {
		return maxPlayersPerAccount;
	}

	public short getStartMap() {
		return startMap;
	}

	public int getStartCell() {
		return startCell;
	}

	public boolean isMultiAccount() {
		return multiAccount;
	}

	public int getStartLevel() {
		return startLevel;
	}

	public int getStartKamas() {
		return startKamas;
	}

	public int getSaveTime() {
		return saveTime;
	}

	public int getRateDrop() {
		return rateDrop;
	}

	public boolean isAllZaaps() {
		return allZaaps;
	}

	public int getLoadDelay() {
		return loadDelay;
	}

	public int getReloadMobDelay() {
		return reloadMobDelay;
	}

	public int getPlayerLimitOnServer() {
		return playerLimitOnServer;
	}

	public boolean isIpLoopBack() {
		return ipLoopBack;
	}

	public int getRateXpPvp() {
		return rateXpPvp;
	}

	public int getAverageLevelPvp() {
		return averageLevelPvp;
	}

	public boolean isMulePvp() {
		return mulePvp;
	}

	public int getRateXpPvm() {
		return rateXpPvm;
	}

	public int getRateKamas() {
		return rateKamas;
	}

	public int getRateHonor() {
		return rateHonor;
	}

	public int getRateXpJob() {
		return rateXpJob;
	}

	public boolean isCustomStartMap() {
		return customStartMap;
	}

	public boolean isUseMobs() {
		return useMobs;
	}

	public boolean isUseIp() {
		return useIp;
	}

	public GameServer getGameServer() {
		return gameServer;
	}

	public LoginServer getRealmServer() {
		return realmServer;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean isCanLog() {
		return canLog;
	}

	public boolean isSaving() {
		return isSaving;
	}

	public boolean isAuraSystem() {
		return auraSystem;
	}

	public ArrayList<Integer> getArenaMaps() {
		return arenaMaps;
	}

	public int getArenaTimer() {
		return arenaTime;
	}

	public ArrayList<Integer> getNoInHdv() {
		return noInHdv;
	}
	
	public ArrayList<Integer> getMarchandMaps() {
		return marchandMaps;
	}
	
	public ArrayList<Integer> getCollectorMaps() {
		return collectorMaps;
	}

	public int getMaxIdleTime() {
		return maxIdleTime;
	}

	public boolean isSocketUseCompactData() {
		return socketUseCompactData;
	}

	public int getSocketTimeCompactData() {
		return socketTimeCompactData;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void setGameServer(GameServer gameServer) {
		this.gameServer = gameServer;
	}

	public void setRealmServer(LoginServer realmServer) {
		this.realmServer = realmServer;
	}

	public void setSaving(boolean isSaving) {
		this.isSaving = isSaving;
	}

	public void setPs(PrintStream ps) {
		this.ps = ps;
	}

	public String getGameServerIpCrypted() {
		return CryptManager.CryptIP(ip)+CryptManager.CryptPort(gamePort);
	}

	public String getDatabaseName() {
		return databaseName;
	}
}
