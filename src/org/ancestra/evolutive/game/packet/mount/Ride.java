package org.ancestra.evolutive.game.packet.mount;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Rr")
public class Ride implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getLevel()<60 || client.getPlayer().getMount() == null || !client.getPlayer().getMount().isMountable() || client.getPlayer().isGhosts()) {
			SocketManager.GAME_SEND_Re_PACKET(client.getPlayer(),"Er", null);
			return;
		}
		
		client.getPlayer().toogleOnMount();
	}
}