package game.packet.basic;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("BS")
public class UseSmiley implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().emoticone(packet.substring(2));
	}
}