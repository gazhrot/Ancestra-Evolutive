package org.ancestra.evolutive.game.packet;//your package

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("")//your packet
public class Example /* your name class */ implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		//your function
	}
}