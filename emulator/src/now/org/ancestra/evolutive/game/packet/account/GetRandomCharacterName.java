package org.ancestra.evolutive.game.packet.account;


import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("AP")
public class GetRandomCharacterName implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.REALM_SEND_REQUIRED_APK(client);
	}
}