package game.packet.waypoint;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("Wv")
public class LeaveZaapi implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().Zaapi_close();
	}
}