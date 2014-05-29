package org.ancestra.evolutive.event.player;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.event.Event;

public class PlayerJoinEvent extends PlayerEvent implements Event {
	
	public PlayerJoinEvent() {}
	
	public PlayerJoinEvent(Player player) {
		super(player);
	}	
}