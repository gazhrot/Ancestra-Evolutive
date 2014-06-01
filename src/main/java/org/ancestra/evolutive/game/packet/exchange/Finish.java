package org.ancestra.evolutive.game.packet.exchange;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Collector;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("EV")
public class Finish implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getIsTradingWith() == 0 &&
			   client.getPlayer().getCurExchange() == null &&
			   client.getPlayer().getCurJobAction() == null &&
			   client.getPlayer().getCurMountPark() == null &&
			   !client.getPlayer().isInBank() &&
			   client.getPlayer().getIsOnCollector() == 0 &&
			   client.getPlayer().getCurTrunk() == null)
			return;
				
		//Si �change avec un personnage
		if(client.getPlayer().getCurExchange() != null) {
			client.getPlayer().getCurExchange().cancel();
			client.getPlayer().setIsTradingWith(0);
			client.getPlayer().setAway(false);
			return;
		}
		//Si m�tier
		if(client.getPlayer().getCurJobAction() != null)
			client.getPlayer().getCurJobAction().resetCraft();
		//Si dans un enclos
		if(client.getPlayer().getCurMountPark() != null)
			client.getPlayer().setCurMountPark(null);
		//prop d'echange avec un joueur
		if(client.getPlayer().getIsTradingWith() > 0) {
			Player p = World.data.getPersonnage(client.getPlayer().getIsTradingWith());
			if(p != null) {
				if(p.isOnline()) {
					SocketManager.GAME_SEND_EV_PACKET(client);
					p.setIsTradingWith(0);
				}
			}
		}
		//Si perco
		if(client.getPlayer().getIsOnCollector() != 0) {
			Collector perco = World.data.getPerco(client.getPlayer().getIsOnCollector());
			if(perco == null) 
				return;
			for(Player z : World.data.getGuild(perco.get_guildID()).getMembers()) {
				if(z.isOnline()) {
					SocketManager.GAME_SEND_gITM_PACKET(z, Collector.parsetoGuild(z.getGuild().getId()));
					String str = "";
					str += "G"+perco.get_N1()+","+perco.get_N2();
					str += "|.|"+World.data.getCarte(perco.get_mapID()).getX()+"|"+World.data.getCarte(perco.get_mapID()).getY()+"|";
					str += client.getPlayer().getName()+"|";
					str += perco.get_LogXp()+";";
					str += perco.get_LogItems();
					SocketManager.GAME_SEND_gT_PACKET(z, str);
				}
			}
			client.getPlayer().getCurMap().getNpcs().remove(perco.getGuid());
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(client.getPlayer().getCurMap(), perco.getGuid());
			perco.DelPerco(perco.getGuid());
			World.database.getCollectorData().delete(perco);
			client.getPlayer().setIsOnCollector(0);
		}
			
		client.getPlayer().save();
		SocketManager.GAME_SEND_EV_PACKET(client);
		client.getPlayer().setIsTradingWith(0);
		client.getPlayer().setAway(false);
		client.getPlayer().setInBank(false);
		client.getPlayer().setCurTrunk(null);
	}
}