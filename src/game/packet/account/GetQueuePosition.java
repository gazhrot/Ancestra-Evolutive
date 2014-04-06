package game.packet.account;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("Af")
public class GetQueuePosition implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int queue = 1;
		int position = 1;
		SocketManager.MULTI_SEND_Af_PACKET(client, position, 1, 1, "1", queue);
	}
}