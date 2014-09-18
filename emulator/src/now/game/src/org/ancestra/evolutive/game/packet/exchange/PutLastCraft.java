package org.ancestra.evolutive.game.packet.exchange;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("EL")
public class PutLastCraft implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getCurJobAction() != null)
			client.getPlayer().getCurJobAction().putLastCraftIngredients();
	}
}