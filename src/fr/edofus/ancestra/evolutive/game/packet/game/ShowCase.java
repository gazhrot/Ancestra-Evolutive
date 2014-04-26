package fr.edofus.ancestra.evolutive.game.packet.game;


import fr.edofus.ancestra.evolutive.common.Constants;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Gf")
public class ShowCase implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().get_fight() == null)
			return;
		if(client.getPlayer().get_fight().get_state() != Constants.FIGHT_STATE_ACTIVE)
			return;
		
		int cellID = -1;
		
		try	{
			cellID = Integer.parseInt(packet.substring(2));
		} catch(Exception e) {}
		
		if(cellID == -1)
			return;
		client.getPlayer().get_fight().showCaseToTeam(client.getPlayer().get_GUID(),cellID);
	}
}