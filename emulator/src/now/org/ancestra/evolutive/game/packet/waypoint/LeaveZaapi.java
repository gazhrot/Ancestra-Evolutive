package org.ancestra.evolutive.game.packet.waypoint;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("Wv")
public class LeaveZaapi implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().Zaapi_close();
	}
}