package game.packet.game;

import common.Constants;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

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