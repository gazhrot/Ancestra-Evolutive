package game.packet.house;

import objects.House;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("hG")
public class Guild implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		House.parseHG(client.getPlayer(), (packet.isEmpty()?null:packet));
	}
}