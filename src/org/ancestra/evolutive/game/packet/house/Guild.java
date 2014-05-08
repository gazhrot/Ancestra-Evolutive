package org.ancestra.evolutive.game.packet.house;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.objects.House;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("hG")
public class Guild implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		House.parseHG(client.getPlayer(), (packet.isEmpty()?null:packet));
	}
}