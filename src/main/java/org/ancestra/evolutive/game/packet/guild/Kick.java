package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.guild.GuildMember;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("gK")
public class Kick implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getGuild() == null)
			return;
		
		String name = packet.substring(2);
		Player P = World.data.getPersoByName(name);
		int guid = -1, guildId = -1;
		Guild toRemGuild;
		GuildMember toRemMember;
		
		if(P == null) {
			int infos[] = World.database.getGuildMemberData().playerExistInGuild(name);
			guid = infos[0];
			guildId = infos[1];
			
			if(guildId < 0 || guid < 0)
				return;
			
			toRemGuild = World.data.getGuild(guildId);
			toRemMember = toRemGuild.getMember(guid);
		}else {
			toRemGuild = P.getGuild();
			
			if(toRemGuild == null)//La guilde du personnage n'est pas charger ?
				toRemGuild = World.data.getGuild(client.getPlayer().getGuild().getId());//On prend la guilde du perso qui l'�jecte
			
			toRemMember = toRemGuild.getMember(P.getUUID());
			
			if(toRemMember == null) 
				return;//Si le membre n'est pas dans la guilde.
			if(toRemMember.getGuild().getId() != client.getPlayer().getGuild().getId()) 
				return;//Si guilde diff�rente
		}
		//si pas la meme guilde
		if(toRemGuild.getId() != client.getPlayer().getGuild().getId()) {
			SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "Ea");
			return;
		}
		//S'il n'a pas le droit de kick, et que ce n'est pas lui m�me la cible
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_BAN) && client.getPlayer().getGuildMember().getUUID() != toRemMember.getUUID()) {
			SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "Ed");
			return;
		}
		//Si diff�rent : Kick
		if(client.getPlayer().getGuildMember().getUUID() != toRemMember.getUUID()) {
			if(toRemMember.getRank() == 1) //S'il veut kicker le meneur
				return;
			
			toRemGuild.removeMember(toRemMember.getPlayer());
			if(P != null)
				P.setGuildMember(null);
			
			SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "K"+client.getPlayer().getName()+"|"+name);
			if(P != null)
				SocketManager.GAME_SEND_gK_PACKET(P, "K"+client.getPlayer().getName());
		}else {//si quitter
			Guild G = client.getPlayer().getGuild();
			if(client.getPlayer().getGuildMember().getRank() == 1 && G.getMembers().size() > 1)	{//Si le meneur veut quitter la guilde mais qu'il reste d'autre joueurs
				SocketManager.GAME_SEND_MESSAGE(client.getPlayer(), "Vous devez mettre un autre meneur pour devoir quitter la guilde !", Server.config.getMotdColor());
				return;
			}
			
			G.removeMember(client.getPlayer());
			client.getPlayer().setGuildMember(null);
			//S'il n'y a plus personne
			if(G.getMembers().isEmpty())
				World.data.removeGuild(G.getId());
			
			SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "K"+name+"|"+name);
		}
	}
}