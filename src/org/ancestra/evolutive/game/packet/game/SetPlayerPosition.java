package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Gp")
public class SetPlayerPosition implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getFight() == null)
			return;
		
		try {
			int cell = Integer.parseInt(packet.substring(2));
			client.getPlayer().getFight().changePlace(client.getPlayer(), cell);
		} catch(NumberFormatException e) {return;}
	}
}