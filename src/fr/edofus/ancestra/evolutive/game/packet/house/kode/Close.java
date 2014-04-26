package fr.edofus.ancestra.evolutive.game.packet.house.kode;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.House;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("KV")
public class Close implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		House.closeCode(client.getPlayer());
	}
}