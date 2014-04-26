package fr.edofus.ancestra.evolutive.game.packet.environement;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("eD")
public class SetDirection implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try	{
			if(client.getPlayer().get_fight() != null)
				return;
			
			int dir = Integer.parseInt(packet.substring(2));
			client.getPlayer().set_orientation(dir);
			SocketManager.GAME_SEND_eD_PACKET_TO_MAP(client.getPlayer().get_curCarte(),client.getPlayer().get_GUID(),dir);
		} catch(NumberFormatException e) {return;}
	}
}