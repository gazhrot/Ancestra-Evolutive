package org.ancestra.evolutive.tool.plugin.packet;

import org.ancestra.evolutive.game.GameClient;

public interface PacketParser {
	public void parse(GameClient client, String packet);
}