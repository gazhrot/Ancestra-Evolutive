package fr.edofus.ancestra.evolutive.game.packet.game;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Gp")
public class SetPlayerPosition implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null)
			return;
		
		try {
			int cell = Integer.parseInt(packet.substring(2));
			client.getPlayer().get_fight().changePlace(client.getPlayer(), cell);
		} catch(NumberFormatException e) {return;}
	}
}