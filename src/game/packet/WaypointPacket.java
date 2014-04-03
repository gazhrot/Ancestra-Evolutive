package game.packet;

import common.SocketManager;

import game.GameClient;

public class WaypointPacket {

	public static void parseWaypointPacket(GameClient client, String packet) {
		switch(packet.charAt(1))
		{
			case 'U'://Use
				use(client, packet);
			break;
			case 'u'://use zaapi
				useZaapi(client, packet);
			break;
			case 'v'://quitter zaapi
				client.getPlayer().Zaapi_close();
			break;
			case 'V'://Quitter
				client.getPlayer().stopZaaping();
			break;
		}
	}

	private static void use(GameClient client, String packet) {
		short id = -1;
		try {
			id = Short.parseShort(packet.substring(2));
		} catch(Exception e) {}
		
		if(id == -1)
			return;
		
		client.getPlayer().useZaap(id);
	}
	
	private static void useZaapi(GameClient client, String packet) {
		if(client.getPlayer().getDeshonor() >= 2) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "183");
			return;
		}
		
		client.getPlayer().Zaapi_use(packet);
	}
}