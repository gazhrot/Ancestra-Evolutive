package game.packet.waypoint;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("Wv")
public class LeaveZaapi implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().Zaapi_close();
	}
}