package org.ancestra.evolutive.game.packet.waypoint;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("WU")
public class UseZaap implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		short id = -1;
		try {
			id = Short.parseShort(packet.substring(2));
		} catch(Exception e) {}
		
		if(id == -1)
			return;
		
		client.getPlayer().useZaap(id);
	}
}