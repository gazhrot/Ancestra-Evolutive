package fr.edofus.ancestra.evolutive.game.packet.exchange;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("EK")
public class Ready implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getCurJobAction() != null) {
			if(!client.getPlayer().getCurJobAction().isCraft())
				return;
			client.getPlayer().getCurJobAction().startCraft(client.getPlayer());
		}
		if(client.getPlayer().get_curExchange() == null)
			return;
		client.getPlayer().get_curExchange().toogleOK(client.getPlayer().get_GUID());
	}
}