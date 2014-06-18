package org.ancestra.evolutive.game.packet.basic;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("BS")
public class UseSmiley implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
        try {
            int emoteId = Integer.parseInt(packet.substring(2));
            client.getPlayer().emoticone(emoteId);
        } catch (NumberFormatException ignored) {}

    }
}