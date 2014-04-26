package fr.edofus.ancestra.evolutive.game.packet.mount;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Rn")
public class SetName implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getMount() == null)
			return;
		
		String name = packet.substring(2);
		client.getPlayer().getMount().setName(name);
		SocketManager.GAME_SEND_Rn_PACKET(client.getPlayer(), name);
	}
}