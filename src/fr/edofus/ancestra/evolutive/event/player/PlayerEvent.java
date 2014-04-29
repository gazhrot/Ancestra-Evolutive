 package fr.edofus.ancestra.evolutive.event.player;

import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.event.Event;

public class PlayerEvent extends Event {
	
	private Player player;
	
	public PlayerEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
}