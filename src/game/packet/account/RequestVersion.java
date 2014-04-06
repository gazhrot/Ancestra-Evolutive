package game.packet.account;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("AV")
public class RequestVersion implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_AV0(client);
	}
}