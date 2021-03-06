package org.ancestra.evolutive.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Events {
	Map<Class<?>, Set<EventListener>> listeners = new HashMap<Class<?>, Set<EventListener>>();

	public void add(Class<?> zClass, EventListener eventListener) {
		if(!listeners.containsKey(zClass)) 
			listeners.put(zClass, new HashSet<EventListener>(2));
		listeners.get(zClass).add(eventListener);
    }

	public boolean call(Event event) {
		boolean cancelled = false;
		for(EventListener listener : listeners.get(event.getClass())) {
			try {
				listener.call(event);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(listener.isCancelled())
				cancelled = true;
		}
		return cancelled;
	}
}