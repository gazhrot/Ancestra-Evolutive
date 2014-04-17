package game.packet.basic;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("BD")
public class GetDate implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_SERVER_DATE(client);
		SocketManager.GAME_SEND_SERVER_HOUR(client);
	}
}