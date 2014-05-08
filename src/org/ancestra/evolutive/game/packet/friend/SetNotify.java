package org.ancestra.evolutive.game.packet.friend;


import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


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