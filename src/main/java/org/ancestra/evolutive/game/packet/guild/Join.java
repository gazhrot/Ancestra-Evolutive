package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.guild.GuildMember;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("gJ")
public class Join implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		switch(packet.charAt(2))
		{
			case 'R'://Nom perso			
				Player P = World.data.getPlayerByName(packet.substring(3));
				
				if(P == null || client.getPlayer().getGuild() == null) {
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(), "Eu");
					return;
				}
				if(!P.isOnline()) {
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(), "Eu");
					return;
				}
				if(P.isAway()) {
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(), "Eo");
					return;
				}
				if(P.getGuild() != null) {
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(), "Ea");
					return;
				}
				if(!client.getPlayer().getGuildMember().canDo(Constants.G_INVITE)) {
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(), "Ed");
					return;
				}
				if(client.getPlayer().getGuild().getMembers().size() >= (40+client.getPlayer().getGuild().getLevel())) {//Limite membres max
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "155;"+(40+client.getPlayer().getGuild().getLevel()));
					return;
				}
				
				client.getPlayer().setInviting(P.getId());
				P.setInviting(client.getPlayer().getId());
	
				SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(),"R"+packet.substring(1));
				SocketManager.GAME_SEND_gJ_PACKET(P,"r"+client.getPlayer().getId()+"|"+client.getPlayer().getName()+"|"+client.getPlayer().getGuild().getName());
			break;
			case 'E'://ou Refus
				if(packet.substring(3).equalsIgnoreCase(client.getPlayer().getInviting()+""))
				{
					Player p = World.data.getPlayer(client.getPlayer().getInviting());
					if(p == null)return;//Pas cens� arriver
					SocketManager.GAME_SEND_gJ_PACKET(p,"Ec");
				}
			break;
			case 'K'://Accepte
				if(packet.substring(3).equalsIgnoreCase(client.getPlayer().getInviting()+""))
				{
					Player p = World.data.getPlayer(client.getPlayer().getInviting());
					if(p == null)return;//Pas cens� arriver
					Guild G = p.getGuild();
					GuildMember GM = G.addNewMember(client.getPlayer());
					World.database.getGuildMemberData().update(GM);
					client.getPlayer().setGuildMember(GM);
					client.getPlayer().setInviting(-1);
					p.setInviting(-1);
					//Packet
					SocketManager.GAME_SEND_gJ_PACKET(p,"Ka"+client.getPlayer().getName());
					SocketManager.GAME_SEND_gS_PACKET(client.getPlayer(), GM);
					SocketManager.GAME_SEND_gJ_PACKET(client.getPlayer(),"Kj");
				}
			break;
		}
	}
}