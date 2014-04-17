package game.packet.guild;

import objects.Guild;
import objects.Guild.GuildMember;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;

import common.Constants;
import common.SocketManager;
import common.World;

import game.GameClient;

@Packet("gJ")
public class Join implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		switch(packet.charAt(2))
		{
			case 'R'://Nom perso
				Player P = World.data.getPersoByName(packet.substring(1));
				if(P == null || client.getPlayer().get_guild() == null)
				{
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(), "Eu");
					return;
				}
				if(!P.isOnline())
				{
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(), "Eu");
					return;
				}
				if(P.is_away())
				{
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(), "Eo");
					return;
				}
				if(P.get_guild() != null)
				{
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(), "Ea");
					return;
				}
				if(!client.getPlayer().getGuildMember().canDo(Constants.G_INVITE))
				{
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(), "Ed");
					return;
				}
				if(client.getPlayer().get_guild().getMembers().size() >= (40+client.getPlayer().get_guild().get_lvl()))//Limite membres max
				{
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "155;"+(40+client.getPlayer().get_guild().get_lvl()));
					return;
				}
				
				client.getPlayer().setInvitation(P.get_GUID());
				P.setInvitation(client.getPlayer().get_GUID());
	
				SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(),"R"+packet.substring(1));
				SocketManager.GAME_SEND_gJ_PACKET(P,"r"+client.getPlayer().get_GUID()+"|"+client.getPlayer().get_name()+"|"+client.getPlayer().get_guild().get_name());
			break;
			case 'E'://ou Refus
				if(packet.substring(1).equalsIgnoreCase(client.getPlayer().getInvitation()+""))
				{
					Player p = World.data.getPersonnage(client.getPlayer().getInvitation());
					if(p == null)return;//Pas cens� arriver
					SocketManager.GAME_SEND_gJ_PACKET(p,"Ec");
				}
			break;
			case 'K'://Accepte
				if(packet.substring(1).equalsIgnoreCase(client.getPlayer().getInvitation()+""))
				{
					Player p = World.data.getPersonnage(client.getPlayer().getInvitation());
					if(p == null)return;//Pas cens� arriver
					Guild G = p.get_guild();
					GuildMember GM = G.addNewMember(client.getPlayer());
					World.database.getGuildMemberData().update(GM);
					client.getPlayer().setGuildMember(GM);
					client.getPlayer().setInvitation(-1);
					p.setInvitation(-1);
					//Packet
					SocketManager.GAME_SEND_gJ_PACKET(p,"Ka"+client.getPlayer().get_name());
					SocketManager.GAME_SEND_gS_PACKET(client.getPlayer(), GM);
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(),"Kj");
				}
			break;
		}
	}
}