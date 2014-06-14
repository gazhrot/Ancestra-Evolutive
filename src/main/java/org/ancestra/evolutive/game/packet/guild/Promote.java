package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.guild.GuildMember;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("gP")
public class Promote implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		if(client.getPlayer().getGuild() == null)return;	//Si le personnage envoyeur n'a m�me pas de guilde
		
		String[] infos = packet.split("\\|");
		
		int guid = Integer.parseInt(infos[0]);
		int rank = Integer.parseInt(infos[1]);
		byte xpGive = Byte.parseByte(infos[2]);
		int right = Integer.parseInt(infos[3]);
		
		Player p = World.data.getPersonnage(guid);	//Cherche le personnage a qui l'on change les droits dans la m�moire
		GuildMember toChange;
		GuildMember changer = client.getPlayer().getGuildMember();
		
		//R�cup�ration du personnage � changer, et verification de quelques conditions de base
		if(p == null)	//Arrive lorsque le personnage n'est pas charg� dans la m�moire
		{
			int guildId = World.database.getGuildMemberData().playerExistInGuild(guid);	//R�cup�re l'id de la guilde du personnage qui n'est pas dans la m�moire
			
			if(guildId < 0)return;	//Si le personnage � qui les droits doivent �tre modifi� n'existe pas ou n'a pas de guilde
			
			
			if(guildId != client.getPlayer().getGuild().getId())					//Si ils ne sont pas dans la m�me guilde
			{
				SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "Ed");
				return;
			}
			toChange = World.data.getGuild(guildId).getMember(guid);
		}
		else
		{
			if(p.getGuild() == null)return;	//Si la personne � qui changer les droits n'a pas de guilde
			if(client.getPlayer().getGuild().getId() != p.getGuild().getId())	//Si ils ne sont pas de la meme guilde
			{
				SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "Ea");
				return;
			}
			
			toChange = p.getGuildMember();
		}
		
		//V�rifie ce que le personnage changeur � le droit de faire
		
		if(changer.getRank() == 1)	//Si c'est le meneur
		{
			if(changer.getUUID() == toChange.getUUID())	//Si il se modifie lui m�me, reset tout sauf l'XP
			{
				rank = -1;
				right = -1;
			}
			else //Si il modifie un autre membre
			{
				if(rank == 1) //Si il met un autre membre "Meneur"
				{
					changer.setAllRights(2, (byte) -1, 29694);	//Met le meneur "Bras droit" avec tout les droits
					
					//D�fini les droits � mettre au nouveau meneur
					rank = 1;
					xpGive = -1;
					right = 1;
				}
			}
		}
		else	//Sinon, c'est un membre normal
		{
			if(toChange.getRank() == 1)	//S'il veut changer le meneur, reset tout sauf l'XP
			{
				rank = -1;
				right = -1;
			}
			else	//Sinon il veut changer un membre normal
			{
				if(!changer.canDo(Constants.G_RANK) || rank == 1)	//S'il ne peut changer les rang ou qu'il veut mettre meneur
					rank = -1; 	//"Reset" le rang
				
				if(!changer.canDo(Constants.G_RIGHT) || right == 1)	//S'il ne peut changer les droits ou qu'il veut mettre les droits de meneur
					right = -1;	//"Reset" les droits
				
				if(!changer.canDo(Constants.G_HISXP) && !changer.canDo(Constants.G_ALLXP) && changer.getUUID() == toChange.getUUID())	//S'il ne peut changer l'XP de personne et qu'il est la cible
					xpGive = -1; //"Reset" l'XP
			}
			
			if(!changer.canDo(Constants.G_ALLXP) && !changer.equals(toChange))	//S'il n'a pas le droit de changer l'XP des autres et qu'il n'est pas la cible
				xpGive = -1; //"Reset" L'XP
		}

		toChange.setAllRights(rank,xpGive,right);
		
		SocketManager.GAME_SEND_gS_PACKET(client.getPlayer(),client.getPlayer().getGuildMember());
		
		if(p != null && p.getId() != client.getPlayer().getId())
			SocketManager.GAME_SEND_gS_PACKET(p,p.getGuildMember());
	}
}