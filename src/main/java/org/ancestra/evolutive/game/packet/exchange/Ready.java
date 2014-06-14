package org.ancestra.evolutive.game.packet.exchange;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("EK")
public class Ready implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getCurJobAction() != null) {
			if(!client.getPlayer().getCurJobAction().isCraft())
				return;
			client.getPlayer().getCurJobAction().startCraft(client.getPlayer());
		}
		if(client.getPlayer().getCurExchange() == null)
			return;
		client.getPlayer().getCurExchange().toogleOK(client.getPlayer().getId());
	}
}