package game.packet.house.kode;

import objects.House;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("KV")
public class Close implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		House.closeCode(client.getPlayer());
	}
}