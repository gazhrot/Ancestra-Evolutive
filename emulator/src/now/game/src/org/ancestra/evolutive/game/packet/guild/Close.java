package org.ancestra.evolutive.game.packet.guild;


import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("gV")
public class Close implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_gV_PACKET(client.getPlayer());
	}
}