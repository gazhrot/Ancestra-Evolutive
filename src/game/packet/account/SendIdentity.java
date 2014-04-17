package game.packet.account;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("Ai")
public class SendIdentity implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getAccount().setClientKey(packet.substring(2));
	}
}