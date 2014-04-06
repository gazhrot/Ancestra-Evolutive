package game.packet.friend;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

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