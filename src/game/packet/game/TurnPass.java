package game.packet.game;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("Gt")
public class TurnPass implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() != null)
			client.getPlayer().get_fight().playerPass(client.getPlayer());
	}
}