package org.ancestra.evolutive.game.packet.basic;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("BY")
public class Statut implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		switch(packet.charAt(2))
		{
			case 'A': //Absent
				if(client.getPlayer().isAbsent()) {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "038");
					client.getPlayer().setAbsent(false);
				}else {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "037");
					client.getPlayer().setAbsent(true);
				}
			break;
			case 'I': //Invisible
				if(client.getPlayer().isInvisible())	{
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "051");
					client.getPlayer().setInvisible(false);
				}else {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "050");
					client.getPlayer().setInvisible(true);
				}
			break;
		}
	}
}