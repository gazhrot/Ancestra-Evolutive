package org.ancestra.evolutive.game.packet.basic;


import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("BD")
public class GetDate implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_SERVER_DATE(client);
		SocketManager.GAME_SEND_SERVER_HOUR(client);
	}
}