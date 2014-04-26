package fr.edofus.ancestra.evolutive.game.packet;//your package

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("")//your packet
public class Example /* your name class */ implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		//your function
	}
}