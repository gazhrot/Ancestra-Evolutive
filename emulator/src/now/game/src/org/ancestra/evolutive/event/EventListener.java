package org.ancestra.evolutive.event;

public abstract class EventListener {
	
	protected boolean cancelled = false;
	
	/**
	 * An abstract function used when an event are created.
	 * @param the event type who is create.
	 */
	public abstract void call(Event event);
	
	
	/**
	 * Use to set variable cancelled.
	 * @param cancelled must be 'true' to stop an event.
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	/**
	 * If you need to stop an event, use {@link #setCancelled()} to close event with true or to continue.
	 * @return if the event must be closed.
	 */
	public boolean isCancelled() {
		return this.cancelled;
	}
}