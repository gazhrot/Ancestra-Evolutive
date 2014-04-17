package game.packet.game;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("GP")
public class ToogleWings implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().toggleWings(packet.charAt(2));
	}
}