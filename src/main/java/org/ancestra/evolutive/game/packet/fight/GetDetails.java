package org.ancestra.evolutive.game.packet.fight;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("fD")
public class GetDetails implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int key = -1;
		try {
			key = Integer.parseInt(packet.substring(2).replace(0x0+"", ""));
		} catch(Exception e) {}
		
		if(key == -1)
			return;
		
		SocketManager.GAME_SEND_FIGHT_DETAILS(client, client.getPlayer().getCurMap().getFights().get(key));
	}
}