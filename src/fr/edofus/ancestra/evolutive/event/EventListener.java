package fr.edofus.ancestra.evolutive.event;

public interface EventListener {
	
	public abstract void call(Event event);
	
	public boolean isCancelled();
}