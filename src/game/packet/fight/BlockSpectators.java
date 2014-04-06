package game.packet.fight;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("fS")
public class BlockSpectators implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null)
			return;
		client.getPlayer().get_fight().toggleLockSpec(client.getPlayer().get_GUID());
	}
}