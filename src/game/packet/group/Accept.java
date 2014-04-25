package game.packet.group;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;
import client.Player.Group;

import common.SocketManager;
import core.World;

import game.GameClient;

@Packet("PA")
public class Accept implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().getInvitation() == 0)
			return;
		
		Player player = World.data.getPersonnage(client.getPlayer().getInvitation());
		
		if(player == null)
			return;
		
		Group group = player.getGroup();
		
		if(group == null) {
			group = new Group(player, client.getPlayer());
			SocketManager.GAME_SEND_GROUP_CREATE(client, group);
			SocketManager.GAME_SEND_PL_PACKET(client, group);
			SocketManager.GAME_SEND_GROUP_CREATE(player.get_compte().getGameClient(),group);
			SocketManager.GAME_SEND_PL_PACKET(player.get_compte().getGameClient(), group);
			player.setGroup(group);
			SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(player.get_compte().getGameClient(), group);
		}else {
			SocketManager.GAME_SEND_GROUP_CREATE(client, group);
			SocketManager.GAME_SEND_PL_PACKET(client, group);
			SocketManager.GAME_SEND_PM_ADD_PACKET_TO_GROUP(group, client.getPlayer());
			group.addPerso(client.getPlayer());
		}
		
		client.getPlayer().setGroup(group);
		SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(client, group);
		SocketManager.GAME_SEND_PR_PACKET(player);
	}
}