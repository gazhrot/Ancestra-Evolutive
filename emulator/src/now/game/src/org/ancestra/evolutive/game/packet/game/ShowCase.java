package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.fight.team.PlayerTeam;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Gf")
public class ShowCase implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int cellID;
		try	{
			cellID = Integer.parseInt(packet.substring(2));
            ((PlayerTeam)client.getPlayer().getFighter().getTeam()).showCase(client.getPlayer().getId(),cellID);
        } catch(Exception e) {
        }
    }
}