package fr.edofus.ancestra.evolutive.event;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import fr.edofus.ancestra.evolutive.enums.Priority;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

public @interface EventHandler {
	
	public abstract Priority priority();
}