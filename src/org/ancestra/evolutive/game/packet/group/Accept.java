package org.ancestra.evolutive.game.packet.group;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Group;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("PA")
public class Accept implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().getInviting() == 0)
			return;
		
		Player player = World.data.getPersonnage(client.getPlayer().getInviting());
		
		if(player == null)
			return;
		
		Group group = player.getGroup();
		
		if(group == null) {
			group = new Group(player, client.getPlayer());
			SocketManager.GAME_SEND_GROUP_CREATE(client, group);
			SocketManager.GAME_SEND_PL_PACKET(client, group);
			SocketManager.GAME_SEND_GROUP_CREATE(player.getAccount().getGameClient(),group);
			SocketManager.GAME_SEND_PL_PACKET(player.getAccount().getGameClient(), group);
			player.setGroup(group);
			SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(player.getAccount().getGameClient(), group);
		}else {
			SocketManager.GAME_SEND_GROUP_CREATE(client, group);
			SocketManager.GAME_SEND_PL_PACKET(client, group);
			SocketManager.GAME_SEND_PM_ADD_PACKET_TO_GROUP(group, client.getPlayer());
			group.addPlayer(client.getPlayer());
		}
		
		client.getPlayer().setGroup(group);
		SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(client, group);
		SocketManager.GAME_SEND_PR_PACKET(player);
	}
}