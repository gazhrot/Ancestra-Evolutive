package org.ancestra.evolutive.game.packet.fight;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("fH")
public class NeedHelp implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getFight() == null)
			return;
		client.getPlayer().getFight().toggleHelp(client.getPlayer().getUUID());
	}
}