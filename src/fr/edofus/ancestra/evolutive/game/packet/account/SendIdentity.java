package fr.edofus.ancestra.evolutive.game.packet.account;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Ai")
public class SendIdentity implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getAccount().setClientKey(packet.substring(2));
	}
}