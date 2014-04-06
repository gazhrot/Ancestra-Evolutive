package game.packet.group;

import client.Player;

import common.SocketManager;
import common.World;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("PR")
public class Decline implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().getInvitation() == 0)
			return;
		
		client.getPlayer().setInvitation(0);
		SocketManager.GAME_SEND_BN(client);
		
		Player player = World.data.getPersonnage(client.getPlayer().getInvitation());
		
		if(player == null) 
			return;
		
		player.setInvitation(0);
		SocketManager.GAME_SEND_PR_PACKET(player);
	}
}