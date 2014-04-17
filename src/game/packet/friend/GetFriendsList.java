package game.packet.friend;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("FL")
public class GetFriendsList implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_FRIENDLIST_PACKET(client.getPlayer());
	}
}