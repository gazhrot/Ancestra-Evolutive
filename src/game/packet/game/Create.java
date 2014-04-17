package game.packet.game;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("GC")
public class Create implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() != null)
			client.getPlayer().sendGameCreate();
	}
}