package org.ancestra.evolutive.game.packet.group;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("PR")
public class Decline implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().getInviting() == 0)
			return;
		
		client.send("BN");	
		Player player = World.data.getPlayer(client.getPlayer().getInviting());
		
		if(player != null) 
			SocketManager.GAME_SEND_PR_PACKET(player);
		
		player.setInviting(0);
		client.getPlayer().setInviting(0);
	}
}