package org.ancestra.evolutive.game.packet.environement;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("eD")
public class SetDirection implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try	{
			if(client.getPlayer().getFight() != null)
				return;
	
			int dir = Integer.parseInt(packet.substring(2));
			
			if(dir > 7 || dir < 0)
				return;
			
			client.getPlayer().setOrientation(dir);
			SocketManager.GAME_SEND_eD_PACKET_TO_MAP(client.getPlayer().getCurMap(),client.getPlayer().getId(),dir);
		} catch(NumberFormatException e) {}
	}
}