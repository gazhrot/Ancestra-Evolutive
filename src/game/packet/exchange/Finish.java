package game.packet.exchange;

import objects.Percepteur;
import client.Player;

import common.SocketManager;
import common.World;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("EV")
public class Finish implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_isTradingWith() == 0 &&
			   client.getPlayer().get_curExchange() == null &&
			   client.getPlayer().getCurJobAction() == null &&
			   client.getPlayer().getInMountPark() == null &&
			   !client.getPlayer().isInBank() &&
			   client.getPlayer().get_isOnPercepteurID() == 0 &&
			   client.getPlayer().getInTrunk() == null)
			return;
				
		//Si �change avec un personnage
		if(client.getPlayer().get_curExchange() != null) {
			client.getPlayer().get_curExchange().cancel();
			client.getPlayer().set_isTradingWith(0);
			client.getPlayer().set_away(false);
			return;
		}
		//Si m�tier
		if(client.getPlayer().getCurJobAction() != null)
			client.getPlayer().getCurJobAction().resetCraft();
		//Si dans un enclos
		if(client.getPlayer().getInMountPark() != null)client.getPlayer().leftMountPark();
		//prop d'echange avec un joueur
		if(client.getPlayer().get_isTradingWith() > 0) {
			Player p = World.data.getPersonnage(client.getPlayer().get_isTradingWith());
			if(p != null) {
				if(p.isOnline()) {
					SocketManager.GAME_SEND_EV_PACKET(client);
					p.set_isTradingWith(0);
				}
			}
		}
		//Si perco
		if(client.getPlayer().get_isOnPercepteurID() != 0) {
			Percepteur perco = World.data.getPerco(client.getPlayer().get_isOnPercepteurID());
			if(perco == null) 
				return;
			for(Player z : World.data.getGuild(perco.get_guildID()).getMembers()) {
				if(z.isOnline()) {
					SocketManager.GAME_SEND_gITM_PACKET(z, Percepteur.parsetoGuild(z.get_guild().get_id()));
					String str = "";
					str += "G"+perco.get_N1()+","+perco.get_N2();
					str += "|.|"+World.data.getCarte((short)perco.get_mapID()).getX()+"|"+World.data.getCarte((short)perco.get_mapID()).getY()+"|";
					str += client.getPlayer().get_name()+"|";
					str += perco.get_LogXp()+";";
					str += perco.get_LogItems();
					SocketManager.GAME_SEND_gT_PACKET(z, str);
				}
			}
			client.getPlayer().get_curCarte().RemoveNPC(perco.getGuid());
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(client.getPlayer().get_curCarte(), perco.getGuid());
			perco.DelPerco(perco.getGuid());
			World.database.getCollectorData().delete(perco);
			client.getPlayer().set_isOnPercepteurID(0);
		}
			
		client.getPlayer().save();
		SocketManager.GAME_SEND_EV_PACKET(client);
		client.getPlayer().set_isTradingWith(0);
		client.getPlayer().set_away(false);
		client.getPlayer().setInBank(false);
		client.getPlayer().setInTrunk(null);
	}
}