package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("GR")
public class ReadyFight implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().setReady(packet.substring(2).equalsIgnoreCase("1"));
		client.getPlayer().getFight().onReadyChange();
	}
}