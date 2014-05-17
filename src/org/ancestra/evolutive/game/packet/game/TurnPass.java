package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Gt")
public class TurnPass implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getFight() != null)
			client.getPlayer().getFight().playerPass(client.getPlayer());
	}
}