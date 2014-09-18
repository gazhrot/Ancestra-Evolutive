package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("GP")
public class ToogleWings implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().toggleWings(packet.charAt(2));
	}
}