package fr.edofus.ancestra.evolutive.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Events {
	Map<Event, Set<EventListener>> listeners = new HashMap<Event, Set<EventListener>>();

	public void add(Event event, EventListener listener) {
		if(!listeners.containsKey(event)) 
			listeners.put(event, new HashSet<EventListener>(2));
		listeners.get(event).add(listener);
    }

	public boolean call(Event event) {
		boolean cancelled = false;
		for(Entry<Event, Set<EventListener>> entry : listeners.entrySet()) {
			if(entry.getKey().getClass().getName().equals(event.getClass().getName())) {
				for(EventListener listener: entry.getValue()) {
					listener.call(event);
					if(listener.isCancelled())
						cancelled = true;
				}
			}
		}
		return cancelled;
	}
	
	public Event getEventClass(Class<?> zClass) {
		try {
			return (Event) zClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}	
}