package game.packet.friend;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("FI")
public class SetNotify implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		switch(packet.charAt(2)) {
			case '-':
				client.getPlayer().SetSeeFriendOnline(false);
				SocketManager.GAME_SEND_BN(client.getPlayer());
			break;
			case'+':
				client.getPlayer().SetSeeFriendOnline(true);
				SocketManager.GAME_SEND_BN(client.getPlayer());
			break;
		}
	}
}