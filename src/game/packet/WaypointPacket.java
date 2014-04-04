package game.packet;

import common.SocketManager;

import game.GameClient;
import game.packet.handler.Packet;

public class WaypointPacket {

	@Packet("WU")
	public static void use(GameClient client, String packet) {
		short id = -1;
		try {
			id = Short.parseShort(packet.substring(2));
		} catch(Exception e) {}
		
		if(id == -1)
			return;
		
		client.getPlayer().useZaap(id);
	}
	
	@Packet("Wu")
	public static void useZaapi(GameClient client, String packet) {
		if(client.getPlayer().getDeshonor() >= 2) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "183");
			return;
		}
		
		client.getPlayer().Zaapi_use(packet);
	}
	
	@Packet("WV")
	public static void leave(GameClient client, String packet) {
		client.getPlayer().stopZaaping();
	}
	
	@Packet("Wv")
	public static void leaveZaapi(GameClient client, String packet) {
		client.getPlayer().Zaapi_close();
	}
}