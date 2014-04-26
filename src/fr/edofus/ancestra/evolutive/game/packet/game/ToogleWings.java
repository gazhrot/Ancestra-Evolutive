package fr.edofus.ancestra.evolutive.game.packet.game;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("GP")
public class ToogleWings implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().toggleWings(packet.charAt(2));
	}
}