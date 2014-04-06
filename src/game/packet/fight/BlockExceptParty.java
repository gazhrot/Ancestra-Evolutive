package game.packet.fight;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("fP")
public class BlockExceptParty implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null || client.getPlayer().getGroup() == null)
			return;
		client.getPlayer().get_fight().toggleOnlyGroup(client.getPlayer().get_GUID());
	}
}