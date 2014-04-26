package fr.edofus.ancestra.evolutive.game.packet.waypoint;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Wv")
public class LeaveZaapi implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		client.getPlayer().Zaapi_close();
	}
}