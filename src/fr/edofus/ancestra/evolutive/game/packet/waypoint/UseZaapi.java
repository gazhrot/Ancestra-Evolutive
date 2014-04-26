package fr.edofus.ancestra.evolutive.game.packet.waypoint;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Wu")
public class UseZaapi implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getDeshonor() >= 2) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "183");
			return;
		}
		
		client.getPlayer().Zaapi_use(packet);		
	}
}