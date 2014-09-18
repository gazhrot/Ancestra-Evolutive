package org.ancestra.evolutive.game.packet.dialog;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.game.actions.GameActionManager;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("DV")
public class Leave implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.send("DV");
        client.getPlayer().setIsTalkingWith(0);
        client.getPlayer().getGameActionManager().resetActions();
	}
}