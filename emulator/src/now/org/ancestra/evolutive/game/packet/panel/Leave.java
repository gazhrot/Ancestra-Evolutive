package org.ancestra.evolutive.game.packet.panel;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("dV")
public class Leave implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().setAway(false);
		client.getPlayer().send("dV");
	}
}