package game.packet.account;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("AP")
public class GetRandomCharacterName implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.REALM_SEND_REQUIRED_APK(client);
	}
}