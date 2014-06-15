package org.ancestra.evolutive.game.packet.basic;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("BW")
public class WhoIs implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		Player player = World.data.getPersoByName(packet);
		if(player == null) 
			return;
		SocketManager.GAME_SEND_BWK(client.getPlayer(), player.getAccount().getPseudo()+"|1|"+player.getName()+"|"+player.getMap().getSubArea().getArea().getId());
	}
}