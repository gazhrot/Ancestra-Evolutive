package org.ancestra.evolutive.game.packet.friend;

import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("FD")
public class RemoveFriend implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		
		int guid = -1;
		switch(packet.charAt(2))
		{
			case '%'://Nom de perso
				packet = packet.substring(3);
				Player player = World.data.getPlayerByName(packet);
				if(player == null) {//Si P est nul, ou si P est nonNul et P offline
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = player.getAccount().getUUID();
			break;
			case '*'://Pseudo
				packet = packet.substring(3);
				Account account = World.data.getCompteByPseudo(packet);
				if(account == null) {
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = account.getUUID();
			break;
			default:
				packet = packet.substring(2);
				player = World.data.getPlayerByName(packet);
				if(player == null?true:!player.isOnline()) {//Si P est nul, ou si P est nonNul et P offline
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = player.getAccount().getUUID();
			break;
		}
		
		if(guid == -1 || !client.getAccount().isFriendWith(guid)) {
			SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
			return;
		}
		
		client.getAccount().removeFriend(guid);
	}
}