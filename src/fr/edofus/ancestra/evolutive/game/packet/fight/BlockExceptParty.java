package fr.edofus.ancestra.evolutive.game.packet.fight;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("fP")
public class BlockExceptParty implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null || client.getPlayer().getGroup() == null)
			return;
		client.getPlayer().get_fight().toggleOnlyGroup(client.getPlayer().get_GUID());
	}
}