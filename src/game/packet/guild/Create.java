package game.packet.guild;

import objects.Guild;
import objects.Guild.GuildMember;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;

import common.SocketManager;
import common.World;

import game.GameClient;

@Packet("gC")
public class Create implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().get_guild() != null || client.getPlayer().getGuildMember() != null) {
			SocketManager.GAME_SEND_gC_PACKET(client.getPlayer(), "Ea");
			return;
		}
		if(client.getPlayer().get_fight() != null || client.getPlayer().is_away())
			return;
		
		try	{
			String[] infos = packet.substring(2).split("\\|");
			//base 10 => 36
			String bgID = Integer.toString(Integer.parseInt(infos[0]), 36);
			String bgCol = Integer.toString(Integer.parseInt(infos[1]), 36);
			String embID =  Integer.toString(Integer.parseInt(infos[2]), 36);
			String embCol =  Integer.toString(Integer.parseInt(infos[3]), 36);
			String name = infos[4];
			
			if(World.data.guildNameIsUsed(name)) {
				SocketManager.GAME_SEND_gC_PACKET(client.getPlayer(), "Ean");
				return;
			}
			
			//Validation du nom de la guilde
			String tempName = name.toLowerCase();
			boolean isValid = true;
			//V�rifie d'abord si il contient des termes d�finit
			if(tempName.length() > 20 || tempName.contains("mj")
			|| tempName.contains("modo") || tempName.contains("admin"))
				isValid = false;
			//Si le nom passe le test, on v�rifie que les caract�re entr� sont correct.
			if(isValid)	{
				int tiretCount = 0;
				for(char curLetter : tempName.toCharArray()) {
					if(!((curLetter >= 'a' && curLetter <= 'z') || curLetter == '-')) {
						isValid = false;
						break;
					}
					if(curLetter == '-') {
						if(tiretCount >= 2)	{
							isValid = false;
							break;
						}else {
							tiretCount++;
						}
					}
				}
			}
			
			//Si le nom est invalide
			if(!isValid) {
				SocketManager.GAME_SEND_gC_PACKET(client.getPlayer(), "Ean");
				return;
			}
			
			//FIN de la validation
			String emblem = bgID+","+bgCol+","+embID+","+embCol;//9,6o5nc,2c,0;
			
			if(World.data.guildEmblemIsUsed(emblem)) {
				SocketManager.GAME_SEND_gC_PACKET(client.getPlayer(), "Eae");
				return;
			}
			if(client.getPlayer().get_curCarte().get_id() == 2196) {//Temple de cr�ation de guilde
				if(!client.getPlayer().hasItemTemplate(1575,1)) {//Guildalogemme
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "14");
					return;
				}
				client.getPlayer().removeByTemplateID(1575, 1);
			}
			
			Guild guild = new Guild(client.getPlayer(),name,emblem);
			GuildMember member = guild.addNewMember(client.getPlayer());
			member.setAllRights(1,(byte) 0,1);//1 => Meneur (Tous droits)
			client.getPlayer().setGuildMember(member);//On ajoute le meneur
			
			World.data.addGuild(guild, true);
			World.database.getGuildMemberData().update(member);
			SocketManager.GAME_SEND_gS_PACKET(client.getPlayer(), member);
			SocketManager.GAME_SEND_gC_PACKET(client.getPlayer(),"K");
			SocketManager.GAME_SEND_gV_PACKET(client.getPlayer());
		} catch(Exception e) {return;}
	}
}