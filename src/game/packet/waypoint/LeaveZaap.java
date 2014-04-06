package game.packet.waypoint;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("WV")
public class LeaveZaap implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().stopZaaping();		
	}
}