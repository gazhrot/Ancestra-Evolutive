package game.packet.waypoint;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

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