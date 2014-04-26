package fr.edofus.ancestra.evolutive.game.packet.basic;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("BS")
public class UseSmiley implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().emoticone(packet.substring(2));
	}
}