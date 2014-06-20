package org.ancestra.evolutive.game.packet.group;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Group;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

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
			if(client.getPlayer().getFollow() != null)
				client.getPlayer().getFollow().getFollowers().remove(client.getPlayer().getUUID());
			SocketManager.GAME_SEND_FLAG_PACKET(client.getPlayer(), player);
			SocketManager.GAME_SEND_PF(client.getPlayer(), "+"+player.getUUID());
			client.getPlayer().setFollow(player);
			player.getFollowers().put(client.getPlayer().getUUID(), client.getPlayer());
		}else 
		if(packet.charAt(2) == '-') {//Ne plus suivre
			SocketManager.GAME_SEND_DELETE_FLAG_PACKET(client.getPlayer());
			SocketManager.GAME_SEND_PF(client.getPlayer(), "-");
			client.getPlayer().setFollow(null);
			player.getFollowers().remove(client.getPlayer().getUUID());
		}
	}
}