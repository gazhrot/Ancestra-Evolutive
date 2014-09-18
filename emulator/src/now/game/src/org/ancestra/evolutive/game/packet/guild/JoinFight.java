package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.creature.collector.Collector;
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
                collector.getFight().joinFight(client.getPlayer(),collector.getFighter().getTeam());
			break;
		}
	}
}