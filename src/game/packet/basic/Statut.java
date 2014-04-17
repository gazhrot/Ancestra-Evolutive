package game.packet.basic;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("BY")
public class Statut implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		switch(packet.charAt(2))
		{
			case 'A': //Absent
				if(client.getPlayer()._isAbsent) {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "038");
					client.getPlayer()._isAbsent = false;
				}else {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "037");
					client.getPlayer()._isAbsent = true;
				}
			break;
			case 'I': //Invisible
				if(client.getPlayer()._isInvisible)	{
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "051");
					client.getPlayer()._isInvisible = false;
				}else {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "050");
					client.getPlayer()._isInvisible = true;
				}
			break;
		}
	}
}