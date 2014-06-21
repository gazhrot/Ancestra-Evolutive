package org.ancestra.evolutive.tool.command;

import org.ancestra.evolutive.core.Console;

import java.util.ArrayList;

public class CommandGroupAccess<T> {
	
	private ArrayList<CommandAccess<T>> commandAccess = new ArrayList<>();
	
	public void addAccess(CommandAccess<T> access) {
		this.commandAccess.add(access);
	}
	
	public boolean authorizes(T t) {
		StringBuilder errors = new StringBuilder();
		
		for(CommandAccess<T> access: this.commandAccess) 
			if(!access.authorizes(t)) 
				errors.append(access.getRequiertsMessage()).append("\n");
		
		Console.instance.print(errors.toString(), t);
		
		return errors.toString().isEmpty();
	}
}