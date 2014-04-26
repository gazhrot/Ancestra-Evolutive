package fr.edofus.ancestra.evolutive.game.packet.house;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.House;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("hV")
public class Close implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		House.closeBuy(client.getPlayer());
	}
}