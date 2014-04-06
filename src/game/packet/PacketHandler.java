package game.packet;

import java.io.IOException;

import game.GameClient;
import common.Constants;
import common.SocketManager;
import common.World;
import core.Server;

public class PacketHandler {
			
	public static void parsePacket(GameClient client, String packet) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException { 
		if(!verify(client, packet))
			return;
		
		/** Les plugins avant les packages. **/
		String prefix = (String) packet.subSequence(0, 2);		
		PacketParser parser = Server.config.getPluginPacket(prefix);
		
		if(parser == null) {
			parser = World.data.getParsers().get(prefix);
			if(parser != null)
				parser.parse(client, packet);
		}else {
			parser.parse(client, packet);
		}
		
		/** Les packages avant les plugins. **/
		/*String prefix = (String) packet.subSequence(0, 2);		
		PacketParser parser = World.data.getParsers().get(prefix);
		
		if(parser == null) {
			parser = Server.config.getPluginPacket(prefix);
			if(parser != null)
				parser.parse(client, packet);
		}else {
			parser.parse(client, packet);
		}*/
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