package fr.edofus.ancestra.evolutive.tool.plugin;


import fr.edofus.ancestra.evolutive.core.Console;
import fr.edofus.ancestra.evolutive.core.Server;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.database.Database;

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