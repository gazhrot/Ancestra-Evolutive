package org.ancestra.evolutive.game.packet.account;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("AL")
public class GetCharacters implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.send(client.getAccount().getAccountHelper().getPlayersList());
	}
}