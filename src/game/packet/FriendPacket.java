package game.packet;

import client.Account;
import client.Player;
import common.SocketManager;
import common.World;

import game.GameClient;

public class FriendPacket {

	public static void parseFriendPacket(GameClient client, String packet) {
		switch(packet.charAt(1))
		{
			case 'A'://Ajouter
				add(client, packet);
			break;
			case 'D'://Effacer un ami
				delete(client, packet);
			break;
			case 'L'://Liste
				SocketManager.GAME_SEND_FRIENDLIST_PACKET(client.getPlayer());
			break;
			case 'O':
				setSee(client, packet);
			break;
			case 'J': //Wife
				wife(client, packet);
			break;
		}
	}

	private static void add(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		
		int guid = -1;
		
		switch(packet.charAt(2))
		{
			case '%'://Nom de perso
				packet = packet.substring(3);
				Player player = World.data.getPersoByName(packet);
				if(player == null?true:!player.isOnline()) {//Si P est nul, ou si P est nonNul et P offline
					SocketManager.GAME_SEND_FA_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = player.getAccID();
			break;
			case '*'://Pseudo
				packet = packet.substring(3);
				Account account = World.data.getCompteByPseudo(packet);
				if(account == null?true:!account.isOnline()) {
					SocketManager.GAME_SEND_FA_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = account.get_GUID();
			break;
			default:
				packet = packet.substring(2);
				player = World.data.getPersoByName(packet);
				if(player == null?true:!player.isOnline()) {//Si P est nul, ou si P est nonNul et P offline
					SocketManager.GAME_SEND_FA_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = player.get_compte().get_GUID();
			break;
		}
		if(guid == -1) {
			SocketManager.GAME_SEND_FA_PACKET(client.getPlayer(), "Ef");
			return;
		}
		client.getAccount().addFriend(guid);
	}
	
	private static void delete(GameClient client, String packet) {
		if(client.getPlayer() == null)return;
		int guid = -1;
		switch(packet.charAt(2))
		{
			case '%'://Nom de perso
				packet = packet.substring(3);
				Player player = World.data.getPersoByName(packet);
				if(player == null) {//Si P est nul, ou si P est nonNul et P offline
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = player.getAccID();
			break;
			case '*'://Pseudo
				packet = packet.substring(3);
				Account account = World.data.getCompteByPseudo(packet);
				if(account == null) {
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = account.get_GUID();
			break;
			default:
				packet = packet.substring(2);
				player = World.data.getPersoByName(packet);
				if(player == null?true:!player.isOnline()) {//Si P est nul, ou si P est nonNul et P offline
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = player.get_compte().get_GUID();
			break;
		}
		if(guid == -1 || !client.getAccount().isFriendWith(guid)) {
			SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
			return;
		}
		client.getAccount().removeFriend(guid);
	}

	private static void setSee(GameClient client, String packet) {
		switch(packet.charAt(2))
		{
			case '-':
				client.getPlayer().SetSeeFriendOnline(false);
				SocketManager.GAME_SEND_BN(client.getPlayer());
			break;
			case'+':
				client.getPlayer().SetSeeFriendOnline(true);
				SocketManager.GAME_SEND_BN(client.getPlayer());
			break;
		}
	}
	
	private static void wife(GameClient client, String packet) {
		Player player = World.data.getPersonnage(client.getPlayer().getWife());
		
		if(player == null) 
			return;
		
		if(!player.isOnline()) {
			if(player.get_sexe() == 0) 
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "140");
			else 
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "139");
			SocketManager.GAME_SEND_FRIENDLIST_PACKET(client.getPlayer());
			return;
		}
		
		switch(packet.charAt(2))
		{
			case 'S'://Teleportation
				if(client.getPlayer().get_fight() != null)
					return;
				else
					client.getPlayer().meetWife(player);
			break;
			case 'C'://Suivre le deplacement
				if(packet.charAt(3) == '+') {//Si lancement de la traque
					if(client.getPlayer()._Follows != null)
						client.getPlayer()._Follows._Follower.remove(client.getPlayer().get_GUID());
					
					SocketManager.GAME_SEND_FLAG_PACKET(client.getPlayer(), player);
					client.getPlayer()._Follows = player;
					player._Follower.put(client.getPlayer().get_GUID(), client.getPlayer());
				} else {//On arrete de suivre
					SocketManager.GAME_SEND_DELETE_FLAG_PACKET(client.getPlayer());
					client.getPlayer()._Follows = null;
					player._Follower.remove(client.getPlayer().get_GUID());
				}
			break;
		}
	} 
}