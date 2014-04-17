package game.packet.group;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;
import client.Player.Group;

import common.SocketManager;
import common.World;

import game.GameClient;

@Packet("PF")
public class Follow implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		Group group = client.getPlayer().getGroup();

		if(group == null)
			return;
		
		int toFollow = -1;
		
		try	{
			toFollow = Integer.parseInt(packet.substring(3));
		} catch(NumberFormatException e){return;};
		
		if(toFollow == -1) 
			return;
		
		Player player = World.data.getPersonnage(toFollow);
		
		if(player == null || !player.isOnline()) 
			return;
		
		if(packet.charAt(2) == '+') {//Suivre
			if(client.getPlayer()._Follows != null)
				client.getPlayer()._Follows._Follower.remove(client.getPlayer().get_GUID());
			SocketManager.GAME_SEND_FLAG_PACKET(client.getPlayer(), player);
			SocketManager.GAME_SEND_PF(client.getPlayer(), "+"+player.get_GUID());
			client.getPlayer()._Follows = player;
			player._Follower.put(client.getPlayer().get_GUID(), client.getPlayer());
		}else 
		if(packet.charAt(2) == '-') {//Ne plus suivre
			SocketManager.GAME_SEND_DELETE_FLAG_PACKET(client.getPlayer());
			SocketManager.GAME_SEND_PF(client.getPlayer(), "-");
			client.getPlayer()._Follows = null;
			player._Follower.remove(client.getPlayer().get_GUID());
		}
	}
}