package game.packet.friend;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("FL")
public class GetFriendsList implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_FRIENDLIST_PACKET(client.getPlayer());
	}
}