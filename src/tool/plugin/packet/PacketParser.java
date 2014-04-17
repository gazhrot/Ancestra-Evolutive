package tool.plugin.packet;

import game.GameClient;

public interface PacketParser {
	public void parse(GameClient client, String packet);
}