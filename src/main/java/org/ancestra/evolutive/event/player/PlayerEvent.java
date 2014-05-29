 package org.ancestra.evolutive.event.player;

import org.ancestra.evolutive.client.Player;

public class PlayerEvent {
	
	private Player player;
	
	public PlayerEvent() {}
	
	public PlayerEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
}