package game.packet.fight;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("fP")
public class BlockExceptParty implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null || client.getPlayer().getGroup() == null)
			return;
		client.getPlayer().get_fight().toggleOnlyGroup(client.getPlayer().get_GUID());
	}
}