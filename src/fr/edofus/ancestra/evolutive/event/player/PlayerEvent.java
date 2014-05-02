 package fr.edofus.ancestra.evolutive.event.player;

import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.event.Event;

public class PlayerEvent implements Event {
	
	private Player player;
	
	public PlayerEvent() {}
	
	public PlayerEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
}