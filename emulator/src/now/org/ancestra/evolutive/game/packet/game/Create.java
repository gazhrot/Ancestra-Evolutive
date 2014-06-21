package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("GC")
public class Create implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() != null)
			client.getPlayer().sendGameCreate();
	}
}