package game.packet;//your package

import game.GameClient;

@Packet("")//your packet
public class Example /* your name class */ implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		//your function
	}
}