package fr.edofus.ancestra.evolutive.tool.command;

import java.util.Deque;
import java.util.LinkedList;


import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.core.Console;
import fr.edofus.ancestra.evolutive.core.World;

public class CommandParser {

	public static void parse(String line, Object t) {
		String name;
		String[] parameters = null;
		
		try {
			 String[] split = line.contains(" ") 
					 ? line.split(" ") 
					 : new String[] { line };
			 
			 name = split[0];
			 
			 if(split.length > 1) {
				 line = line.substring(name.length()+1);
				 parameters = line.contains(" ") 
						 ? line.split(" ")
						 : new String[] { line };
			 }
		} catch(Exception e) {
			Console.instance.print("Erreur de syntaxe", t);
			return; 
		}
		
		if(t instanceof Player) {
			Command<Player> command = World.data.getPlayerCommands().get(name);
			
			if(command == null) {
				Console.instance.print("Commande non reconnue", t);
				return; 
			}
			
			if(parameters != null) {
				Deque<String> params = new LinkedList<>();
				for(String param: parameters)
					params.addLast(param);
				
				Parameter<Player> lastParameter = null;
				
				while(params.isEmpty()) {
					Parameter<Player> temporary = command.getParameters().get(params.pop());
					if(temporary == null) {
						if(lastParameter != null)
							lastParameter.action((Player)t);
						else
							command.execute((Player)t);
					} else
						lastParameter = temporary;
				}
			} else 
				command.execute((Player)t);
		} 
		else if (t instanceof Console) {
			Command<Console> command = World.data.getConsoleCommands().get(name);
			
			if(command == null) {
				Console.instance.print("Commande non reconnue", t);
				return; 
			}
			
			if(parameters != null) {
				Deque<String> params = new LinkedList<>();
				for(String param: parameters)
					params.addLast(param);
				
				Parameter<Console> lastParameter = null;
				
				while(params.isEmpty()) {
					Parameter<Console> temporary = command.getParameters().get(params.pop());
					if(temporary == null) {
						if(lastParameter != null)
							lastParameter.action((Console)t);
						else
							command.execute((Console)t);
					} else
						lastParameter = temporary;
				}
			} else 
				command.execute((Console)t);
		}
	}
}
