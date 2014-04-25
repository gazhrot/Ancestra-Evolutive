package game.packet.guild;

import objects.Guild;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;

import common.Constants;
import common.SocketManager;
import core.World;

import game.GameClient;

@Packet("gB")
public class BoostStat implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_guild() == null)
			return;
		
		Guild guild = client.getPlayer().get_guild();
		
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_BOOST))
			return;
		
		switch(packet.charAt(2))
		{
			case 'p'://Prospec
				if(guild.get_Capital() < 1)
					return;
				if(guild.get_Stats(176) >= 500)
					return;
				
				guild.set_Capital(guild.get_Capital() - 1);
				guild.upgrade_Stats(176, 1);
			break;
			case 'x'://Sagesse
				if(guild.get_Capital() < 1)
					return;
				if(guild.get_Stats(124) >= 400)
					return;
				
				guild.set_Capital(guild.get_Capital() - 1);
				guild.upgrade_Stats(124, 1);
			break;
			case 'o'://Pod
				if(guild.get_Capital() < 1)
					return;
				if(guild.get_Stats(158) >= 5000)
					return;
				
				guild.set_Capital(guild.get_Capital() - 1);
				guild.upgrade_Stats(158, 20);
			break;
			case 'k'://Nb Perco
				if(guild.get_Capital() < 10)
					return;
				if(guild.get_nbrPerco() >= 50)
					return;
				
				guild.set_Capital(guild.get_Capital() - 10);
				guild.set_nbrPerco(guild.get_nbrPerco() + 1);
			break;
		}
		
		World.database.getGuildData().update(guild);
		SocketManager.GAME_SEND_gIB_PACKET(client.getPlayer(), client.getPlayer().get_guild().parsePercotoGuild());
	}
}