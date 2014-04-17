package game.packet.exchange;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("EL")
public class PutLastCraft implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getCurJobAction() != null)
			client.getPlayer().getCurJobAction().putLastCraftIngredients();
	}
}