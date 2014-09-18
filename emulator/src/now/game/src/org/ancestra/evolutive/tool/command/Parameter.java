package org.ancestra.evolutive.tool.command;

public abstract class Parameter<T> extends Command<T> {
	
	public Parameter(String name, String description, String arguments) {
		super(name, description, arguments);
	}
	
	public Parameter(String name, String description, String arguments, int gm) {
		super(name, description, arguments, gm);
	}
}
