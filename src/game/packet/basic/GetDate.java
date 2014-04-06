package game.packet.basic;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("BD")
public class GetDate implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_SERVER_DATE(client);
		SocketManager.GAME_SEND_SERVER_HOUR(client);
	}
}