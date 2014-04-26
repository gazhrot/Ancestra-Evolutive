package fr.edofus.ancestra.evolutive.game.packet.exchange;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("EL")
public class PutLastCraft implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getCurJobAction() != null)
			client.getPlayer().getCurJobAction().putLastCraftIngredients();
	}
}