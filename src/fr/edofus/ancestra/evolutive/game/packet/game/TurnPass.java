package fr.edofus.ancestra.evolutive.game.packet.game;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Gt")
public class TurnPass implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() != null)
			client.getPlayer().get_fight().playerPass(client.getPlayer());
	}
}