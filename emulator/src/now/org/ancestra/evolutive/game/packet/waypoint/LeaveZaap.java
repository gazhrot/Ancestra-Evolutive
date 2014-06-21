package org.ancestra.evolutive.game.packet.waypoint;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("WV")
public class LeaveZaap implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().stopZaaping();		
	}
}