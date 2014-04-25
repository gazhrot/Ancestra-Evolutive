package game.packet.enemy;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Account;
import client.Player;

import common.SocketManager;
import core.World;

import game.GameClient;

@Packet("iA")
public class AddEnemy implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		
		int guid = -1;
		
		switch(packet.charAt(2))
		{
			case '%'://Nom de perso
				packet = packet.substring(3);
				Player player = World.data.getPersoByName(packet);
				if(player == null) {
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = player.getAccID();
			break;
			case '*'://Pseudo
				packet = packet.substring(3);
				Account account = World.data.getCompteByPseudo(packet);
				if(account == null)	{
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = account.get_GUID();
			break;
			default:
				packet = packet.substring(2);
				player = World.data.getPersoByName(packet);
				if(player == null?true:!player.isOnline())
				{
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = player.get_compte().get_GUID();
			break;
		}
		if(guid == -1) {
			SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
			return;
		}
		client.getAccount().addEnemy(packet, guid);
	}
}