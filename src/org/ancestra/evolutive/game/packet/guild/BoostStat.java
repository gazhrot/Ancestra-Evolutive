package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("gB")
public class BoostStat implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getGuild() == null)
			return;
		
		Guild guild = client.getPlayer().getGuild();
		
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_BOOST))
			return;
		
		switch(packet.charAt(2))
		{
			case 'p'://Prospec
				if(guild.getCapital() < 1)
					return;
				if(guild.getStat(176) >= 500)
					return;
				
				guild.setCapital(guild.getCapital() - 1);
				guild.upgradeStat(176, 1);
			break;
			case 'x'://Sagesse
				if(guild.getCapital() < 1)
					return;
				if(guild.getStat(124) >= 400)
					return;
				
				guild.setCapital(guild.getCapital() - 1);
				guild.upgradeStat(124, 1);
			break;
			case 'o'://Pod
				if(guild.getCapital() < 1)
					return;
				if(guild.getStat(158) >= 5000)
					return;
				
				guild.setCapital(guild.getCapital() - 1);
				guild.upgradeStat(158, 20);
			break;
			case 'k'://Nb Perco
				if(guild.getCapital() < 10)
					return;
				if(guild.getNbrCollector() >= 50)
					return;
				
				guild.setCapital(guild.getCapital() - 10);
				guild.setNbrCollector(guild.getNbrCollector() + 1);
			break;
		}
		
		World.database.getGuildData().update(guild);
		SocketManager.GAME_SEND_gIB_PACKET(client.getPlayer(), client.getPlayer().getGuild().parseCollector());
	}
}