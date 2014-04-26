package fr.edofus.ancestra.evolutive.tool.plugin.packet;

import fr.edofus.ancestra.evolutive.game.GameClient;

public interface PacketParser {
	public void parse(GameClient client, String packet);
}