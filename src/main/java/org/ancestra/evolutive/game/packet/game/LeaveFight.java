package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("GQ")
public class LeaveFight implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int id = -1;
		if(!packet.substring(2).isEmpty()) {
			try	{
				id = Integer.parseInt(packet.substring(2));
			} catch(Exception e) {}
		}
		
		if(client.getPlayer().getFight() == null)
			return;
		
		if(id > 0) {//Expulsion d'un joueurs autre que soi-meme
			Player target = World.data.getPersonnage(id);//On ne quitte pas un joueur qui : est null, ne combat pas, n'est pas de sa team.
			
			if(target == null || target.getFight() == null || target.getFight().getTeamID(target.getId()) != client.getPlayer().getFight().getTeamID(client.getPlayer().getId()))
				return;
			
			client.getPlayer().getFight().leftFight(client.getPlayer(), target);
		}else {
			client.getPlayer().getFight().leftFight(client.getPlayer(), null);
		}
	}
}