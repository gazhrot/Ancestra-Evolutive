package game.packet.account;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("Ai")
public class SendIdentity implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getAccount().setClientKey(packet.substring(2));
	}
}