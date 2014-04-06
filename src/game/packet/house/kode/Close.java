package game.packet.house.kode;

import objects.House;
import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("KV")
public class Close implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		House.closeCode(client.getPlayer());
	}
}