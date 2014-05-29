package org.ancestra.evolutive.game.packet.house;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("hB")
public class Buy implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		House.HouseAchat(client.getPlayer());
	}
}