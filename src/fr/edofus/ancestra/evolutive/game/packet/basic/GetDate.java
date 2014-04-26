package fr.edofus.ancestra.evolutive.game.packet.basic;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("BD")
public class GetDate implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_SERVER_DATE(client);
		SocketManager.GAME_SEND_SERVER_HOUR(client);
	}
}