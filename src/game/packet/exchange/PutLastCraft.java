package game.packet.exchange;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("EL")
public class PutLastCraft implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getCurJobAction() != null)
			client.getPlayer().getCurJobAction().putLastCraftIngredients();
	}
}