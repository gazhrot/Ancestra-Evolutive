package org.ancestra.evolutive.game.packet.dialog;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("DV")
public class Leave implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
		if(client.getPlayer().getIsTalkingWith() != 0)
			client.getPlayer().setIsTalkingWith(0);
	}
}