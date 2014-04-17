package game.packet.account;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("AP")
public class GetRandomCharacterName implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.REALM_SEND_REQUIRED_APK(client);
	}
}