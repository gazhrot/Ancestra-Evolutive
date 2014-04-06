package game.packet.house;

import objects.House;
import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("hB")
public class Buy implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		House.HouseAchat(client.getPlayer());
	}
}