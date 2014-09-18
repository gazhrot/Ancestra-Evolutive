package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.creature.collector.Collector;
import org.ancestra.evolutive.enums.GuildRight;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("gF")
public class RemoveCollector implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		
		if(client.getPlayer().getGuild() == null || client.getPlayer().getFight() != null || client.getPlayer().isAway())
			return;
		if(!client.getPlayer().getGuildMember().canDo(GuildRight.POS_COLLECTOR.getId()))
			return;//On peut le retirer si on a le droit de le poser
		
		byte IDPerco = Byte.parseByte(packet);
		Collector perco = World.data.getPerco(IDPerco);
		if(perco == null || perco.get_inFight() > 0) return;
		SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(client.getPlayer().getMap(), IDPerco);
		World.database.getCollectorData().delete(perco);
		perco.DelPerco(perco.getId());
		for(Player z : client.getPlayer().getGuild().getMembers())
		{
			if(z.isOnline())
			{
				SocketManager.GAME_SEND_gITM_PACKET(z, Collector.parsetoGuild(z.getGuild().getId()));
				String str = "";
				str += "R"+perco.getFirstNameId()+","+perco.getLastNameId()+"|";
                str += perco.getMap().getId()+"|";
                str += perco.getMap().getX()+"|"
                        +perco.getMap().getY()+"|"+client.getPlayer().getName();
				SocketManager.GAME_SEND_gT_PACKET(z, str);
			}
		}
	}
}