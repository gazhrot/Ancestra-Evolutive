package game.packet.account;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("Af")
public class GetQueuePosition implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int queue = 1;
		int position = 1;
		SocketManager.MULTI_SEND_Af_PACKET(client, position, 1, 1, "1", queue);
	}
}