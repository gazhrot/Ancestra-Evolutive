package fr.edofus.ancestra.evolutive.game.packet.fight;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("fD")
public class GetDetails implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int key = -1;
		try {
			key = Integer.parseInt(packet.substring(2).replace(((int)0x0)+"", ""));
		} catch(Exception e) {}
		
		if(key == -1)
			return;
		
		SocketManager.GAME_SEND_FIGHT_DETAILS(client, client.getPlayer().get_curCarte().get_fights().get(key));
	}
}