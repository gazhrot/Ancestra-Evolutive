package game.packet.group;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;

import common.SocketManager;
import common.World;

import game.GameClient;

@Packet("PR")
public class Decline implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().getInvitation() == 0)
			return;
		
		SocketManager.GAME_SEND_BN(client);		
		Player player = World.data.getPersonnage(client.getPlayer().getInvitation());
		
		if(player != null) 
			SocketManager.GAME_SEND_PR_PACKET(player);
		
		player.setInvitation(0);
		client.getPlayer().setInvitation(0);
	}
}