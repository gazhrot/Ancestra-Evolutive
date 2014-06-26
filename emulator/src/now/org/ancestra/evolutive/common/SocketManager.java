package org.ancestra.evolutive.common;

import org.ancestra.evolutive.client.Client;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Group;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Mount;
import org.ancestra.evolutive.entity.collector.Collector;
import org.ancestra.evolutive.entity.monster.MobGroup;
import org.ancestra.evolutive.entity.npc.Npc;
import org.ancestra.evolutive.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.game.GameServer;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.guild.GuildMember;
import org.ancestra.evolutive.hdv.Hdv;
import org.ancestra.evolutive.hdv.HdvEntry;
import org.ancestra.evolutive.house.Trunk;
import org.ancestra.evolutive.job.JobStat;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.InteractiveObject;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.map.MountPark;
import org.ancestra.evolutive.object.ObjectPosition;
import org.ancestra.evolutive.object.ObjectSet;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.object.ObjectTemplate;

import java.util.ArrayList;
import java.util.Map.Entry;

public class SocketManager {
	
	public static void send(Player p, String packet) {
        if(!packet.isEmpty())
            p.getAccount().getGameClient().getSession().write(packet);
	}
	
	public static void send(Client out, String packet) {
        if(!packet.isEmpty())
		    out.getSession().write(packet);
	}


	public static void MULTI_SEND_Af_PACKET(Client out,int position, int totalAbo, int totalNonAbo, String subscribe,
			int queueID)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("Af").append(position).append("|").append(totalAbo).append("|").append(totalNonAbo).append("|").append(subscribe).append("|").append(queueID);
		send(out,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Serv: Send>>"+packet.toString());
	}


