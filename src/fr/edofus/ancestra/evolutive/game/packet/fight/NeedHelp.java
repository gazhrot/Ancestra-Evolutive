package fr.edofus.ancestra.evolutive.game.packet.fight;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("fH")
public class NeedHelp implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null)
			return;
		client.getPlayer().get_fight().toggleHelp(client.getPlayer().get_GUID());
	}
}