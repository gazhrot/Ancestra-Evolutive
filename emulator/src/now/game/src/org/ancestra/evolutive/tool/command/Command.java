package org.ancestra.evolutive.tool.command;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.tool.time.restricter.TimeRestricter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class Command<T> {
	
	private final String name;
	private final String description;
	private final String arguments;
	private int gmLvl = 0;
    private ConcurrentMap<String, Parameter<T>> parameters = new ConcurrentHashMap<>();
    private TimeRestricter restricter;

	public abstract void action(T t, String[] args);
	
	/**
	 * Use for simple command ( Player and Console ).
	 * @param name : name of the command.
	 * @param description : the description of the command.
	 */
	public Command(String name, String description, String arguments) {
		this.name = name.toUpperCase();
		this.description = description;
		this.arguments = arguments;
	}
	
	/**
	 * Use for admin command.
	 * @param name : name of the command.
	 * @param description : the description of the command.
	 * @param gmLvl : level admin of the player requiert.
	 */
	public Command(String name, String description, String arguments, int gmLvl) {
		this(name, description, arguments);
		this.gmLvl = gmLvl;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return the arguments
	 */
	public String getArguments() {
		return arguments;
	}

	public ConcurrentMap<String, Parameter<T>> getParameters() {
		return parameters;
	}
	
	public void addParameter(Parameter<T> parameter) {
		this.parameters.put(parameter.getName(), parameter);
	}
	
	public int getGmLvl() {
		return gmLvl;
	}
	
	public TimeRestricter attachRestricter(TimeRestricter restricter) {
		this.restricter = restricter;
		return restricter;
	}
	
	public void execute(T t, String[] args) {
		if(t instanceof Player && 
				this.restricter != null && !this.restricter.authorizes((Player)t)) {
			return;
		}
		this.action(t, args);
	}
}