package game.packet.game;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("Gt")
public class TurnPass implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() != null)
			client.getPlayer().get_fight().playerPass(client.getPlayer());
	}
}