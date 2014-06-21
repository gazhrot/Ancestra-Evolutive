package org.ancestra.evolutive.tool.command;

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

			if(parameters != null) {
				Deque<String> params = new LinkedList<>();
				for(String param: parameters)
					params.addLast(param);

				Parameter<Player> lastParameter = null;

				while(!params.isEmpty()) {
                    String param = params.pop();
					Parameter<Player> temporary = command.getParameters().get(param);
					if(temporary == null) {
						if(lastParameter != null) {
                            params.addFirst(param);
							lastParameter.action((Player)t, (String[])params.toArray());
                        } else
							command.execute((Player)t, (String[])params.toArray());
					} else
						lastParameter = temporary;
				}
			} else 
				command.execute((Player)t, parameters);
		} 
		else if (t instanceof Console) {
			Command<Console> command = World.data.getConsoleCommands().get(name);

			if(command == null) {
				Console.instance.print("Commande non reconnue.", t);
				return; 
			}

			if(parameters != null && command.isSpecificParams()) {
				Deque<String> params = new LinkedList<>();
				for(String param: parameters)
					params.addLast(param);

				Parameter<Console> lastParameter = null;

				while(params.isEmpty()) {
					Parameter<Console> temporary = command.getParameters().get(params.pop());
					if(temporary == null) {
						if(lastParameter != null)
							lastParameter.action((Console)t, (String[])params.toArray());
						else
							command.execute((Console)t, (String[])params.toArray());
					} else
						lastParameter = temporary;
				}
			} else {
				command.execute((Console)t, parameters);
			}
		}
	}
}