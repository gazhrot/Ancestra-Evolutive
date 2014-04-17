package game.packet;//your package

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("")//your packet
public class Example /* your name class */ implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		//your function
	}
}