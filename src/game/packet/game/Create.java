package game.packet.game;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("GC")
public class Create implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() != null)
			client.getPlayer().sendGameCreate();
	}
}