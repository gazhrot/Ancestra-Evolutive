package org.ancestra.evolutive.game.packet.friend;


import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("FJ")
public class Join implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
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
		
		switch(packet.charAt(2)) {
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