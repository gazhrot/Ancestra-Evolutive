package fr.edofus.ancestra.evolutive.game.packet.game;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("GC")
public class Create implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() != null)
			client.getPlayer().sendGameCreate();
	}
}