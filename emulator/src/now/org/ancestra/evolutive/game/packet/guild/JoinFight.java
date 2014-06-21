package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.collector.Collector;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

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
				
				Collector collector = World.data.getPerco(id);
				
				if(collector == null) 
					return;
				
				int fight = -1;
				
				try	{
					fight = collector.get_inFightID();
				} catch(Exception e) {}
				
				int map = -1;
				
				try {		
					map = World.data.getMap(collector.getMap().getId()).getFights().get(fight).getMap().getId();
				} catch(Exception e) {}
				
				int cell = -1;
				
				try {
					cell = collector.getCell().getId();
				} catch(Exception e) {}
				
				if(Server.config.isDebug()) 
					Log.addToLog("[DEBUG] Percepteur INFORMATIONS : TiD:"+id+", FightID:"+fight+", MapID:"+map+", CellID"+cell);
				if(id == -1 || fight == -1 || map == -1 || cell == -1) 
					return;
				if(client.getPlayer().getFight() == null && !client.getPlayer().isAway())	{
					if(client.getPlayer().getMap().getId() != map)
						client.getPlayer().setPosition(map, cell);
					World.data.getMap(map).getFights().get(fight).joinPercepteurFight(client.getPlayer(),client.getPlayer().getId(), id);
				}
			break;
		}
	}
}