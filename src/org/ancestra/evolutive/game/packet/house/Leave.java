package org.ancestra.evolutive.game.packet.house;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.objects.House;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("hQ")
public class Leave implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		House.Leave(client.getPlayer(), packet);
	}
}