package org.ancestra.evolutive.tool.command;

import org.ancestra.evolutive.client.Admin;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;

import java.util.concurrent.ConcurrentLinkedDeque;

@SuppressWarnings("unchecked")
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
            Command<Player> command = World.data.getPlayerCommands().get(name);

            CommandParser.launch((Player) t, command, parameters);
		}
        else if (t instanceof Console) {
            Command<Console> command = World.data.getConsoleCommands().get(name);
    
            CommandParser.launch((Console) t, command, parameters);
        }
        else if(t instanceof Admin) {
            if(((Admin) t).getGmLvl() < 1)
                return;

            Command<Admin> command = World.data.getAdminCommands().get(name);

            if(command != null && (((Admin) t).getGmLvl() < command.getGmLvl())) {
				Console.instance.print("Vous ne disposez pas du rang de modération neccesaire.", t);
				return;
			}

			CommandParser.launch(t, command, parameters);
		}
	}

	private static <T> void launch(Object t, Command<T> command, String[] parameters) {
        if(t == null)
            return;

        if(command == null) {
            Console.instance.print("Commande non reconnue.", t);
            return;
        }

        if(parameters != null && !command.getParameters().isEmpty()) {
            CommandParser.execute(t, command, parameters);
        } else {
            command.execute((T) t, parameters);
        }
    }
	
	public static <T> void execute(Object t, Command<T> command, String[] parameters) {
		ConcurrentLinkedDeque<String> params = new ConcurrentLinkedDeque<>();
		
		for(String param: parameters)
			params.addLast(param.toUpperCase());
		
		for(String param : params) {
			Parameter<T> parameter = command.getParameters().get(param);
			if(parameter != null) {
				params.remove(param);
				String[] array = {};
					
				try {
					array = params.toArray(array);
				} catch(Exception e) {}

				parameter.execute((T) t, array);
			}
		}
	}
}