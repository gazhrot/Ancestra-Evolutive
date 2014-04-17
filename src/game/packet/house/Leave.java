package game.packet.house;

import objects.House;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("hQ")
public class Leave implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		House.Leave(client.getPlayer(), packet);
	}
}