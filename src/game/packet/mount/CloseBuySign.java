package game.packet.mount;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("Rv")
public class CloseBuySign implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_R_PACKET(client.getPlayer(), "v");
	}
}