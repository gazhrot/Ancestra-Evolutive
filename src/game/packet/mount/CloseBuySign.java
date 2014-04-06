package game.packet.mount;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("Rv")
public class CloseBuySign implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_R_PACKET(client.getPlayer(), "v");
	}
}