package game.packet.group;

import client.Player;
import client.Player.Group;

import common.SocketManager;
import common.World;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

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
			for(Player T : group.getPersos()) {
				if(T.get_GUID() == player.get_GUID()) 
					continue;
				if(T._Follows != null)
					T._Follows._Follower.remove(client.getPlayer().get_GUID());
				SocketManager.GAME_SEND_FLAG_PACKET(T, player);
				SocketManager.GAME_SEND_PF(T, "+"+player.get_GUID());
				T._Follows = player;
				player._Follower.put(T.get_GUID(), T);
			}
		}else 
		if(packet.charAt(2) == '-') {//Ne plus suivre
			for(Player p : group.getPersos()) {
				if(p.get_GUID() == player.get_GUID()) 
					continue;
				SocketManager.GAME_SEND_DELETE_FLAG_PACKET(p);
				SocketManager.GAME_SEND_PF(p, "-");
				p._Follows = null;
				player._Follower.remove(p.get_GUID());
			}
		}
	}
}