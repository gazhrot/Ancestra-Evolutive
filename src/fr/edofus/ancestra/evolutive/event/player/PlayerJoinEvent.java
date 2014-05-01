package fr.edofus.ancestra.evolutive.event.player;

import fr.edofus.ancestra.evolutive.client.Player;

public class PlayerJoinEvent extends PlayerEvent {
	
	public PlayerJoinEvent() {}
	
	public PlayerJoinEvent(Player player) {
		super(player);
	}	
}