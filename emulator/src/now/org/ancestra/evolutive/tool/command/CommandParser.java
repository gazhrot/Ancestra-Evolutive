package org.ancestra.evolutive.tool.command;

import org.ancestra.evolutive.client.Admin;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map.Entry;

public class CommandParser {

	public static void parse(String line, Object t) {
		String name;
		String[] parameters = null;

		try {
			 String[] split = line.contains(" ") ? line.split(" ") 
				: new String[] { line };

			 name = split[0].toUpperCase();

			 if(split.length > 1) {
				 line = line.substring(name.length() + 1);
				 parameters = line.contains(" ") ? line.split(" ")
						 : new String[] { line };
			 }
		} catch(Exception e) {
			Console.instance.print("Erreur de syntaxe.", t);
			return; 
		}

		if(t instanceof Player) {
			Command<Player> command = null;
			
			for(Entry<String, Command<Player>> c: World.data.getPlayerCommands().entrySet()) {
				if(c.getKey().equalsIgnoreCase(name) || c.getValue().getName().equalsIgnoreCase(name)) {
					command = c.getValue();
					break;
				}
			}
			
			if(command == null) {
				Console.instance.print("Commande non reconnue.", t);
				return; 
			}
			
			
			if(parameters != null && !command.getParameters().isEmpty()) {
				Deque<String> params = new LinkedList<>();
				
				for(String param: parameters)
					params.addLast(param.toUpperCase());

				for(String param : params) {
					Parameter<Player> parameter = command.getParameters().get(param);
					if(parameter != null) {
						params.remove(param);
						parameter.execute((Player) t, (String[]) params.toArray());
					}
				}
			} else {
				command.execute((Player) t, parameters);
			}
		} else if(t instanceof Admin) {			
			Command<Admin> command = World.data.getAdminCommands().get(name);
			System.out.println(name);
			if(command == null) {
				Console.instance.print("Commande non reconnue.", t);
				return; 
			}	
			
			if(((Admin) t).getGmLvl() < command.getGmLvl()) {
				if(((Admin) t).getGmLvl() <= 1) 
					return;
				Console.instance.print("Vous ne disposez pas du rang de modération neccésaire.", t);
				return;
			}
			
			if(parameters != null && !command.getParameters().isEmpty()) {
				Deque<String> params = new LinkedList<>();
				
				for(String param: parameters)
					params.addLast(param.toUpperCase());

				for(String param : params) {
					Parameter<Admin> parameter = command.getParameters().get(param);
					if(parameter != null) {
						params.remove(param);
						String[] array = {};
						try {
							array = (String[]) params.toArray();
						} catch(Exception e) {}
						parameter.execute((Admin) t, array);
					}
				}
			} else {
				command.execute((Admin) t, parameters);
			}
		} else if (t instanceof Console) {
			Command<Console> command = World.data.getConsoleCommands().get(name);

			if(command == null) {
				Console.instance.print("Commande non reconnue.", t);
				return; 
			}

			if(parameters != null && !command.getParameters().isEmpty()) {
				Deque<String> params = new LinkedList<>();
				
				for(String param: parameters)
					params.addLast(param.toUpperCase());

				for(String param : params) {
					Parameter<Console> parameter = command.getParameters().get(param);
					if(parameter != null) {
						params.remove(param);
						parameter.execute((Console) t, (String[]) params.toArray());
					}
				}
			} else {
				command.execute((Console) t, parameters);
			}
		}
	}
}