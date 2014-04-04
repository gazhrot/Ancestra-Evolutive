package game.packet;

import objects.Guild;
import objects.House;
import objects.Percepteur;
import objects.Carte.MountPark;
import objects.Guild.GuildMember;
import client.Player;

import common.Constants;
import common.Formulas;
import common.SocketManager;
import common.World;

import core.Log;
import core.Server;
import game.GameClient;
import game.packet.handler.Packet;

public class GuildPacket {

	@Packet("gB")
	public static void boostStat(GameClient client, String packet) {
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

	@Packet("gb")
	public static void boostSpell(GameClient client, String packet) {
		if(client.getPlayer().get_guild() == null)
			return;
		
		Guild guild = client.getPlayer().get_guild();
		
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_BOOST))
			return;
		
		int id = Integer.parseInt(packet.substring(2));
		
		if(guild.getSpells().containsKey(id)) {
			if(guild.get_Capital() < 5)
				return;
			
			guild.set_Capital(guild.get_Capital() - 5);
			guild.boostSpell(id);
			World.database.getGuildData().update(guild);
			SocketManager.GAME_SEND_gIB_PACKET(client.getPlayer(), client.getPlayer().get_guild().parsePercotoGuild());
		}else {
			Log.addToLog("[ERROR]Sort "+id+" non trouve.");
		}
	}
	
	@Packet("gC")
	public static void create(GameClient client, String packet)
	{
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
	
	@Packet("gf")
	public static void goToMountpark(GameClient client, String packet)
	{
		packet = packet.substring(2);
		if(client.getPlayer().get_guild() == null)
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		
		if(client.getPlayer().get_fight() != null || client.getPlayer().is_away())return;
		short MapID = Short.parseShort(packet);
		MountPark MP = World.data.getCarte(MapID).getMountPark();
		if(MP.get_guild().get_id() != client.getPlayer().get_guild().get_id())
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		int CellID = World.data.getEncloCellIdByMapId(MapID);
		if (client.getPlayer().hasItemTemplate(9035, 1))
		{
			client.getPlayer().removeByTemplateID(9035,1);
			client.getPlayer().teleport(MapID, CellID);
		}else
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1159");
			return;
		}
	}
	
	@Packet("gF")
	public static void removeCollector(GameClient client, String packet) 
	{
		packet = packet.substring(2);
		if(client.getPlayer().get_guild() == null || client.getPlayer().get_fight() != null || client.getPlayer().is_away())
			return;
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_POSPERCO))return;//On peut le retirer si on a le droit de le poser
		byte IDPerco = Byte.parseByte(packet);
		Percepteur perco = World.data.getPerco(IDPerco);
		if(perco == null || perco.get_inFight() > 0) return;
		SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(client.getPlayer().get_curCarte(), IDPerco);
		World.database.getCollectorData().delete(perco);
		perco.DelPerco(perco.getGuid());
		for(Player z : client.getPlayer().get_guild().getMembers())
		{
			if(z.isOnline())
			{
				SocketManager.GAME_SEND_gITM_PACKET(z, Percepteur.parsetoGuild(z.get_guild().get_id()));
				String str = "";
				str += "R"+perco.get_N1()+","+perco.get_N2()+"|";
				str += perco.get_mapID()+"|";
				str += World.data.getCarte((short)perco.get_mapID()).getX()+"|"+World.data.getCarte((short)perco.get_mapID()).getY()+"|"+client.getPlayer().get_name();
				SocketManager.GAME_SEND_gT_PACKET(z, str);
			}
		}
	}

	@Packet("gh")
	public static void goToHouse(GameClient client, String packet)
	{
		packet = packet.substring(2);
		if(client.getPlayer().get_guild() == null)
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		
		if(client.getPlayer().get_fight() != null || client.getPlayer().is_away())return;
		int HouseID = Integer.parseInt(packet);
		House h = World.data.getHouses().get(HouseID);
		if(h == null) return;
		if(client.getPlayer().get_guild().get_id() != h.get_guild_id()) 
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		if(!h.canDo(Constants.H_GTELE))
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1136");
			return;
		}
		if (client.getPlayer().hasItemTemplate(8883, 1))
		{
			client.getPlayer().removeByTemplateID(8883,1);
			client.getPlayer().teleport((short)h.get_mapid(), h.get_caseid());
		}else
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1137");
			return;
		}
	}
	
	@Packet("gH")
	public static void addCollector(GameClient client, String packet) 
	{
		if(client.getPlayer().get_guild() == null || client.getPlayer().get_fight() != null || client.getPlayer().is_away())return;
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_POSPERCO))return;//Pas le droit de le poser
		if(client.getPlayer().get_guild().getMembers().size() < 10)return;//Guilde invalide
		short price = (short)(1000+10*client.getPlayer().get_guild().get_lvl());//Calcul du prix du percepteur
		if(client.getPlayer().get_kamas() < price)//Kamas insuffisants
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "182");
			return;
		}
		if(Percepteur.GetPercoGuildID(client.getPlayer().get_curCarte().get_id()) > 0)//La carte poss�de un perco
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1168;1");
			return;
		}
		if(client.getPlayer().get_curCarte().get_placesStr().length() < 5)//La map ne poss�de pas de "places"
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "113");
			return;
		}
		if(Percepteur.CountPercoGuild(client.getPlayer().get_guild().get_id()) >= client.getPlayer().get_guild().get_nbrPerco()) return;//Limite de percepteur
		short random1 = (short) (Formulas.getRandomValue(1, 39));
		short random2 = (short) (Formulas.getRandomValue(1, 71));
		//Ajout du Perco.
		int id = World.database.getCollectorData().nextId();
		Percepteur perco = new Percepteur(id, client.getPlayer().get_curCarte().get_id(), client.getPlayer().get_curCell().getID(), (byte)3, client.getPlayer().get_guild().get_id(), random1, random2, "", 0, 0);
		World.data.addPerco(perco);
		SocketManager.GAME_SEND_ADD_PERCO_TO_MAP(client.getPlayer().get_curCarte());
		World.database.getCollectorData().create(perco);
		for(Player z : client.getPlayer().get_guild().getMembers())
		{
			if(z != null && z.isOnline())
			{
				SocketManager.GAME_SEND_gITM_PACKET(z, Percepteur.parsetoGuild(z.get_guild().get_id()));
				String str = "";
				str += "S"+perco.get_N1()+","+perco.get_N2()+"|";
				str += perco.get_mapID()+"|";
				str += World.data.getCarte((short)perco.get_mapID()).getX()+"|"+World.data.getCarte((short)perco.get_mapID()).getY()+"|"+client.getPlayer().get_name();
				SocketManager.GAME_SEND_gT_PACKET(z, str);
			}
		}
	}

	@Packet("gI")
	public static void infos(GameClient client, String packet)
	{
		switch(packet.charAt(2)) {
			case 'B'://Perco
				SocketManager.GAME_SEND_gIB_PACKET(client.getPlayer(), client.getPlayer().get_guild().parsePercotoGuild());
			break;
			case 'F'://Enclos
				SocketManager.GAME_SEND_gIF_PACKET(client.getPlayer(), World.data.parseMPtoGuild(client.getPlayer().get_guild().get_id()));
			break;
			case 'G'://General
				SocketManager.GAME_SEND_gIG_PACKET(client.getPlayer(), client.getPlayer().get_guild());
			break;
			case 'H'://House
				SocketManager.GAME_SEND_gIH_PACKET(client.getPlayer(), House.parseHouseToGuild(client.getPlayer()));
			break;
			case 'M'://Members
				SocketManager.GAME_SEND_gIM_PACKET(client.getPlayer(), client.getPlayer().get_guild(),'+');
			break;
			case 'T'://Perco
				SocketManager.GAME_SEND_gITM_PACKET(client.getPlayer(), Percepteur.parsetoGuild(client.getPlayer().get_guild().get_id()));
				Percepteur.parseAttaque(client.getPlayer(), client.getPlayer().get_guild().get_id());
				Percepteur.parseDefense(client.getPlayer(), client.getPlayer().get_guild().get_id());
			break;
		}
	}
	
	@Packet("gJ")
	public static void join(GameClient client, String packet)
	{
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
	
	@Packet("gK")
	public static void kick(GameClient client, String packet)
	{
		String name = packet.substring(2);
		if(client.getPlayer().get_guild() == null)return;
		Player P = World.data.getPersoByName(name);
		int guid = -1,guildId = -1;
		Guild toRemGuild;
		GuildMember toRemMember;
		if(P == null)
		{
			int infos[] = World.database.getGuildMemberData().playerExistInGuild(name);
			guid = infos[0];
			guildId = infos[1];
			if(guildId < 0 || guid < 0)return;
			toRemGuild = World.data.getGuild(guildId);
			toRemMember = toRemGuild.getMember(guid);
		}
		else
		{
			toRemGuild = P.get_guild();
			if(toRemGuild == null)//La guilde du personnage n'est pas charger ?
			{
					toRemGuild = World.data.getGuild(client.getPlayer().get_guild().get_id());//On prend la guilde du perso qui l'�jecte
			}
			toRemMember = toRemGuild.getMember(P.get_GUID());
			if(toRemMember == null) return;//Si le membre n'est pas dans la guilde.
			if(toRemMember.getGuild().get_id() != client.getPlayer().get_guild().get_id()) return;//Si guilde diff�rente
		}
		//si pas la meme guilde
		if(toRemGuild.get_id() != client.getPlayer().get_guild().get_id())
		{
			SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "Ea");
			return;
		}
		//S'il n'a pas le droit de kick, et que ce n'est pas lui m�me la cible
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_BAN) && client.getPlayer().getGuildMember().getGuid() != toRemMember.getGuid())
		{
			SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "Ed");
			return;
		}
		//Si diff�rent : Kick
		if(client.getPlayer().getGuildMember().getGuid() != toRemMember.getGuid())
		{
			if(toRemMember.getRank() == 1) //S'il veut kicker le meneur
				return;
			
			toRemGuild.removeMember(toRemMember.getPerso());
			if(P != null)
				P.setGuildMember(null);
			
			SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "K"+client.getPlayer().get_name()+"|"+name);
			if(P != null)
				SocketManager.GAME_SEND_gK_PACKET(P, "K"+client.getPlayer().get_name());
		}else//si quitter
		{
			Guild G = client.getPlayer().get_guild();
			if(client.getPlayer().getGuildMember().getRank() == 1 && G.getMembers().size() > 1)	//Si le meneur veut quitter la guilde mais qu'il reste d'autre joueurs
			{
				//TODO : Envoyer le message qu'il doit mettre un autre membre meneur (Pas vraiment....)
				return;
			}
			G.removeMember(client.getPlayer());
			client.getPlayer().setGuildMember(null);
			//S'il n'y a plus personne
			if(G.getMembers().isEmpty())World.data.removeGuild(G.get_id());
			SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "K"+name+"|"+name);
		}
	}
	
	@Packet("gP")
	public static void promote(GameClient client, String packet)
	{
		packet = packet.substring(2);
		if(client.getPlayer().get_guild() == null)return;	//Si le personnage envoyeur n'a m�me pas de guilde
		
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
			
			
			if(guildId != client.getPlayer().get_guild().get_id())					//Si ils ne sont pas dans la m�me guilde
			{
				SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "Ed");
				return;
			}
			toChange = World.data.getGuild(guildId).getMember(guid);
		}
		else
		{
			if(p.get_guild() == null)return;	//Si la personne � qui changer les droits n'a pas de guilde
			if(client.getPlayer().get_guild().get_id() != p.get_guild().get_id())	//Si ils ne sont pas de la meme guilde
			{
				SocketManager.GAME_SEND_gK_PACKET(client.getPlayer(), "Ea");
				return;
			}
			
			toChange = p.getGuildMember();
		}
		
		//V�rifie ce que le personnage changeur � le droit de faire
		
		if(changer.getRank() == 1)	//Si c'est le meneur
		{
			if(changer.getGuid() == toChange.getGuid())	//Si il se modifie lui m�me, reset tout sauf l'XP
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
				
				if(!changer.canDo(Constants.G_HISXP) && !changer.canDo(Constants.G_ALLXP) && changer.getGuid() == toChange.getGuid())	//S'il ne peut changer l'XP de personne et qu'il est la cible
					xpGive = -1; //"Reset" l'XP
			}
			
			if(!changer.canDo(Constants.G_ALLXP) && !changer.equals(toChange))	//S'il n'a pas le droit de changer l'XP des autres et qu'il n'est pas la cible
				xpGive = -1; //"Reset" L'XP
		}

		toChange.setAllRights(rank,xpGive,right);
		
		SocketManager.GAME_SEND_gS_PACKET(client.getPlayer(),client.getPlayer().getGuildMember());
		
		if(p != null && p.get_GUID() != client.getPlayer().get_GUID())
			SocketManager.GAME_SEND_gS_PACKET(p,p.getGuildMember());
	}
	
	@Packet("gT")
	public static void joinFight(GameClient client, String packet) {
		packet = packet.substring(2);
		switch(packet.charAt(0))
		{
			case 'J'://Rejoindre
				int id = -1;
				try	{
					id = Integer.parseInt(Integer.toString(Integer.parseInt(packet.substring(1)), 36));
				} catch(Exception e) {}
				
				Percepteur collector = World.data.getPerco(id);
				
				if(collector == null) 
					return;
				
				int fight = -1;
				
				try	{
					fight = collector.get_inFightID();
				} catch(Exception e) {}
				
				short map = -1;
				
				try {		
					map = World.data.getCarte((short) collector.get_mapID()).getFight(fight).get_map().get_id();
				} catch(Exception e) {}
				
				int cell = -1;
				
				try {
					cell = collector.get_cellID();
				} catch(Exception e) {}
				
				if(Server.config.isDebug()) 
					Log.addToLog("[DEBUG] Percepteur INFORMATIONS : TiD:"+id+", FightID:"+fight+", MapID:"+map+", CellID"+cell);
				if(id == -1 || fight == -1 || map == -1 || cell == -1) 
					return;
				if(client.getPlayer().get_fight() == null && !client.getPlayer().is_away())	{
					if(client.getPlayer().get_curCarte().get_id() != map)
						client.getPlayer().teleport(map, cell);
					World.data.getCarte(map).getFight(fight).joinPercepteurFight(client.getPlayer(),client.getPlayer().get_GUID(), id);
				}
			break;
		}
	}
	
	@Packet("gV")
	public static void close(GameClient client, String packet) {
		SocketManager.GAME_SEND_gV_PACKET(client.getPlayer());
	}
}