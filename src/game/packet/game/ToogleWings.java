package game.packet.game;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("GP")
public class ToogleWings implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().toggleWings(packet.charAt(2));
	}
}