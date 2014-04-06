package game.packet;

import game.GameClient;
import common.Constants;
import common.SocketManager;
import common.World;

public class PacketHandler {
			
	public static void parsePacket(GameClient client, String packet) { 
		if(!verify(client, packet))
			return;

		System.out.println("Parse : "+packet.subSequence(0, 2));
		World.data.getParsers().get(packet.substring(0, 2)).parse(client, packet);
	}

	private static boolean verify(GameClient client, String packet) {
		if (!client.getFilter().authorizes(Constants.getIp(client.getSession().getRemoteAddress().toString())))
			client.kick();
		
		if(client.getPlayer() != null)
			client.getPlayer().refreshLastPacketTime();
		
		if(packet.length() > 3 && packet.substring(0,4).equalsIgnoreCase("ping"))	{
			SocketManager.GAME_SEND_PONG(client);
			return false;
		}
		if(packet.length() > 4 && packet.substring(0,5).equalsIgnoreCase("qping")) {
			SocketManager.GAME_SEND_QPONG(client);
			return false;
		}
		return true;
	}
}