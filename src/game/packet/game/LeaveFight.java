package game.packet.game;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;

import core.World;

import game.GameClient;

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
		
		if(client.getPlayer().get_fight() == null)
			return;
		
		if(id > 0) {//Expulsion d'un joueurs autre que soi-meme
			Player target = World.data.getPersonnage(id);//On ne quitte pas un joueur qui : est null, ne combat pas, n'est pas de sa team.
			
			if(target == null || target.get_fight() == null || target.get_fight().getTeamID(target.get_GUID()) != client.getPlayer().get_fight().getTeamID(client.getPlayer().get_GUID()))
				return;
			
			client.getPlayer().get_fight().leftFight(client.getPlayer(), target);
		}else {
			client.getPlayer().get_fight().leftFight(client.getPlayer(), null);
		}
	}
}