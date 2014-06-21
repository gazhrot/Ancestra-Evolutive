package org.ancestra.evolutive.tool.command;

public abstract class Parameter<T> extends Command<T> {
	
	public Parameter(int pos, String name) {
		super(name);
	}
}
