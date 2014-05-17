package org.ancestra.evolutive.tool.plugin;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.Database;

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