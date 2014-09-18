package org.ancestra.evolutive.game.packet.account;


import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("Af")
public class GetQueuePosition implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int queue = 1;
		int position = 1;
		SocketManager.MULTI_SEND_Af_PACKET(client, position, 1, 1, "1", queue);
	}
}