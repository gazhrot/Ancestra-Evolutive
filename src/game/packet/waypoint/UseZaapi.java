package game.packet.waypoint;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("Wu")
public class UseZaapi implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getDeshonor() >= 2) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "183");
			return;
		}
		
		client.getPlayer().Zaapi_use(packet);		
	}
}