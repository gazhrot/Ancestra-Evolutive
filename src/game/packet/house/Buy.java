package game.packet.house;

import objects.House;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("hB")
public class Buy implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		House.HouseAchat(client.getPlayer());
	}
}