package org.ancestra.evolutive.game.packet.house.kode;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("KV")
public class Close implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		House.closeCode(client.getPlayer());
	}
}