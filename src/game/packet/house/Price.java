package game.packet.house;

import objects.House;
import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("hS")
public class Price implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		House.SellPrice(client.getPlayer(), packet);
	}
}