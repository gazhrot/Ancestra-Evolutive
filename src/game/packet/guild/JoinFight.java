package game.packet.guild;

import objects.Percepteur;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;

import common.World;

import core.Log;
import core.Server;
import game.GameClient;

@Packet("gT")
public class JoinFight implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		switch(packet.charAt(0))
		{
			case 'J'://Rejoindre
				int id = -1;
				try	{
					id = Integer.parseInt(Integer.toString(Integer.parseInt(packet.substring(1)), 36));
				} catch(Exception e) {}
				
				Percepteur collector = World.data.getPerco(id);
				
				if(collector == null) 
					return;
				
				int fight = -1;
				
				try	{
					fight = collector.get_inFightID();
				} catch(Exception e) {}
				
				short map = -1;
				
				try {		
					map = World.data.getCarte((short) collector.get_mapID()).getFight(fight).get_map().get_id();
				} catch(Exception e) {}
				
				int cell = -1;
				
				try {
					cell = collector.get_cellID();
				} catch(Exception e) {}
				
				if(Server.config.isDebug()) 
					Log.addToLog("[DEBUG] Percepteur INFORMATIONS : TiD:"+id+", FightID:"+fight+", MapID:"+map+", CellID"+cell);
				if(id == -1 || fight == -1 || map == -1 || cell == -1) 
					return;
				if(client.getPlayer().get_fight() == null && !client.getPlayer().is_away())	{
					if(client.getPlayer().get_curCarte().get_id() != map)
						client.getPlayer().teleport(map, cell);
					World.data.getCarte(map).getFight(fight).joinPercepteurFight(client.getPlayer(),client.getPlayer().get_GUID(), id);
				}
			break;
		}
	}
}