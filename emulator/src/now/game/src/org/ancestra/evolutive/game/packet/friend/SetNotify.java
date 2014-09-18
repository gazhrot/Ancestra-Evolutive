package org.ancestra.evolutive.game.packet.friend;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("FI")
public class SetNotify implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		switch(packet.charAt(2)) {
		case '-':
			client.getPlayer().setShowFriendConnection(false);
			client.send("BN");
			break;
		case'+':
			client.getPlayer().setShowFriendConnection(true);
			client.send("BN");
			break;
		}
	}
}