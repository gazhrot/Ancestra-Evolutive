package fr.edofus.ancestra.evolutive.game.packet.account;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("AP")
public class GetRandomCharacterName implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.REALM_SEND_REQUIRED_APK(client);
	}
}