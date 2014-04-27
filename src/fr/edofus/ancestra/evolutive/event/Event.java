package fr.edofus.ancestra.evolutive.event;

public abstract class Event {
	
	private String name;

	public Event() {}

	public String getEventName() {
		if(this.name == null)
			this.name = getClass().getSimpleName();
		return this.name;
	}
}