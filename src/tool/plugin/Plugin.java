package tool.plugin;


import core.Console;
import core.Server;
import core.World;
import database.Database;

public abstract class Plugin {
	
	public void onEnable() {}
	
	public void onDisable() {}
	
	public void onReload() {}
	
	public Console getConsole() {
		return Console.instance;
	}
	
	public Server getServer() {
		return Server.config;
	}
	
	public World getWorld() {
		return World.data;
	}
	
	public Database getDatabase() {
		return World.database;
	}
}