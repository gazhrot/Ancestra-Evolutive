package game.packet.waypoint;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("WV")
public class LeaveZaap implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().stopZaaping();		
	}
}