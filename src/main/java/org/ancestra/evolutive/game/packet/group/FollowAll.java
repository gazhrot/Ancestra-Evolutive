package org.ancestra.evolutive.game.packet.group;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Group;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("PG")
public class FollowAll implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		Group group = client.getPlayer().getGroup();
		
		if(group == null)
			return;
		
		int toFollow = -1;
		try	{
			toFollow = Integer.parseInt(packet.substring(3));
		} catch(NumberFormatException e) {return;}
		
		if(toFollow == -1) 
			return;
		
		Player player = World.data.getPersonnage(toFollow);
		
		if(player == null || !player.isOnline()) 
			return;
		
		if(packet.charAt(2) == '+') {//Suivre
			for(Player T : group.getPlayers()) {
				if(T.getId() == player.getId())
					continue;
				if(T.getFollow() != null)
					T.getFollow().getFollowers().remove(client.getPlayer().getId());
				SocketManager.GAME_SEND_FLAG_PACKET(T, player);
				SocketManager.GAME_SEND_PF(T, "+"+player.getId());
				T.setFollow(player);
				player.getFollowers().put(T.getId(), T);
			}
		}else 
		if(packet.charAt(2) == '-') {//Ne plus suivre
			for(Player p : group.getPlayers()) {
				if(p.getId() == player.getId())
					continue;
				SocketManager.GAME_SEND_DELETE_FLAG_PACKET(p);
				SocketManager.GAME_SEND_PF(p, "-");
				p.setFollow(null);
				player.getFollowers().remove(p.getId());
			}
		}
	}
}