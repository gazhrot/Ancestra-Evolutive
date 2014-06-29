package org.ancestra.evolutive.core;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.ancestra.evolutive.client.Admin;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.CryptManager;
import org.ancestra.evolutive.event.Event;
import org.ancestra.evolutive.event.EventListener;
import org.ancestra.evolutive.event.player.PlayerJoinEvent;
import org.ancestra.evolutive.game.GameServer;
import org.ancestra.evolutive.login.LoginServer;
import org.ancestra.evolutive.tool.command.Command;
import org.ancestra.evolutive.tool.command.Parameter;
import org.ancestra.evolutive.tool.plugin.PluginLoader;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;
import org.ancestra.evolutive.tool.time.restricter.RestrictLevel;
import org.ancestra.evolutive.tool.time.restricter.TimeRestricter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Server {
	
	public static Server config = new Server();
    //Config
    private static final Config configFile = ConfigFactory.parseFile(new File("config.conf"));
    public static final long startTime = System.currentTimeMillis();


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
    private int port;
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
    public static final boolean regenLifeWhenOffline = configFile.getBoolean("server.regenLifeWhenOffline");
	
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
	private int playerLimitOnServer;

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
	

	public void initialize() {	
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
            this.port = configFile.getInt("database.port");
			
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

		World.events.add(PlayerJoinEvent.class, new EventListener() {

			@Override
			public void call(Event event) {
				if(event instanceof PlayerJoinEvent) {
					PlayerJoinEvent event1 = (PlayerJoinEvent) event;
					System.out.println("- Le joueur " + event1.getPlayer().getName() + " vient de se connecte !");
				}
			}			
		});
		
		//totalite des commandes
		Map<String, Command<Player>> playerCommands = new HashMap<>();
		Map<String, Command<Console>> consoleCommands = new HashMap<>();
		
		/**
		 * Commandes des joueurs
		 */
		
		//teleportation zone de depart
		if(configFile.getBoolean("commands.players.teleport.savePos.active")) {
			String name = configFile.getString("commands.players.teleport.savePos.name").toUpperCase();
			
			//creation de la commande
			Command<Player> command = new Command<Player>(name, "Téléporte votre joueur à votre dernière position sauvegarder.") {
				
				@Override
				public void action(Player player, String[] args) {
					if(player.getFight() != null) {
						player.sendText("Action impossible en combat");
						return;
					}
					player.warpToSavePos();
				}
				
			};
		
			
			//ajout message de succes
			command.addSuccessMessage("Vous avez bien ete teleporte a votre derniere position sauvegardee");
			//ajout aux commmandes
			playerCommands.put(name, command);
		}
		
		//sauvegarde du personnage
		if(configFile.getBoolean("commands.players.save.playerSave.active")) {
			String name = configFile.getString("commands.players.save.playerSave.name").toUpperCase();
			
			//creation de la commande
			Command<Player> command = new Command<Player>(name, "Sauvegarde votre joueur actuel.") {
				
				@Override
				public void action(Player player, String[] args) {
					player.save();
				}
				
			};
			
			//mise en place d'un restricteur de temps. 1 save/heure puis 5 minutes d'attente avant relance
			TimeRestricter restricter = 
					new TimeRestricter(RestrictLevel.ACCOUNT, 1, 1, TimeUnit.HOURS, 5, TimeUnit.MINUTES);
			command.attachRestricter(restricter).activeErrorMessage(); //indique le temps restant avant relance
			
			
			//ajout message de succes
			command.addSuccessMessage("Votre personnage a ete sauvegarde avec succes.");
			//ajout aux commmandes
			playerCommands.put(name, command);
		}
		
		//informations du serveur
		if(configFile.getBoolean("commands.players.informations.serverInfos.active")) {
			String name = configFile.getString("commands.players.informations.serverInfos.name").toUpperCase();
			
			//creation de la commande
			Command<Player> command = new Command<Player>(name, "Affiche les informations du server.") {
				
				@Override
				public void action(Player player, String[] args) {
					player.sendText(Constants.serverInfos());
				}
				
			};
			
			//ajout aux commmandes
			playerCommands.put(name, command);
		}
		
		//liste des commandes
		if(configFile.getBoolean("commands.players.list.commandList.active")) {
			String name = configFile.getString("commands.players.list.commandList.name");
			
			//creation de la commande
			Command<Player> command = new Command<Player>(name, null) {
				
				@Override
				public void action(Player player, String[] args) {
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
			String name = configFile.getString("commands.console.server.uptime.name").toUpperCase();
			
			//creation de la commande
			Command<Console> command = new Command<Console>(name, "Affiche les informations du server.") {
				
				@Override
				public void action(Console console, String[] args) {
					Console.instance.writeln(Constants.serverInfos());
				}
				
			};
			
			//ajout aux commmandes
			consoleCommands.put(name, command);
		}
		
		if(configFile.getBoolean("commands.console.server.reboot.active")) {
			String name = configFile.getString("commands.console.server.reboot.name").toUpperCase();
			
			//creation de la commande
			Command<Console> command = new Command<Console>(name, "Sauvegarde puis ferme le server.") {
				
				@Override
				public void action(Console console, String[] args) {
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
			String name = configFile.getString("commands.console.server.save.name").toUpperCase();
			
			//creation de la commande
			Command<Console> command = new Command<Console>(name, "Sauvegarde le server.") {
				
				@Override
				public void action(Console console, String[] args) {
					World.data.saveData(-1);
					Console.instance.write(" <> Sauvegarde terminee");
				}
				
			};
			
			//ajout aux commmandes
			consoleCommands.put(name, command);
		}

		//creation de la commande
		Command<Console> command = new Command<Console>("PLUGIN", null) {
			@Override
			public void action(Console t, String[] args) {
				Console.instance.println("Paramètre non indiqué : ADD, SHOW");
			}
		};		
		command.addParameter(new Parameter<Console>("SHOW", "Affiche les différents plug-ins actuellement actif.") {

			@Override
			public void action(Console t, String[] args) {
				if(World.data.getOtherPlugins().isEmpty()) {
					Console.instance.writeln("Aucun plug-in est actuellement actif.");
					return;
				}
				
				Console.instance.writeln("Liste des plug-ins actif :");
				
				for(Entry<String, PluginLoader> plugin : World.data.getOtherPlugins().entrySet())
					Console.instance.writeln("--> " + plugin.getKey());					
			}
			
		});

		command.addParameter(new Parameter<Console>("ADD", "Permet d'ajouté un plug-in au server.") {

			@Override
			public void action(Console console, String[] args) {
				if(args == null) {
					Console.instance.writeln("Aucun argument défini. Merci d'indiquer le nom du fichier compilé.d");
					return;
				}	
				
				final String name = args[0].replace(".jar", "");
				
				FilenameFilter filter = new FilenameFilter() {
					@Override
					public boolean accept(File arg0, String arg1) {
						return arg1.equals(name + ".jar");
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
								Console.instance.writeln("Erreur lors de l'execution du fichier en question.");
							}
						}
					}
				} else {
					Console.instance.writeln("Le fichier " + name +".jar n'existe pas !");
				}	
			}	
		});
				
		//ajout aux commmandes
		consoleCommands.put("PLUGIN", command);
		
		//Commande fixe HELP
		command = new Command<Console>("HELP", null) {
			
			@Override
			public void action(Console console, String[] args) {
				StringBuilder commands = new StringBuilder("Liste des commandes disponibles : \n");
				for(Command<Console> command: World.data.getConsoleCommands().values()) {
					if(command == null || (command.getDescription() == null && command.getParameters().isEmpty()))
						continue;
					if(!command.getParameters().isEmpty()) {
						for(Entry<String, Parameter<Console>> parameter : command.getParameters().entrySet()) {
							commands.append("-> ").append(command.getName()).append(" ").append(parameter.getKey()).append((parameter.getValue().getDescription() != null ? " - " + parameter.getValue().getDescription() : "")).append("\n");
						}
					} else {
						commands.append("-> ").append(command.getName()).append((command.getDescription() != null ? " - " + command.getDescription() : "")).append("\n");
					}
				}
					
				Console.instance.writeln(commands.toString().substring(0, commands.toString().length() - 2));
			}
			
		};
		
		//ajout aux commmandes
		consoleCommands.put("HELP", command);
		
		//ajout des commandes dans les donnees du serveur
		World.data.getPlayerCommands().putAll(playerCommands);
		World.data.getAdminCommands().putAll(Admin.initialize());
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
			@Override
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
		String i = "org.ancestra.evolutive.game.packet.";
		String[] packages = {"account", "basic", "channel", "dialog", "enemy", "environement", 
							 "exchange", "fight", "friend", "game", "group", "guild", "house", 
							 "house.kode", "mount", "object", "panel", "spell", "waypoint"};

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

    public int getPort(){
        return this.port;
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
