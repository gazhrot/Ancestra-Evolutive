package fr.edofus.ancestra.evolutive.event;

public abstract class Event {
	
	private String name;
	private final boolean async;

	public Event() {
		this(false);
	}

	public Event(boolean isAsync) {
		this.async = isAsync;
	}

	public String getEventName() {
		if(this.name == null)
			this.name = getClass().getSimpleName();
		return this.name;
	}

	public final boolean isAsynchronous() {
		return this.async;
	}
}