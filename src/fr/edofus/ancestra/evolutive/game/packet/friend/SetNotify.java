package fr.edofus.ancestra.evolutive.game.packet.friend;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

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