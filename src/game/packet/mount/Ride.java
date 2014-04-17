package game.packet.mount;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("Rr")
public class Ride implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_lvl()<60 || client.getPlayer().getMount() == null || !client.getPlayer().getMount().isMountable() || client.getPlayer()._isGhosts) {
			SocketManager.GAME_SEND_Re_PACKET(client.getPlayer(),"Er", null);
			return;
		}
		
		client.getPlayer().toogleOnMount();
	}
}