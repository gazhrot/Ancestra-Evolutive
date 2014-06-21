package org.ancestra.evolutive.tool.command;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.tool.time.restricter.TimeRestricter;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class Command<T> {
	
	private final String name;
    private final boolean specificParams;
    private Map<String, Parameter<T>> parameters = new HashMap<>();
    private CommandGroupAccess<T> commandGroupAccess = new CommandGroupAccess<>();
    private StringBuilder successMessages = new StringBuilder();
    private TimeRestricter restricter;
    protected Logger logger;


	public abstract void action(T t, String[] args);
	
	public Command(String name) {
		this(name, false);
	}
	
	public Command(String name, boolean specific) {
		this.name = name.toLowerCase();
		this.specificParams = specific;
        logger = (Logger) LoggerFactory.getLogger("Command " + name);
	}
	
	public void addParameter(Parameter<T> parameter) {
		this.parameters.put(parameter.getName(), parameter);
	}
	
	public void addAccess(CommandAccess<T> access) {
		this.commandGroupAccess.addAccess(access);
	}
	
	public void addSuccessMessage(String message) {
		this.successMessages.append(message).append("\n");
	}
	
	public void execute(T t, String[] args) {
		if(this.commandGroupAccess.authorizes(t)) {
			if(t instanceof Player && 
					this.restricter != null && !this.restricter.authorizes((Player)t)) {
                return;
            }
			this.action(t, args);
			logger.info(this.successMessages.toString(), t);
		}
	}
	
	public TimeRestricter attachRestricter(TimeRestricter restricter) {
		this.restricter = restricter;
		return restricter;
	}

	public String getName() {
		return name;
	}

	public Map<String, Parameter<T>> getParameters() {
		return parameters;
	}

	public boolean isSpecificParams() {
		return specificParams;
	}
}