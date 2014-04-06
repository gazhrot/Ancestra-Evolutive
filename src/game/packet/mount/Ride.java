package game.packet.mount;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

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