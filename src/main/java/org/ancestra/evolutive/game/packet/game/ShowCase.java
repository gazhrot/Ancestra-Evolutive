package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Gf")
public class ShowCase implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().getFight() == null)
			return;
		if(client.getPlayer().getFight().get_state() != Constants.FIGHT_STATE_ACTIVE)
			return;
		
		int cellID = -1;
		
		try	{
			cellID = Integer.parseInt(packet.substring(2));
		} catch(Exception e) {}
		
		if(cellID == -1)
			return;
		client.getPlayer().getFight().showCaseToTeam(client.getPlayer().getId(),cellID);
	}
}