	public static void REALM_SEND_POLICY_FILE(Client _out)
	{
		String packet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +   
    		"<cross-domain-policy>"+  
    	    "<allow-access-from domain=\"*\" to-ports=\"*\" secure=\"false\" />"+  
    	    "<site-control permitted-cross-domain-policies=\"master-only\" />"+  
    	    "</cross-domain-policy>";
		send(_out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	

	public static void GAME_SEND_ATTRIBUTE_FAILED(Client out)
	{
		String packet = "ATE";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_ATTRIBUTE_SUCCESS(Client out)
	{
		String packet = "ATK0";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_AV0(Client out)
	{
		String packet = "AV0";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_HIDE_GENERATE_NAME(Client out)
	{
		String packet = "APE2";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_NAME_ALREADY_EXIST(Client out)
	{
		String packet = "AAEa";
		send(out,packet);
		if(Server.config.isDebug())
		
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_CREATE_PERSO_FULL(Client out)
	{
		String packet = "AAEf";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_CREATE_OK(Client out)
	{
		String packet = "AAK";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}



	public static void GAME_SEND_CREATE_FAILED(Client out)
	{
		String packet = "AAEF";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);

	}

	public static void GAME_SEND_PERSO_SELECTION_FAILED(Client out)
	{
		String packet = "ASE";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_STATS_PACKET(Player perso)
	{
		String packet = perso.getAsPacket();
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_Rx_PACKET(Player out)
	{
		String packet = "Rx"+out.getMountXp();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_Rn_PACKET(Player out,String name)
	{
		String packet = "Rn"+name;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_Re_PACKET(Player out,String sign,Mount DD)
	{
		String packet = "Re"+sign;
		if(sign.equals("+"))packet += DD.parse();
		
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_ASK(Client out,Player perso)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("ASK|").append(perso.getId()).append("|").append(perso.getName()).append("|");
		packet.append(perso.getLevel()).append("|").append(perso.getClasse().getId()).append("|").append(perso.getSex());
		packet.append("|").append(perso.getGfx()).append("|").append((perso.getColor1()==-1?"-1":Integer.toHexString(perso.getColor1())));
		packet.append("|").append((perso.getColor2()==-1?"-1":Integer.toHexString(perso.getColor2()))).append("|");
		packet.append((perso.getColor3()==-1?"-1":Integer.toHexString(perso.getColor3()))).append("|");
		packet.append(perso.parseItemToASK());
		
		send(out,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_ALIGNEMENT(Client out,int alliID)
	{
		String packet = "ZS"+alliID;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_ADD_CANAL(Client out, String chans)
	{
		String packet = "cC+"+chans;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_ZONE_ALLIGN_STATUT(Client out)
	{
		String packet = "al|"+World.data.getSousZoneStateString();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_SEESPELL_OPTION(Client out, boolean spells)
	{
		String packet = "SLo"+(spells?"+":"-");
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_RESTRICTIONS(Client out)
	{
		String packet =  "AR6bk";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_Ow_PACKET(Player perso)
	{
		String packet =  "Ow"+perso.getPodUsed()+"|"+perso.getMaxPod();
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_OT_PACKET(Client out, int id)
	{
		String packet =  "OT";
		if(id > 0) packet += id;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_SEE_FRIEND_CONNEXION(Client out,boolean see)
	{
		String packet = "FO"+(see?"+":"-");
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_GAME_CREATE(Client out, String _name)
	{
		String packet = "GCK|1|"+_name;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_SERVER_HOUR(Client out)
	{
		String packet = GameServer.getServerTime();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_SERVER_DATE(Client out)
	{
		String packet = GameServer.getServerDate();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_MAPDATA(Client out, int id, String date,String key)
	{
		String packet = "GDM|"+id+"|"+date+"|"+key;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_GDK_PACKET(Client out)
	{
		String packet = "GDK";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_MAP_MOBS_GMS_PACKETS(Client out, Maps carte)
	{
		String packet = carte.getMobGroupGMsPackets();
		if(packet.equals(""))return;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_MAP_OBJECTS_GDS_PACKETS(Client out, Maps carte)
	{
		String packet = carte.getObjectsGDsPackets();
		if(packet.equals(""))return;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_MAP_NPCS_GMS_PACKETS(Client out, Maps carte)
	{
		String packet = carte.getNpcsGMsPackets();
		if(packet.equals("") && packet.length() < 4)return;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_MAP_PERCO_GMS_PACKETS(Client out, Maps carte){
        Collector collector = World.data.getCollector(carte);
        if(collector == null) return;
		String packet = "GM|+"+collector.getHelper().getGmPacket();
		if(packet.length() < 5)return;
		send(out,packet);

	}

	public static void GAME_SEND_ERASE_ON_MAP_TO_MAP(Maps map,int guid)
	{
		String packet = "GM|-"+guid;
		for(Player z : map.getPlayers())
		{
			if(z.getAccount().getGameClient() == null)continue;
			send(z.getAccount().getGameClient(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map "+map.getId()+": Send>>"+packet);
	}
	
	public static void GAME_SEND_ERASE_ON_MAP_TO_FIGHT(Fight f, int guid)
	{
		String packet = "GM|-"+guid;
		for(int z=0;z < f.getFighters(1).size();z++)
		{
			if(f.getFighters(1).get(z).getPersonnage().getAccount().getGameClient() == null)continue;
			send(f.getFighters(1).get(z).getPersonnage().getAccount().getGameClient(),packet);
		}
		for(int z=0;z < f.getFighters(2).size();z++)
		{
			if(f.getFighters(2).get(z).getPersonnage().getAccount().getGameClient() == null)continue;
			send(f.getFighters(2).get(z).getPersonnage().getAccount().getGameClient(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fighter ID "+f.getId()+": Send>>"+packet);
	}
	
	public static void GAME_SEND_ON_FIGHTER_KICK(Fight f, int guid, int team)
	{
		String packet = "GM|-"+guid;
		for(Fighter F : f.getFighters(team))
		{
			if(F.getPersonnage() == null || F.getPersonnage().getAccount().getGameClient() == null || F.getPersonnage().getId() == guid)continue;
			send(F.getPersonnage().getAccount().getGameClient(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fighter ID "+f.getId()+": Send>>"+packet);
	}
	
	public static void GAME_SEND_ALTER_FIGHTER_MOUNT(Fight fight, Fighter fighter, int guid, int team, int otherteam)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("GM|-").append(guid).append((char)0x00).append(fighter.getGmPacket('~'));
		for(Fighter F : fight.getFighters(team))
		{
			if(F.getPersonnage() == null || F.getPersonnage().getAccount().getGameClient() == null || !F.getPersonnage().isOnline())continue;
			send(F.getPersonnage().getAccount().getGameClient(),packet.toString());
		}
		if(otherteam > -1)
		{
			for(Fighter F : fight.getFighters(otherteam))
			{
				if(F.getPersonnage() == null || F.getPersonnage().getAccount().getGameClient() == null || !F.getPersonnage().isOnline())continue;
				send(F.getPersonnage().getAccount().getGameClient(),packet.toString());
			}
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight ID "+fight.getId()+": Send>>"+packet);
	}

	public static void GAME_SEND_ADD_PLAYER_TO_MAP(Maps map, Player perso)
	{
		String packet = "GM|+"+perso.getHelper().getGmPacket();
		for(Player z : map.getPlayers()) send(z,packet);	
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map "+map.getId()+": Send>>"+packet);
	}

	public static void GAME_SEND_DUEL_Y_AWAY(Client out, int guid)
	{
		String packet = "GA;903;"+guid+";o";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_DUEL_E_AWAY(Client out, int guid)
	{
		String packet = "GA;903;"+guid+";z";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_MAP_NEW_DUEL_TO_MAP(Maps map,int guid, int guid2)
	{
		String packet = "GA;900;"+guid+";"+guid2;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map "+map.getId()+": Send>>"+packet);
	}
	
	public static void GAME_SEND_CANCEL_DUEL_TO_MAP(Maps map, int guid,int guid2)
	{
		String packet = "GA;902;"+guid+";"+guid2;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	
	public static void GAME_SEND_MAP_START_DUEL_TO_MAP(Maps map,int guid, int guid2)
	{
		String packet = "GA;901;"+guid+";"+guid2;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}

	public static void GAME_SEND_MAP_FIGHT_COUNT(Client out,Maps map)
	{
		String packet = "fC"+map.getFights().size();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_FIGHT_GJK_PACKET_TO_FIGHT(Fight fight, int teams,int state, int cancelBtn, int duel, int spec, int time, int type)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("GJK").append(state).append("|").append(cancelBtn).append("|").append(duel).append("|").append(spec).append("|").append(time).append("|").append(type);
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft() || f.getPersonnage() == null)continue;
			send(f.getPersonnage(),packet.toString());
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet.toString());
	}
	
	public static void GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(Fight fight,int teams, String places, int team)
	{
		String packet = "GP"+places+"|"+team;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}

	public static void GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(Maps map)
	{
		String packet = "fC"+map.getFights().size();
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}

	public static void GAME_SEND_GAME_ADDFLAG_PACKET_TO_MAP(Maps map,int arg1, int guid1,int guid2,int cell1,String str1,int cell2,String str2)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("Gc+").append(guid1).append(";").append(arg1).append("|").append(guid1).append(";").append(cell1).append(";").append(str1).append("|").append(guid2).append(";").append(cell2).append(";").append(str2);
		for(Player z : map.getPlayers()) send(z,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet.toString());
	}
	
	public static void GAME_SEND_GAME_ADDFLAG_PACKET_TO_PLAYER(Player p, Maps map,int arg1, int guid1,int guid2,int cell1,String str1,int cell2,String str2)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("Gc+").append(guid1).append(";").append(arg1).append("|").append(guid1).append(";").append(cell1).append(";").append(str1).append("|").append(guid2).append(";").append(cell2).append(";").append(str2);
		send(p,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet.toString());
	}
	
	public static void GAME_SEND_GAME_REMFLAG_PACKET_TO_MAP(Maps map, int guid)
	{
		String packet = "Gc-"+guid;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	
	public static void GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(Maps map,int teamID,Fighter perso)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("Gt").append(teamID).append("|+").append(perso.getGUID()).append(";").append(perso.getPacketsName()).append(";").append(perso.get_lvl());
		for(Player z : map.getPlayers()) send(z,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet.toString());
	}
	
	public static void GAME_SEND_ADD_IN_TEAM_PACKET_TO_PLAYER(Player p, Maps map,int teamID,Fighter perso)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("Gt").append(teamID).append("|+").append(perso.getGUID()).append(";").append(perso.getPacketsName()).append(";").append(perso.get_lvl());
		send(p,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet.toString());
	}
	
	public static void GAME_SEND_REMOVE_IN_TEAM_PACKET_TO_MAP(Maps map,int teamID,Fighter perso)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("Gt").append(teamID).append("|-").append(perso.getGUID()).append(";").append(perso.getPacketsName()).append(";").append(perso.get_lvl());
		for(Player z : map.getPlayers()) send(z,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet.toString());
	}
	
	public static void GAME_SEND_MAP_MOBS_GMS_PACKETS_TO_MAP(Maps map)
	{
		String packet = map.getMobGroupGMsPackets(); // Un par un comme sa lors du respawn :)
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	
	public static void GAME_SEND_MAP_MOBS_GM_PACKET(Maps map, MobGroup current_Mobs)
	{
		String packet = "GM|";
		packet += current_Mobs.getHelper().getGmPacket(); // Un par un comme sa lors du respawn :)
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	
	public static void GAME_SEND_MAP_GMS_PACKETS(Maps map, Player player) {
		String packet = (player.getFight() == null ? map.getGMsPackets() : map.getFightersGMsPackets());
		send(player, packet);
		
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_ON_EQUIP_ITEM(Maps map, Player _perso)
	{
		String packet = _perso.parseToOa();
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	
	public static void GAME_SEND_ON_EQUIP_ITEM_FIGHT(Player _perso, Fighter f, Fight F)
	{
		String packet = _perso.parseToOa();
		for(Fighter z : F.getFighters(f.getTeam2())) 
		{
			if(z.getPersonnage() == null) continue;
			send(z.getPersonnage(),packet);
		}
		for(Fighter z : F.getFighters(f.getOtherTeam())) 
		{
			if(z.getPersonnage() == null) continue;
			send(z.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}

	public static void GAME_SEND_FIGHT_CHANGE_PLACE_PACKET_TO_FIGHT(Fight fight, int teams, Maps map, int guid, int cell)
	{
		String packet = "GIC|"+guid+";"+cell+";1";
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
				send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_FIGHT_CHANGE_OPTION_PACKET_TO_MAP(Maps map,char s,char option, int guid)
	{
		String packet = "Go"+s+option+guid;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_FIGHT_PLAYER_READY_TO_FIGHT(Fight fight,int teams, int guid, boolean b)
	{
		String packet = "GR"+(b?"1":"0")+guid;
		if(fight.get_state() != 2)return;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			if(f.hasLeft())continue;
				send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight: Send>>"+packet);
	}

	public static void GAME_SEND_GJK_PACKET(Player out,int state,int cancelBtn,int duel,int spec,int time,int unknown)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("GJK").append(state).append("|").append(cancelBtn).append("|").append(duel).append("|").append(spec).append("|").append(time).append("|").append(unknown);
		send(out,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet.toString());
	}

	public static void GAME_SEND_FIGHT_PLACES_PACKET(Client out,String places, int team)
	{
		String packet = "GP"+places+"|"+team;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_Im_PACKET_TO_ALL(String str)
	{
		String packet = "Im"+str; 
		for(Player perso : World.data.getOnlinePersos())
			send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_Im_PACKET(Player out,String str)
	{
		String packet = "Im"+str;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_ILS_PACKET(Player out,int i)
	{
		String packet = "ILS"+i;
		send(out,packet);

	}public static void GAME_SEND_ILF_PACKET(Player P,int i)
	{
		String packet = "ILF"+i;
		send(P,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_Im_PACKET_TO_MAP(Maps map, String id)
	{
		String packet = "Im"+id;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	public static void GAME_SEND_eUK_PACKET_TO_MAP(Maps map, int guid, int emote)
	{
		String packet = "eUK"+guid+"|"+emote;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	public static void GAME_SEND_Im_PACKET_TO_FIGHT(Fight fight,int teams, String id)
	{
		String packet = "Im"+id;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	
	public static void GAME_SEND_MESSAGE(Player out,String mess, String color)
	{
		String packet = "cs<font color='#"+color+"'>"+mess+"</font>";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_MESSAGE_TO_MAP(Maps map,String mess, String color)
	{
		String packet = "cs<font color='#"+color+"'>"+mess+"</font>";
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}

	public static void GAME_SEND_GA903_ERROR_PACKET(Client out, char c,int guid)
	{
		String packet = "GA;903;"+guid+";"+c;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_GIC_PACKETS_TO_FIGHT(Fight fight,int teams)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("GIC|");
		for(Fighter p : fight.getFighters(3))
		{
			if(p == null || p.get_fightCell(false) == null)continue;
			packet.append(p.getGUID()).append(";").append(p.get_fightCell(false).getId()).append(";1|");
		}
		for(Fighter perso:fight.getFighters(teams))
		{
			if(perso == null || perso.hasLeft())continue;
			if(perso.getPersonnage() == null || !perso.getPersonnage().isOnline())continue;
			send(perso.getPersonnage(),packet.toString());
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight: Send>>"+packet.toString());
	}
	public static void GAME_SEND_GIC_PACKET_TO_FIGHT(Fight fight,int teams,Fighter f)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("GIC|").append(f.getGUID()).append(";").append(f.get_fightCell(false).getId()).append(";1|");

		for(Fighter perso:fight.getFighters(teams))
		{
			if(perso.hasLeft())continue;
			if(perso.getPersonnage() == null || !perso.getPersonnage().isOnline())continue;
			send(perso.getPersonnage(),packet.toString());
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight: Send>>"+packet.toString());
	}
	public static void GAME_SEND_GS_PACKET_TO_FIGHT(Fight fight,int teams)
	{
		String packet = "GS";
		for(Fighter f:fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			f.initBuffStats();
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}
	public static void GAME_SEND_GS_PACKET(Player out)
	{
		String packet = "GS";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}
	public static void GAME_SEND_GTL_PACKET_TO_FIGHT(Fight fight, int teams)
	{
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),fight.getGTL());
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+fight.getGTL());
	}
	public static void GAME_SEND_GTL_PACKET(Player out,Fight fight)
	{
		String packet = fight.getGTL();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}
	public static void GAME_SEND_GTM_PACKET_TO_FIGHT(Fight fight, int teams)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("GTM");
		for(Fighter f : fight.getFighters(3))
		{
			packet.append("|").append(f.getGUID()).append(";");
			if(f.isDead())
			{
				packet.append("1");
				continue;
			}else
			packet.append("0;").append(f.getPDV()+";").append(f.getPA()+";").append(f.getPM()+";");
			packet.append((f.isHide()?"-1":f.get_fightCell(false).getId())).append(";");//On envoie pas la cell d'un invisible :p
			packet.append(";");//??
			packet.append(f.getPDVMAX());
		}
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet.toString());
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet.toString());
	}

	public static void GAME_SEND_GAMETURNSTART_PACKET_TO_FIGHT(Fight fight,int teams, int guid, int time)
	{
		String packet = "GTS"+guid+"|"+time;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}
	public static void GAME_SEND_GAMETURNSTART_PACKET(Player P,int guid, int time)
	{
		String packet = "GTS"+guid+"|"+time;
		send(P,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}
	public static void GAME_SEND_GV_PACKET(Player P)
	{
		String packet = "GV";
		send(P,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}
	public static void GAME_SEND_PONG(Client out)
	{
		String packet = "pong";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_QPONG(Client out)
	{
		String packet = "qpong";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_GAS_PACKET_TO_FIGHT(Fight fight,int teams, int guid)
	{
		String packet = "GAS"+guid;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}
	
	public static void GAME_SEND_GA_PACKET_TO_FIGHT(Fight fight,int teams, int actionID,String s1, String s2){
		String packet = "GA;"+actionID+";"+s1;
		if(!s2.equals(""))
			packet+=";"+s2;
		
		for(Fighter f : fight.getFighters(teams)){
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
	}
	
	public static void GAME_SEND_GA_PACKET(Client out, String actionID,String s0,String s1, String s2)
	{
		String packet = "GA"+actionID+";"+s0;
		if(!s1.equals(""))
			packet += ";"+s1;
		if(!s2.equals(""))
			packet+=";"+s2;
		
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_GA_PACKET_TO_FIGHT(Fight fight,int teams,int gameActionID,String s1, String s2,String s3)
	{
		String packet = "GA"+gameActionID+";"+s1+";"+s2+";"+s3;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}
	
	public static void GAME_SEND_GAMEACTION_TO_FIGHT(Fight fight, int teams,String packet)
	{
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}

	public static void GAME_SEND_GAF_PACKET_TO_FIGHT(Fight fight, int teams, int i1,int guid)
	{
		String packet = "GAF"+i1+"|"+guid;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}

	public static void GAME_SEND_BN(Player out)
	{
		String packet = "BN";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_BN(Client out)
	{
		String packet = "BN";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_GAMETURNSTOP_PACKET_TO_FIGHT(Fight fight,int teams, int guid)
	{
		String packet = "GTF"+guid;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}

	public static void GAME_SEND_GTR_PACKET_TO_FIGHT(Fight fight, int teams,int guid)
	{
		String packet = "GTR"+guid;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}

	public static void GAME_SEND_EMOTICONE_TO_MAP(Maps map,int guid, int id)
	{
		String packet = "cS"+guid+"|"+id;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}

	public static void GAME_SEND_SPELL_UPGRADE_FAILED(Client _out)
	{
		String packet = "SUE";
		send(_out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_SPELL_UPGRADE_SUCCED(Client _out,int spellID,int level)
	{
		String packet = "SUK"+spellID+"~"+level;
		send(_out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_SPELL_LIST(Player perso)
	{
		String packet = perso.parseSpellsList();
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_FIGHT_PLAYER_DIE_TO_FIGHT(Fight fight, int teams,int guid)
	{
		String packet = "GA;103;"+guid+";"+guid;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft() || f.getPersonnage() == null)continue;
			if(f.getPersonnage().isOnline())
				send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}

	public static void GAME_SEND_FIGHT_GE_PACKET_TO_FIGHT(Fight fight, int teams, int win)
	{
		String packet = fight.GetGE(win);
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft() || f.getPersonnage() == null)continue;
			if(f.getPersonnage().isOnline())
				send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}
	
	public static void GAME_SEND_FIGHT_GE_PACKET(Client out,Fight fight, int win)
	{
		String packet = fight.GetGE(win);
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet);
	}
	
	public static void GAME_SEND_FIGHT_GIE_TO_FIGHT(Fight fight, int teams,int mType,int cible,int value,String mParam2,String mParam3,String mParam4, int turn,int spellID)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("GIE").append(mType).append(";").append(cible).append(";").append(value).append(";").append(mParam2).append(";").append(mParam3).append(";").append(mParam4).append(";").append(turn).append(";").append(spellID);
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft() || f.getPersonnage() == null)continue;
			if(f.getPersonnage().isOnline())
			send(f.getPersonnage(),packet.toString());
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight : Send>>"+packet.toString());
	}
	
	public static void GAME_SEND_MAP_FIGHT_GMS_PACKETS_TO_FIGHT(Fight fight, int teams,Maps map)
	{
		String packet = map.getFightersGMsPackets();
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight: Send>>"+packet);
	}

	public static void GAME_SEND_MAP_FIGHT_GMS_PACKETS(Fight fight,Maps map, Player _perso)
	{
		String packet = map.getFightersGMsPackets();
		send(_perso, packet);
		
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight: Send>>"+packet);
	}
	
	public static void GAME_SEND_FIGHT_PLAYER_JOIN(Fight fight,int teams, Fighter _fighter)
	{
		String packet = _fighter.getGmPacket('+');
		
		for(Fighter f : fight.getFighters(teams))
		{
			if (f != _fighter)
			{
				if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
				if(f.getPersonnage() != null && f.getPersonnage().getAccount().getGameClient() != null)
					send(f.getPersonnage(),packet);
			}
		}
		
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight: Send>>"+packet);
	}
	
	public static void GAME_SEND_cMK_PACKET(Player perso,String suffix,int guid,String name,String msg)
	{
		String packet = "cMK"+suffix+"|"+guid+"|"+name+"|"+msg;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_FIGHT_LIST_PACKET(Client out,Maps map)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("fL");
		for(Entry<Integer,Fight> entry : map.getFights().entrySet())
		{
			if(packet.length()>2)
				packet.append("|");
			packet.append(entry.getValue().parseFightInfos());
		}
		send(out,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet.toString());
	}
	
	public static void GAME_SEND_cMK_PACKET_TO_MAP(Maps map,String suffix,int guid,String name,String msg)
	{
		String packet = "cMK"+suffix+"|"+guid+"|"+name+"|"+msg;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	public static void GAME_SEND_cMK_PACKET_TO_GUILD(Guild g,String suffix,int guid,String name,String msg)
	{
		String packet = "cMK"+suffix+"|"+guid+"|"+name+"|"+msg;
		for(Player perso : g.getMembers())
		{
			if(perso == null || !perso.isOnline())continue;
					send(perso,packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Guild: Send>>"+packet);
	}
	public static void GAME_SEND_cMK_PACKET_TO_ALL(String suffix,int guid,String name,String msg)
	{
		String packet = "cMK"+suffix+"|"+guid+"|"+name+"|"+msg;
		for(Player perso : World.data.getOnlinePersos())
			send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: ALL("+World.data.getOnlinePersos().size()+"): Send>>"+packet);
	}
	public static void GAME_SEND_cMK_PACKET_TO_ALIGN(String suffix,int guid,String name,String msg, Player _perso)
	{
		String packet = "cMK"+suffix+"|"+guid+"|"+name+"|"+msg;
		for(Player perso : World.data.getOnlinePersos())
		{
			if(perso.getAlign() == _perso.getAlign())
			{
				send(perso,packet);
			}
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: ALL("+World.data.getOnlinePersos().size()+"): Send>>"+packet);
	}
	public static void GAME_SEND_cMK_PACKET_TO_ADMIN(String suffix,int guid,String name,String msg)
	{
		String packet = "cMK"+suffix+"|"+guid+"|"+name+"|"+msg;
		for(Player perso : World.data.getOnlinePersos())if(perso.isOnline())if(perso.getAccount() != null)if(perso.getAccount().getGmLvl()>0)send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: ALL("+World.data.getOnlinePersos().size()+"): Send>>"+packet);
	}
	public static void GAME_SEND_cMK_PACKET_TO_FIGHT(Fight fight,int teams,String suffix,int guid,String name,String msg)
	{
		String packet = "cMK"+suffix+"|"+guid+"|"+name+"|"+msg;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight: Send>>"+packet);
	}
	
	public static void GAME_SEND_GDZ_PACKET_TO_FIGHT(Fight fight,int teams,String suffix,int cell,int size,int unk)
	{
		String packet = "GDZ"+suffix+cell+";"+size+";"+unk;
		
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight: Send>>"+packet);
	}
	
	public static void GAME_SEND_GDC_PACKET_TO_FIGHT(Fight fight,int teams,int cell)
	{
		String packet = "GDC"+cell;
		
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
			send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight: Send>>"+packet);
	}
	
	public static void GAME_SEND_GA2_PACKET(Client out, int guid)
	{
		String packet = "GA;2;"+guid+";";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_CHAT_ERROR_PACKET(Client out,String name)
	{
		String packet = "cMEf"+name;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_eD_PACKET_TO_MAP(Maps map,int guid, int dir)
	{
		String packet = "eD"+guid+"|"+dir;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}

	public static void GAME_SEND_ECK_PACKET(Player out, int type,String str)
	{
		String packet = "ECK"+type;
		if(!str.equals(""))packet += "|"+str;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);	
	}
	
	public static void GAME_SEND_ECK_PACKET(Client out, int type,String str)
	{
		String packet = "ECK"+type;
		if(!str.equals(""))packet += "|"+str;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);	
	}
	
	public static void GAME_SEND_ITEM_VENDOR_LIST_PACKET(Client out, Npc npc)
	{
		String packet = "EL"+npc.getTemplate().getObjectList();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);	
	}
	
	public static void GAME_SEND_ITEM_LIST_PACKET_PERCEPTEUR(Client out, Collector perco)
	{
		String packet = "EL"+perco.getItemPercepteurList();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);	
	}
	
	public static void GAME_SEND_ITEM_LIST_PACKET_SELLER(Player p, Player out)
	{
		String packet = "EL"+p.parseStoreItemsList();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);	
	}
	
	public static void GAME_SEND_EV_PACKET(Client out)
	{
		String packet = "EV";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);	
	}
	
	public static void GAME_SEND_DCK_PACKET(Client out, int id)
	{
		String packet = "DCK"+id;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);	
	}

	public static void GAME_SEND_QUESTION_PACKET(Client out,String str)
	{
		String packet = "DQ"+str;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_END_DIALOG_PACKET(Client out)
	{
		String packet = "DV";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_CONSOLE_MESSAGE_PACKET(Client out, String mess)
	{
		String packet = "BAT2"+mess;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_BUY_ERROR_PACKET(Client out)
	{
		String packet = "EBE";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_SELL_ERROR_PACKET(Client out)
	{
		String packet = "ESE";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_BUY_OK_PACKET(Client out)
	{
		String packet = "EBK";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_OBJECT_QUANTITY_PACKET(Player out, Object obj)
	{
		String packet = "OQ"+obj.getId()+"|"+obj.getQuantity();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_OAKO_PACKET(Player out, Object obj)
	{
		String packet = "OAKO"+obj.parseItem();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_ESK_PACKEt(Player out)
	{
		String packet = "ESK";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_REMOVE_ITEM_PACKET(Player out, int guid)
	{
		String packet = "OR"+guid;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_DELETE_OBJECT_FAILED_PACKET(Client out)
	{
		String packet = "OdE";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_OBJET_MOVE_PACKET(Player out,Object obj)
	{
		String packet = "OM"+obj.getId()+"|";
		if(obj.getPosition() != ObjectPosition.NO_EQUIPED)
			packet += obj.getPosition().getValue();
		
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_EMOTICONE_TO_FIGHT(Fight fight, int teams, int guid, int id)
	{
		String packet = "cS"+guid+"|"+id;
		for(Fighter f : fight.getFighters(teams))
		{
			if(f.hasLeft())continue;
			if(f.getPersonnage() == null || !f.getPersonnage().isOnline())continue;
				send(f.getPersonnage(),packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight: Send>>"+packet);
	}

	public static void GAME_SEND_OAEL_PACKET(Client out)
	{
		String packet = "OAEL";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_NEW_LVL_PACKET(Client out, int lvl)
	{
		String packet = "AN"+lvl;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_MESSAGE_TO_ALL(String msg,String color)
	{
		String packet = "cs<font color='#"+color+"'>"+msg+"</font>";
		for(Player P : World.data.getOnlinePersos())
		{
			send(P,packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: ALL: Send>>"+packet);
	}

	public static void GAME_SEND_EXCHANGE_REQUEST_OK(Client out, int guid, int guidT, int msgID)
	{
		String packet = "ERK"+guid+"|"+guidT+"|"+msgID;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_EXCHANGE_REQUEST_ERROR(Client out, char c)
	{
		String packet = "ERE"+c;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_EXCHANGE_CONFIRM_OK(Client out, int type)
	{
		String packet = "ECK"+type;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_EXCHANGE_MOVE_OK(Player out,char type,String signe,String s1)
	{
		String packet = "EMK"+type+signe;
		if(!s1.equals(""))
			packet += s1;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_EXCHANGE_OTHER_MOVE_OK(Client out,char type,String signe,String s1)
	{
		String packet = "EmK"+type+signe;
		if(!s1.equals(""))
			packet += s1;
		send(out,packet);
	}
	public static void GAME_SEND_EXCHANGE_VALID(Client out, char c)
	{
		String packet = "EV"+c;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_GROUP_INVITATION_ERROR(Client out, String s) {
		String packet = "PIE"+s;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_GROUP_INVITATION(Client out,String n1, String n2)
	{
		String packet = "PIK"+n1+"|"+n2;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_GROUP_CREATE(Client out, Group g)
	{
		String packet = "PCK"+g.getChief().getName();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Groupe: Send>>"+packet);
	}

	public static void GAME_SEND_PL_PACKET(Client out, Group g)
	{
		String packet = "PL"+g.getChief().getId();
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Groupe: Send>>"+packet);
	}
	
	public static void GAME_SEND_PR_PACKET(Player out)
	{
		String packet = "PR";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_PV_PACKET(Client out,String s)
	{
		String packet = "PV"+s;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_ALL_PM_ADD_PACKET(Client out,Group g)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("PM+");
		boolean first = true;
		for(Player p : g.getPlayers())
		{
			if(!first) packet.append("|");
			packet.append(p.parseToPM());
			first = false;
		}
		send(out,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet.toString());
	}
	
	public static void GAME_SEND_PM_ADD_PACKET_TO_GROUP(Group g, Player p)
	{
		String packet = "PM+"+p.parseToPM();
		for(Player P : g.getPlayers())send(P,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Groupe: Send>>"+packet);
	}
	
	public static void GAME_SEND_PM_MOD_PACKET_TO_GROUP(Group g,Player p)
	{
		String packet = "PM~"+p.parseToPM();
		for(Player P : g.getPlayers())send(P,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Groupe: Send>>"+packet);
	}

	public static void GAME_SEND_PM_DEL_PACKET_TO_GROUP(Group g, int guid)
	{
		String packet = "PM-"+guid;
		for(Player P : g.getPlayers())send(P,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Groupe: Send>>"+packet);
	}

	public static void GAME_SEND_cMK_PACKET_TO_GROUP(Group g,String s, int guid, String name, String msg)
	{
		String packet = "cMK"+s+"|"+guid+"|"+name+"|"+msg+"|";
		for(Player P : g.getPlayers())send(P,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Groupe: Send>>"+packet);
	}

	public static void GAME_SEND_FIGHT_DETAILS(Client out, Fight fight)
	{
		if(fight == null)return;
		StringBuilder packet = new StringBuilder();
		packet.append("fD").append(fight.getId()).append("|");
		for(Fighter f : fight.getFighters(1))packet.append(f.getPacketsName()).append("~").append(f.get_lvl()).append(";");
		packet.append("|");
		for(Fighter f : fight.getFighters(2))packet.append(f.getPacketsName()).append("~").append(f.get_lvl()).append(";");
		send(out,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet.toString());
	}

	public static void GAME_SEND_IQ_PACKET(Player perso, int guid,	int qua)
	{
		String packet = "IQ"+guid+"|"+qua;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_JN_PACKET(Player perso, int jobID,	int lvl)
	{
		String packet = "JN"+jobID+"|"+lvl;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_GDF_PACKET_TO_MAP(Maps map, Case cell)
	{
		int cellID = cell.getId();
		InteractiveObject object = cell.getInteractiveObject();
		String packet = "GDF|"+cellID+";"+object.getState()+";"+(object.isInteractive()?"1":"0");
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	
	public static void GAME_SEND_GDF_PACKET_TO_MAP_TO_FIGHT(Fight fight) {
		String packet = "GDF|";
		
		for(Case c: fight.getMap().getCases().values())
			packet += getGdf(c.getId());
		ArrayList<Fighter> players = new ArrayList<>();
		
		players.addAll(fight.getFighters(0));
		players.addAll(fight.getFighters(1));
		
		for(Fighter f : players) 
			if(f.getPersonnage() != null)
				send(f.getPersonnage(),packet);
	}
	
	private static String getGdf(int cellID) {
		return cellID+";"+Constants.IOBJECT_STATE_EMPTY2+";1|";
	}
	
	public static void GAME_SEND_GA_PACKET_TO_MAP(Maps map, String gameActionID, int actionID,String s1, String s2)
	{
		String packet = "GA"+gameActionID+";"+actionID+";"+s1;
		if(!s2.equals(""))packet += ";"+s2;
		
		for(Player z : map.getPlayers()) send(z,packet);

	}

	public static void GAME_SEND_EL_BANK_PACKET(Player perso)
	{
		String packet = "EL"+perso.parseBankPacket();
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_EL_TRUNK_PACKET(Player perso, Trunk t)
	{
		String packet = "EL"+t.parseToTrunkPacket();
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_EXCHANGE_MOVE_OK_FM(Player out, char type, String signe, String s1)
	{
		String packet = "EmK" + type + signe;
		if(!s1.equals(""))
			packet += s1;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);		
	}

	/*public static void GAME_SEND_JX_PACKET(Player perso,ArrayList<StatsMetier> SMs)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("JX");
		for(StatsMetier sm : SMs)
		{
			packet.append("|").append(sm.getTemplate().getId()).append(";").append(sm.get_lvl()).append(";").append(sm.getXpString(";")).append(";");
		}
		send(perso,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet.toString());
	}
	public static void GAME_SEND_JO_PACKET(Player perso,ArrayList<StatsMetier> SMs)
	{
		for(StatsMetier sm : SMs)
		{
			String packet = "JO"+sm.getID()+"|"+sm.getOptBinValue()+"|2";//FIXME 2=?
			send(perso,packet);
			if(Server.config.isDebug())
				Log.addToSockLog("Game: Send>>"+packet);
		}
	}
	public static void GAME_SEND_JS_PACKET(Player perso,ArrayList<StatsMetier> SMs)
	{
		String packet = "JS";
		for(StatsMetier sm : SMs)
		{
			packet += sm.parseJS();
		}
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}*/
	
	public static void GAME_SEND_JX_PACKET(Player perso, ArrayList<JobStat> SMs)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("JX");
		for(JobStat sm : SMs)
			packet.append("|").append(sm.getTemplate().getId()).append(";").append(sm.get_lvl()).append(";").append(sm.getXpString(";")).append(";");
		send(perso,packet.toString());	
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet.toString());
	}
	
	public static void GAME_SEND_JO_PACKET(Player perso, ArrayList<JobStat> JobStats)
	{
		String packet = "";
		for(JobStat SM : JobStats) {
			packet = "JO"+ SM.getPosition() +"|"+ SM.getOptBinValue() +"|"+ SM.getSlotsPublic();
			send(perso, packet);		
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>> "+packet);
	}
	
	public static void GAME_SEND_JO_PACKET(Player perso, JobStat SM)
	{
		String packet = "JO"+ SM.getPosition() +"|"+ SM.getOptBinValue() +"|"+ SM.getSlotsPublic();
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_JS_PACKET(Player perso, ArrayList<JobStat> SMs)
	{
		String packet = "JS";
		for(JobStat sm : SMs)
			packet += sm.parseJS();
		send(perso, packet);	
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>> "+packet);
	}
	
	
	public static void GAME_SEND_EsK_PACKET(Player perso, String str)
	{
		String packet = "EsK"+str;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_FIGHT_SHOW_CASE(ArrayList<GameClient> PWs, int guid, int cellID)
	{
		String packet = "Gf"+guid+"|"+cellID;
		for(Client PW : PWs)
		{
			send(PW,packet);
		}
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Fight: Send>>"+packet);
	}
	
	public static void GAME_SEND_Ea_PACKET(Player perso, String str)
	{
		String packet = "Ea"+str;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_EA_PACKET(Player perso, String str)
	{
		String packet = "EA"+str;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_Ec_PACKET(Player perso, String str)
	{
		String packet = "Ec"+str;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_Em_PACKET(Player perso, String str)
	{
		String packet = "Em"+str;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_IO_PACKET_TO_MAP(Maps map,int guid,String str)
	{
		String packet = "IO"+guid+"|"+str;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	
	public static void GAME_SEND_FRIENDLIST_PACKET(Player perso)
	{
		String packet = "FL"+perso.getAccount().parseFriend();
		send(perso,packet);
		if(perso.getWife() != 0)
		{
			String packet2 = "FS" + perso.get_wife_friendlist();
			send(perso,packet2);
			if(Server.config.isDebug())
				Log.addToSockLog("Game: Send>>"+packet2);
		} 
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_FRIEND_ONLINE(Player logando, Player amigo) 
	{
		String packet = "Im0143;"+logando.getAccount().getPseudo()+" (<b><a href='asfunction:onHref,ShowPlayerPopupMenu,"+logando.getName()+"'>"+logando.getName()+"</a></b>)";
		send(amigo, packet);
		if (Server.config.isDebug())
		Log.addToSockLog("Game: Send>>" + packet);
	}

	public static void GAME_SEND_FA_PACKET(Player perso, String str)
	{
		String packet = "FA"+str;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_FD_PACKET(Player perso, String str)
	{
		String packet = "FD"+str;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	public static void GAME_SEND_Rp_PACKET(Player perso, MountPark MP)
	{
		StringBuilder packet = new StringBuilder();
		if(MP == null)return;
		
		packet.append("Rp").append(MP.getOwner()).append(";").append(MP.getPrice()).append(";").append(MP.getSize()).append(";").append(MP.getObjectNumb()).append(";");
			
		Guild G = MP.getGuild();
		//Si une guilde est definie
		if(G != null)
		{
			packet.append(G.getName()).append(";").append(G.getEmblem());
		}
		else
		{
			packet.append(";");
		}
		
		send(perso,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet.toString());
	}
	public static void GAME_SEND_OS_PACKET(Player perso, int pano)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("OS");
		int num = perso.getNumbEquipedItemOfPanoplie(pano);
		if(num <= 0) packet.append("-").append(pano);
		else
		{
			packet.append("+").append(pano).append("|");
			ObjectSet IS = World.data.getItemSet(pano);
			if(IS != null)
			{
				StringBuilder items = new StringBuilder();
				//Pour chaque objet de la pano
				for(ObjectTemplate OT : IS.getItemTemplates())
				{
					//Si le joueur l'a �quip�
					if(perso.hasEquiped(OT.getId()))
					{
						//On l'ajoute au packet
						if(items.length() >0)items.append(";");
						items.append(OT.getId());
					}
				}
				packet.append(items.toString()).append("|").append(IS.getBonusStatByItemNumb(num).parseToItemSetStats());
			}
		}	
		send(perso,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet.toString());
	}

	public static void GAME_SEND_MOUNT_DESCRIPTION_PACKET(Player perso,Mount DD)
	{
		String packet = "Rd"+DD.parse();
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_Rr_PACKET(Player perso, String str)
	{
		String packet = "Rr"+str;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_ALTER_GM_PACKET(Maps map,	Player perso)
	{
		String packet = "GM|~"+perso.getHelper().getGmPacket();
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	
	public static void GAME_SEND_Ee_PACKET(Player perso, char c,String s)
	{
		String packet = "Ee"+c+s;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_cC_PACKET(Player perso, char c,String s)
	{
		String packet = "cC"+c+s;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_ADD_NPC_TO_MAP(Maps map, Npc npc)
	{
		String packet = "GM|+"+npc.getHelper().getGmPacket();
		for(Player z : map.getPlayers()) send(z,packet);
	}
	
	public static void GAME_SEND_ADD_PERCO_TO_MAP(Maps map){
        Collector collector = World.data.getCollector(map);
		String packet = "GM|+"+collector.getHelper().getGmPacket();
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}

	public static void GAME_SEND_GDO_PACKET_TO_MAP(Maps map, char c,int cell, int itm, int i)
	{
		String packet = "GDO"+c+cell+";"+itm+";"+i;
		for(Player z : map.getPlayers()) send(z,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Map: Send>>"+packet);
	}
	
	public static void GAME_SEND_GDO_PACKET(Player p, char c,int cell, int itm, int i)
	{
		String packet = "GDO"+c+cell+";"+itm+";"+i;
		send(p,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_ZC_PACKET(Player p,int a)
	{
		String packet = "ZC"+a;
		send(p,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_GIP_PACKET(Player p,int a)
	{
		String packet = "GIP"+a;
		send(p,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_gn_PACKET(Player p)
	{
		String packet = "gn";
		send(p,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_gC_PACKET(Player p, String s)
	{
		String packet = "gC"+s;
		send(p,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_gV_PACKET(Player p)
	{
		String packet = "gV";
		send(p,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_gIM_PACKET(Player p, Guild g, char c)
	{
		String packet = "gIM"+c;
		switch(c)
		{
			case '+':
				packet += g.parseMembersToGM();
			break;
		}
		send(p,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_gIB_PACKET(Player p, String infos)
	{
		String packet = "gIB"+infos;
		send(p,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_gIH_PACKET(Player p, String infos)
	{
		String packet = "gIH"+infos;
		send(p,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_gS_PACKET(Player p, GuildMember gm)
	{
		StringBuilder packet = new StringBuilder();
		packet.append("gS").append(gm.getGuild().getName()).append("|").append(gm.getGuild().getEmblem().replace(',', '|')).append("|").append(gm.parseRights());
		send(p,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet.toString());
	}
	
	public static void GAME_SEND_gJ_PACKET(Player p, String str)
	{
		String packet = "gJ"+str;
		send(p,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_gK_PACKET(Player p, String str)
	{
		String packet = "gK"+str;
		send(p,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_gIG_PACKET(Player p, Guild g)
	{
		long xpMin = World.data.getExpLevel(g.getLevel()).guilde;
		long xpMax;
		if(World.data.getExpLevel(g.getLevel()+1) == null)
		{
			xpMax = -1;
		}else
		{
			xpMax = World.data.getExpLevel(g.getLevel()+1).guilde;
		}
		StringBuilder packet = new StringBuilder();
		packet.append("gIG").append((g.getMembers().size() > 9 ? 1 : 0)).append("|").append(g.getLevel()).append("|").append(xpMin).append("|").append(g.getExperience()).append("|").append(xpMax);
		send(p,packet.toString());
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet.toString());
	}
	
	public static void REALM_SEND_MESSAGE(Client out, String args)
	{
		String packet = "M"+args;
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_WC_PACKET(Player perso)
	{
		String packet = "WC"+perso.parseZaapList();
		send(perso.getAccount().getGameClient(),packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}

	public static void GAME_SEND_WV_PACKET(Player out)
	{
		String packet = "WV";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_ZAAPI_PACKET(Player perso, String list) {
		String packet = "Wc" + perso.getMap().getId()+ "|"+list;
		send(perso, packet);
		Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_CLOSE_ZAAPI_PACKET(Player out) {
		String packet = "Wv";
		send(out, packet);
		Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_WUE_PACKET(Player out)
	{
		String packet = "WUE";
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>"+packet);
	}
	
	public static void GAME_SEND_EMOTE_LIST(Player perso,String s, String s1)
	{
		String packet = "eL"+s+"|"+s1;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_NO_EMOTE(Player out)
	{
		String packet = "eUE";
		send(out, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}

	public static void REALM_SEND_TOO_MANY_PLAYER_ERROR(Client _out)
	{
		String packet = "AlEw";
		send(_out, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void REALM_SEND_REQUIRED_APK(Client out)
	{
	    String chars = "abcdefghijklmnopqrstuvwxyz"; // Tu supprimes les lettres dont tu ne veux pas
	    String pass = "";
	    for(int x=0;x<5;x++)
	    {
	       int i = (int)Math.floor(Math.random() * 26); // Si tu supprimes des lettres tu diminues ce nb
	       pass += chars.charAt(i);
	    }
	    Console.instance.println(pass);
	    
		String packet = "APK"+pass;
				send(out, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_ADD_ENEMY(Player out, Player pr)
	{
		
		String packet = "iAK"+pr.getAccount().getName()+";2;"+pr.getName()+";36;10;0;100.FL.";
		send(out, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_iAEA_PACKET(Player out)
	{
		
		String packet = "iAEA.";
		send(out, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_ENEMY_LIST(Player perso)
	{
		
		String packet = "iL"+perso.getAccount().parseEnemy();
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_iD_COMMANDE(Player perso, String str)
	{
		String packet = "iD"+str;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_BWK(Player perso, String str)
	{
		String packet = "BWK"+str;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_KODE(Player perso, String str)
	{
		String packet = "K"+str;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_hOUSE(Player perso, String str) 
	{
		String packet = "h"+str;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
		
	}
	
	public static void GAME_SEND_FORGETSPELL_INTERFACE(char sign,Player perso)
	{
		String packet = "SF"+sign;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_R_PACKET(Player perso, String str)
	{
		String packet = "R"+str;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_gIF_PACKET(Player perso, String str)
	{
		String packet = "gIF"+str;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_gITM_PACKET(Player perso, String str)
	{
		String packet = "gITM"+str;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_gITp_PACKET(Player perso, String str)
	{
		String packet = "gITp"+str;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_gITP_PACKET(Player perso, String str)
	{
		String packet = "gITP"+str;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_IH_PACKET(Player perso, String str)
	{
		String packet = "IH"+str;
		send(perso, packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	
	public static void GAME_SEND_FLAG_PACKET(Player perso, Player cible) 
	{ 
		String packet = "IC"+cible.getMap().getX()+"|"+cible.getMap().getY();
		send(perso,packet); 
		if(Server.config.isDebug()) 
			Log.addToSockLog("Game: Send>>"+packet); 
	}
	
	public static void GAME_SEND_DELETE_FLAG_PACKET(Player perso) 
	{ 
		String packet = "IC|"; 
		send(perso,packet); 
		if(Server.config.isDebug()) 
			Log.addToSockLog("Game: Send>>"+packet); 
	}
	
	public static void GAME_SEND_gT_PACKET(Player perso, String str) 
	{ 
		String packet = "gT"+str; 
		send(perso,packet); 
		if(Server.config.isDebug()) 
			Log.addToSockLog("Game: Send>>"+packet); 
	}
	
	public static void GAME_SEND_GUILDHOUSE_PACKET(Player perso) 
	{ 
		String packet = "gUT"; 
		send(perso,packet); 
		if(Server.config.isDebug()) 
			Log.addToSockLog("Game: Send>>"+packet); 
	}
	
	public static void GAME_SEND_GUILDENCLO_PACKET(Player perso) 
	{ 
		String packet = "gUF"; 
		send(perso,packet); 
		if(Server.config.isDebug()) 
			Log.addToSockLog("Game: Send>>"+packet); 
	}
	
	/**HDV**/
	public static void GAME_SEND_EHm_PACKET(Player out, String sign,String str)
	{
		String packet = "EHm"+sign + str;
		
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	public static void GAME_SEND_EHM_PACKET(Player out, String sign,String str)
	{
		String packet = "EHM"+sign + str;
		
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	public static void GAME_SEND_EHP_PACKET(Player out, int templateID)	//Packet d'envoie du prix moyen du template (En r�ponse a un packet EHP)
	{
		
		String packet = "EHP"+templateID+"|"+World.data.getObjectTemplate(templateID).getAvgPrice();
		
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	public static void GAME_SEND_EHl(Player out, Hdv seller,int templateID)
	{
		String packet = "EHl" + seller.parseToEHl(templateID);
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	public static void GAME_SEND_EHL_PACKET(Player out, int categ, String templates)	//Packet de listage des templates dans une cat�gorie (En r�ponse au packet EHT)
	{
		String packet = "EHL"+categ+"|"+templates;
		
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	public static void GAME_SEND_EHL_PACKET(Player out, String items)	//Packet de listage des objets en vente
	{
		String packet = "EHL"+items;
		
		send(out,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	public static void GAME_SEND_HDVITEM_SELLING(Player perso)
	{
		String packet = "EL";
		HdvEntry[] entries = perso.getAccount().getHdvItems(Math.abs(perso.getIsTradingWith()));	//R�cup�re un tableau de tout les items que le personnage � en vente dans l'HDV o� il est
		boolean isFirst = true;
		for(HdvEntry curEntry : entries)
		{
			if(curEntry == null)
				break;
			if(curEntry.isPurchased())
				continue;
			if(!isFirst)
				packet += "|";
			packet += curEntry.parseToEL();
			
		isFirst = false;
		}
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	}
	public static void GAME_SEND_WEDDING(Maps c, int action, int homme, int femme, int parlant)
	{
		String packet = "GA;"+action+";"+homme+";"+homme+","+femme+","+parlant;
		Player Homme = World.data.getPlayer(homme);
		send(Homme,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	} 
	public static void GAME_SEND_PF(Player perso, String str)
	{
		String packet = "PF"+str;
		send(perso,packet);
		if(Server.config.isDebug())
			Log.addToSockLog("Game: Send>>" + packet);
	} 
    public static void GAME_SEND_MERCHANT_LIST(Player P, int mapID)
    {
    	StringBuilder packet = new StringBuilder();
    	packet.append("GM|~");
    	if(World.data.getSeller(P.getMap()) == null) return;
        for (Integer pID : World.data.getSeller(P.getMap()))
        {
        	if(!World.data.getPlayer(pID).isOnline() && World.data.getPlayer(pID).isSeeSeller())
        	{
        		packet.append(World.data.getPlayer(pID).parseToMerchant()).append("|");
            }
        }
        if(packet.length() < 5) return;
        send(P, packet.toString());
        if(Server.config.isDebug())
        	Log.addToSockLog("Game: Send>>" + packet.toString());
    }
    
    public static void GAME_SEND_Eq_PACKET(Player Personnage, long Prix)
	{ 
    	send(Personnage, "Eq1|1|" + Prix);
    	if(Server.config.isDebug())
    		Log.addToSockLog("Game: Send>> " + "Eq1|1|" + Prix);
	}
    //TODO: A revoir pour le fm..
    public static void GAME_SEND_EXCHANGE_OTHER_MOVE_OK_FM(GameClient out,char type,String signe,String s1)
	{
		String packet = "EMK"+type+signe;
		if(!s1.equals(""))
			packet += s1;
		send(out,packet);
		if(Server.config.isDebug())
        	Log.addToSockLog("Game: Send>>" + packet);
	}
    
    public static void GAME_SEND_DELETE_STATS_ITEM_FM(Player perso, int id) 
	{
		String packet = "OR" + id;
		send(perso, packet);
		if(Server.config.isDebug())
        	Log.addToSockLog("Game: Send>>" + packet);		
	}
}
