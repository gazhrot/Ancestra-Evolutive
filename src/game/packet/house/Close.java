package game.packet.house;

import objects.House;
import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("hV")
public class Close implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		House.closeBuy(client.getPlayer());
	}
}