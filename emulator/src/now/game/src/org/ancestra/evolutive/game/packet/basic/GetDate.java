package org.ancestra.evolutive.game.packet.basic;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.game.GameServer;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("BD")
public class GetDate implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.send(GameServer.getServerDate());
		client.send(GameServer.getServerTime());
	}
}