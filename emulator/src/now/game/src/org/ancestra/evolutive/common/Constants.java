package org.ancestra.evolutive.common;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.enums.EmulatorInfos;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.object.ObjectPosition;
import org.ancestra.evolutive.object.ObjectTemplate;

import java.util.Map;
import java.util.TreeMap;

public class Constants {
    public static final int TIME_BY_TURN = 2900;
    //DEBUG
	public static int DEBUG_MAP_LIMIT 	=	20000;
	//ZAAPI <alignID,{mapID,mapID,...,mapID}>
	public static Map<Alignement, String> ZAAPI = new TreeMap<>();
	//ZAAP <mapID,cellID>
	public static Map<Integer, Integer> ZAAPS = new TreeMap<>();
	//BANIP
	public static String BAN_IP = "";
	
	public static boolean IPcompareToBanIP(String ip) {
		String[] split = BAN_IP.split(",");
		for(String ipsplit : split)
			if(ip.compareTo(ipsplit) == 0) 
				return true;		
		return false;
	}
	
	public static String getIp(String remoteAddress) {
		return remoteAddress.substring(1).split("\\:")[0];
	}
	
	public static String serverInfos() {
		long uptime = System.currentTimeMillis() - Server.config.getGameServer().getStartTime();
		int jour = (int) (uptime/(1000*3600*24));
		uptime %= (1000*3600*24);
		int hour = (int) (uptime/(1000*3600));
		uptime %= (1000*3600);
		int min = (int) (uptime/(1000*60));
		uptime %= (1000*60);
		int sec = (int) (uptime/(1000));
		
		String mess =	"<b>"+EmulatorInfos.SOFT_NAME.toString()+"</b>\n"
			+			"Uptime: "+jour+"j "+hour+"h "+min+"m "+sec+"s\n"
			+			"Connected: "+Server.config.getGameServer().getPlayerNumber()+"\n"
			+			"Reccord: "+Server.config.getGameServer().getMaxPlayer()+"";
		return mess;
	}
	
	//ETAT
	public static final int ETAT_NEUTRE				= 0;
	public static final int ETAT_SAOUL				= 1;
	public static final int ETAT_CAPT_AME			= 2;
	public static final int ETAT_PORTEUR			= 3;
	public static final int ETAT_PEUREUX			= 4;
	public static final int ETAT_DESORIENTE			= 5;
	public static final int ETAT_ENRACINE			= 6;
	public static final int ETAT_PESANTEUR			= 7;
	public static final int ETAT_PORTE				= 8;
	public static final int ETAT_MOTIV_SYLVESTRE	= 9;
	public static final int ETAT_APPRIVOISEMENT		= 10;
	public static final int ETAT_CHEVAUCHANT		= 11;
	//INTERACTIVE OBJET
	public static final int IOBJECT_STATE_FULL		= 1;
	public static final int IOBJECT_STATE_EMPTYING	= 2;
	public static final int IOBJECT_STATE_EMPTY		= 3;
	public static final int IOBJECT_STATE_EMPTY2	= 4;
	public static final int IOBJECT_STATE_FULLING	= 5;
	


	//Elements 
	public static final int ELEMENT_NULL		=	-1;
	public static final int ELEMENT_NEUTRE		= 	0;
	public static final int ELEMENT_TERRE		= 	1;
	public static final int ELEMENT_EAU			= 	2;
	public static final int ELEMENT_FEU			= 	3;
	public static final int ELEMENT_AIR			= 	4;
	//Classes
	public static final int CLASS_FECA			= 	1;
	public static final int CLASS_OSAMODAS		= 	2;
	public static final int CLASS_ENUTROF		= 	3;
	public static final int CLASS_SRAM			=	4;
	public static final int CLASS_XELOR			=	5;
	public static final int CLASS_ECAFLIP		=	6;
	public static final int CLASS_ENIRIPSA		=	7;
	public static final int CLASS_IOP			=	8;
	public static final int CLASS_CRA			=	9;
	public static final int CLASS_SADIDA		= 	10;
	public static final int CLASS_SACRIEUR		=	11;
	public static final int CLASS_PANDAWA		=	12;

	//GamePlay
	public static final int MAX_EFFECTS_ID 		=	1500;
	//Buff a v?rifier en d?but de tour
	public static final int[] BEGIN_TURN_BUFF	=	{91,92,93,94,95,96,97,98,99,100,108};
	//Buff des Armes
	public static final int[] ARMES_EFFECT_IDS	=	{91,92,93,94,95,96,97,98,99,100,101};
	//Buff a ne pas booster en cas de CC
	public static final int[] NO_BOOST_CC_IDS	=	{101};
	//Invocation Statiques
	public static final int[] STATIC_INVOCATIONS 		= 	{282,556};//Arbre et Cawotte s'tout :p
	
	//Verif d'Etat au lancement d'un sort {spellID,stateID}, ? completer avant d'activer
	public static final int[][] STATE_REQUIRED =
	{
		{699,Constants.ETAT_SAOUL},
		{690,Constants.ETAT_SAOUL}
	};
	
	//Buff d?clench? en cas de frappe
	public static final int[] ON_HIT_BUFFS		=	{9,79,107,788};
	
	//Effects
	public static final int STATS_ADD_PM2			= 	78;
	
	public static final int STATS_REM_PA			= 	101;
	public static final int STATS_ADD_VIE			= 	110;
	public static final int STATS_ADD_PA			= 	111;
	public static final int STATS_ADD_DOMA			=	112;
	
	public static final int STATS_MULTIPLY_DOMMAGE	=	114;
	public static final int STATS_ADD_CC			=	115;
	public static final int STATS_REM_PO			= 	116;
	public static final int STATS_ADD_PO			= 	117;
	public static final int STATS_ADD_FORC			= 	118;
	public static final int STATS_ADD_AGIL			= 	119;
	public static final int STATS_ADD_PA2			=	120;
	public static final int STATS_ADD_EC			=	122;
	public static final int STATS_ADD_CHAN			= 	123;
	public static final int STATS_ADD_SAGE			= 	124;
	public static final int STATS_ADD_VITA			= 	125;
	public static final int STATS_ADD_INTE			= 	126;
	public static final int STATS_REM_PM			= 	127;
	public static final int STATS_ADD_PM			= 	128;
	
	public static final int STATS_ADD_PERDOM		=	138;
	
	public static final int STATS_ADD_PDOM			=	142;
	
	public static final int STATS_REM_DOMA			= 	145;

	public static final int STATS_REM_CHAN			= 	152;
	public static final int STATS_REM_VITA			= 	153;
	public static final int STATS_REM_AGIL			= 	154;
	public static final int STATS_REM_INTE			= 	155;
	public static final int STATS_REM_SAGE			= 	156;
	public static final int STATS_REM_FORC			= 	157;
	public static final int STATS_ADD_PODS			= 	158;
	public static final int STATS_REM_PODS			= 	159;
	public static final int STATS_ADD_AFLEE			=	160;
	public static final int STATS_ADD_MFLEE			=	161;
	public static final int STATS_REM_AFLEE			=	162;
	public static final int STATS_REM_MFLEE			=	163;
	
	public static final int STATS_ADD_MAITRISE		=	165;
	
	public static final int STATS_REM_PA2			=	168;
	public static final int STATS_REM_PM2			=	169;
	
	public static final int STATS_REM_CC			=	171;
	
	public static final int STATS_ADD_INIT			= 	174;
	public static final int STATS_REM_INIT			= 	175;
	public static final int STATS_ADD_PROS			= 	176;
	public static final int STATS_REM_PROS			= 	177;
	public static final int STATS_ADD_SOIN			= 	178;
	public static final int STATS_REM_SOIN			= 	179;
	
	public static final int STATS_CREATURE			= 	182;
	
	public static final int STATS_ADD_RP_TER		=	210;
	public static final int STATS_ADD_RP_EAU 		=	211;
	public static final int STATS_ADD_RP_AIR		=	212;
	public static final int STATS_ADD_RP_FEU 		=	213;
	public static final int STATS_ADD_RP_NEU		= 	214;
	public static final int STATS_REM_RP_TER		=	215;
	public static final int STATS_REM_RP_EAU 		=	216;
	public static final int STATS_REM_RP_AIR		=	217;
	public static final int STATS_REM_RP_FEU 		=	218;
	public static final int STATS_REM_RP_NEU		= 	219;
	public static final int STATS_RETDOM			=	220;
	
	public static final int STATS_TRAPDOM			=	225;
	public static final int STATS_TRAPPER			=	226;
	
	public static final int STATS_ADD_R_FEU 		= 	240;
	public static final int STATS_ADD_R_NEU			=	241;
	public static final int STATS_ADD_R_TER			=	242;
	public static final int STATS_ADD_R_EAU			=	243;
	public static final int STATS_ADD_R_AIR			=	244;
	public static final int STATS_REM_R_FEU 		= 	245;
	public static final int STATS_REM_R_NEU			=	246;
	public static final int STATS_REM_R_TER			=	247;
	public static final int STATS_REM_R_EAU			=	248;
	public static final int STATS_REM_R_AIR			=	249;
	public static final int STATS_ADD_RP_PVP_TER	=	250;
	public static final int STATS_ADD_RP_PVP_EAU	=	251;
	public static final int STATS_ADD_RP_PVP_AIR	=	252;
	public static final int STATS_ADD_RP_PVP_FEU	=	253;
	public static final int STATS_ADD_RP_PVP_NEU	=	254;
	public static final int STATS_REM_RP_PVP_TER	=	255;
	public static final int STATS_REM_RP_PVP_EAU	=	256;
	public static final int STATS_REM_RP_PVP_AIR	=	257;
	public static final int STATS_REM_RP_PVP_FEU	=	258;
	public static final int STATS_REM_RP_PVP_NEU	=	259;
	public static final int STATS_ADD_R_PVP_TER		=	260;
	public static final int STATS_ADD_R_PVP_EAU		=	261;
	public static final int STATS_ADD_R_PVP_AIR		=	262;
	public static final int STATS_ADD_R_PVP_FEU		=	263;
	public static final int STATS_ADD_R_PVP_NEU		=	264;
	//Effets ID & Buffs
	public static final int EFFECT_PASS_TURN		= 	140;
	//Capture
	public static final int CAPTURE_MONSTRE			=	623;
	
	
	//Methodes
	public static TreeMap<Integer, Character> getStartSortsPlaces(int classID)
	{
		TreeMap<Integer,Character> start = new TreeMap<Integer,Character>();
		switch(classID)
		{
			case CLASS_FECA:
				start.put(3,'b');//Attaque Naturelle
				start.put(6,'c');//Armure Terrestre
				start.put(17,'d');//Glyphe Agressif
			break;
			case CLASS_SRAM:
				start.put(61,'b');//Sournoiserie
				start.put(72,'c');//Invisibilit?
				start.put(65,'d');//Piege sournois
			break;
			case CLASS_ENIRIPSA:
				start.put(125,'b');//Mot Interdit
				start.put(128,'c');//Mot de Frayeur
				start.put(121,'d');//Mot Curatif
			break;
			case CLASS_ECAFLIP:
				start.put(102,'b');//Pile ou Face
				start.put(103,'c');//Chance d'ecaflip
				start.put(105,'d');//Bond du felin
			break;
			case CLASS_CRA:
				start.put(161,'b');//Fleche Magique
				start.put(169,'c');//Fleche de Recul
				start.put(164,'d');//Fleche Empoisonn?e(ex Fleche chercheuse)
			break;
			case CLASS_IOP:
				start.put(143,'b');//Intimidation
				start.put(141,'c');//Pression
				start.put(142,'d');//Bond
			break;
			case CLASS_SADIDA:
				start.put(183,'b');//Ronce
				start.put(200,'c');//Poison Paralysant
				start.put(193,'d');//La bloqueuse
			break;
			case CLASS_OSAMODAS:
				start.put(34,'b');//Invocation de tofu
				start.put(21,'c');//Griffe Spectrale
				start.put(23,'d');//Cri de l'ours
			break;
			case CLASS_XELOR:
				start.put(82,'b');//Contre
				start.put(81,'c');//Ralentissement
				start.put(83,'d');//Aiguille
			break;
			case CLASS_PANDAWA:
				start.put(686,'b');//Picole
				start.put(692,'c');//Gueule de bois
				start.put(687,'d');//Poing enflamm?
			break;
			case CLASS_ENUTROF:
				start.put(51,'b');//Lancer de Piece
				start.put(43,'c');//Lancer de Pelle
				start.put(41,'d');//Sac anim?
			break;
			case CLASS_SACRIEUR:
				start.put(432,'b');//Pied du Sacrieur
				start.put(431,'c');//Chatiment Os?
				start.put(434,'d');//Attirance
			break;
		}
		return start;
	}
	
	public static TreeMap<Integer,SpellStats> getStartSorts(int classID)
	{
		TreeMap<Integer,SpellStats> start = new TreeMap<Integer,SpellStats>();
		switch(classID)
		{
			case CLASS_FECA:
				start.put(3,World.data.getSort(3).getStatsByLevel(1));//Attaque Naturelle
				start.put(6,World.data.getSort(6).getStatsByLevel(1));//Armure Terrestre
				start.put(17,World.data.getSort(17).getStatsByLevel(1));//Glyphe Agressif
			break;
			case CLASS_SRAM:
				start.put(61,World.data.getSort(61).getStatsByLevel(1));//Sournoiserie
				start.put(72,World.data.getSort(72).getStatsByLevel(1));//Invisibilit?
				start.put(65,World.data.getSort(65).getStatsByLevel(1));//Piege sournois
			break;
			case CLASS_ENIRIPSA:
				start.put(125,World.data.getSort(125).getStatsByLevel(1));//Mot Interdit
				start.put(128,World.data.getSort(128).getStatsByLevel(1));//Mot de Frayeur
				start.put(121,World.data.getSort(121).getStatsByLevel(1));//Mot Curatif
			break;
			case CLASS_ECAFLIP:
				start.put(102,World.data.getSort(102).getStatsByLevel(1));//Pile ou Face
				start.put(103,World.data.getSort(103).getStatsByLevel(1));//Chance d'ecaflip
				start.put(105,World.data.getSort(105).getStatsByLevel(1));//Bond du felin
			break;
			case CLASS_CRA:
				start.put(161,World.data.getSort(161).getStatsByLevel(1));//Fleche Magique
				start.put(169,World.data.getSort(169).getStatsByLevel(1));//Fleche de Recul
				start.put(164,World.data.getSort(164).getStatsByLevel(1));//Fleche Empoisonn?e(ex Fleche chercheuse)
			break;
			case CLASS_IOP:
				start.put(143,World.data.getSort(143).getStatsByLevel(1));//Intimidation
				start.put(141,World.data.getSort(141).getStatsByLevel(1));//Pression
				start.put(142,World.data.getSort(142).getStatsByLevel(1));//Bond
			break;
			case CLASS_SADIDA:
				start.put(183,World.data.getSort(183).getStatsByLevel(1));//Ronce
				start.put(200,World.data.getSort(200).getStatsByLevel(1));//Poison Paralysant
				start.put(193,World.data.getSort(193).getStatsByLevel(1));//La bloqueuse
			break;
			case CLASS_OSAMODAS:
				start.put(34,World.data.getSort(34).getStatsByLevel(1));//Invocation de tofu
				start.put(21,World.data.getSort(21).getStatsByLevel(1));//Griffe Spectrale
				start.put(23,World.data.getSort(23).getStatsByLevel(1));//Cri de l'ours
			break;
			case CLASS_XELOR:
				start.put(82,World.data.getSort(82).getStatsByLevel(1));//Contre
				start.put(81,World.data.getSort(81).getStatsByLevel(1));//Ralentissement
				start.put(83,World.data.getSort(83).getStatsByLevel(1));//Aiguille
			break;
			case CLASS_PANDAWA:
				start.put(686,World.data.getSort(686).getStatsByLevel(1));//Picole
				start.put(692,World.data.getSort(692).getStatsByLevel(1));//Gueule de bois
				start.put(687,World.data.getSort(687).getStatsByLevel(1));//Poing enflamm?
			break;
			case CLASS_ENUTROF:
				start.put(51,World.data.getSort(51).getStatsByLevel(1));//Lancer de Piece
				start.put(43,World.data.getSort(43).getStatsByLevel(1));//Lancer de Pelle
				start.put(41,World.data.getSort(41).getStatsByLevel(1));//Sac anim?
			break;
			case CLASS_SACRIEUR:
				start.put(432,World.data.getSort(432).getStatsByLevel(1));//Pied du Sacrieur
				start.put(431,World.data.getSort(431).getStatsByLevel(1));//Chatiment Forc?
				start.put(434,World.data.getSort(434).getStatsByLevel(1));//Attirance
			break;
		}
		return start;
	}
	
	public static int getBasePdv(int classID)
	{
		return 50;
	}

	public static int getReqPtsToBoostStatsByClass(int classID,int statID,int val)
	{
		switch(statID)
		{
			case 11://Vita
				return 1;
			case 12://Sage
				return 3;
			case 10://Force
				switch(classID)
				{
					case CLASS_SACRIEUR:
						return 3;
						
					case CLASS_FECA:
						if(val < 50)
							return 2;
						if(val < 150)
							return 3;
						if(val < 250)
							return 4;
						return 5;
						
					case CLASS_XELOR:
						if(val < 50)
							return 2;
						if(val < 150)
							return 3;
						if(val < 250)
							return 4;
						return 5;
						
					case CLASS_SRAM:
						if(val < 100)
							return 1;
						if(val < 200)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
					case CLASS_OSAMODAS:
						if(val < 50)
							return 2;
						if(val < 150)
							return 3;
						if(val < 250)
							return 4;
						return 5;
					
					case CLASS_ENIRIPSA:
						if(val < 50)
							return 2;
						if(val < 150)
							return 3;
						if(val < 250)
							return 4;
						return 5;
					
					case CLASS_PANDAWA:
						if(val < 50)
							return 1;
						if(val < 200)
							return 2;
						return 3;
						
					case CLASS_SADIDA:
						if(val < 50)
							return 1;
						if(val < 250)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
					case CLASS_CRA:
						if(val < 50)
							return 1;
						if(val < 150)
							return 2;
						if(val < 250)
							return 3;
						if(val < 350)
							return 4;
						return 5;
						
					case CLASS_ENUTROF:
						if(val < 50)
							return 1;
						if(val < 150)
							return 2;
						if(val < 250)
							return 3;
						if(val < 350)
							return 4;
						return 5;	
						
					case CLASS_ECAFLIP:
						if(val < 100)
							return 1;
						if(val < 200)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
					case CLASS_IOP:
						if(val < 100)
							return 1;
						if(val < 200)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
				}
			break;
			case 13://Chance
				switch(classID)
				{
					case CLASS_FECA:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
						
					case CLASS_XELOR:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
						
					case CLASS_SACRIEUR:
						return 3;
						
					case CLASS_SRAM:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
						
					case CLASS_SADIDA:
						if(val < 100)
							return 1;
						if(val < 200)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
					case CLASS_PANDAWA:
						if(val < 50)
							return 1;
						if(val < 200)
							return 2;
						return 3;
						
					case CLASS_IOP:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
					
					case CLASS_ENUTROF:
						if(val < 100)
							return 1;
						if(val < 150)
							return 2;
						if(val < 230)
							return 3;
						if(val < 330)
							return 4;
						return 5;
						
					case CLASS_OSAMODAS:
						if(val < 100)
							return 1;
						if(val < 200)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
					case CLASS_ECAFLIP:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
						
					case CLASS_ENIRIPSA:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
						
					case CLASS_CRA:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
				}
			break;
			case 14://Agilit?
				switch(classID)
				{
					case CLASS_FECA:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
					
					case CLASS_XELOR:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
						
					case CLASS_SACRIEUR:
						return 3;
						
					case CLASS_SRAM:
						if(val < 100)
							return 1;
						if(val < 200)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
					case CLASS_SADIDA:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
						
					case CLASS_PANDAWA:
						if(val < 50)
							return 1;
						if(val < 200)
							return 2;
						return 3;
						
					case CLASS_ENIRIPSA:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
					
					case CLASS_IOP:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
					
					case CLASS_ENUTROF:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;	
					
					case CLASS_ECAFLIP:
						if(val < 50)
							return 1;
						if(val < 100)
							return 2;
						if(val < 150)
							return 3;
						if(val < 200)
							return 4;
						return 5;
						
					case CLASS_CRA:
						if(val < 50)
							return 1;
						if(val < 100)
							return 2;
						if(val < 150)
							return 3;
						if(val < 200)
							return 4;
						return 5;
					
					case CLASS_OSAMODAS:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
				}
			break;
			case 15://Intelligence
				switch(classID)
				{
					case CLASS_XELOR:
						if(val < 100)
							return 1;
						if(val < 200)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
					case CLASS_FECA:
						if(val < 100)
							return 1;
						if(val < 200)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
					case CLASS_SACRIEUR:
						return 3;
					
					case CLASS_SRAM:
						if(val < 50)
							return 2;
						if(val < 150)
							return 3;
						if(val < 250)
							return 4;
						return 5;
						
					case CLASS_SADIDA:
						if(val < 100)
							return 1;
						if(val < 200)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
					case CLASS_ENUTROF:
						if(val < 20)
							return 1;
						if(val < 60)
							return 2;
						if(val < 100)
							return 3;
						if(val < 140)
							return 4;
						return 5;	
					
					case CLASS_PANDAWA:
						if(val < 50)
							return 1;
						if(val < 200)
							return 2;
						return 3;
						
					case CLASS_IOP:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
					
					case CLASS_ENIRIPSA:
						if(val < 100)
							return 1;
						if(val < 200)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
					case CLASS_CRA:
						if(val < 50)
							return 1;
						if(val < 150)
							return 2;
						if(val < 250)
							return 3;
						if(val < 350)
							return 4;
						return 5;
						
					case CLASS_OSAMODAS:
						if(val < 100)
							return 1;
						if(val < 200)
							return 2;
						if(val < 300)
							return 3;
						if(val < 400)
							return 4;
						return 5;
						
					case CLASS_ECAFLIP:
						if(val < 20)
							return 1;
						if(val < 40)
							return 2;
						if(val < 60)
							return 3;
						if(val < 80)
							return 4;
						return 5;
				}
			break;
		}
		return 5;
	}

	public static boolean isValidPlaceForItem(ObjectTemplate template, ObjectPosition place) {
		switch(template.getType()) {
			case AMULETTE:
				if(place == ObjectPosition.AMULETTE)
					return true;
				break;
			
			case ARC:
			case BAGUETTE:
			case BATON:
			case DAGUES:
			case EPEE:
			case MARTEAU:
			case PELLE:
			case HACHE:
			case OUTIL:
			case PIOCHE:
			case FAUX:
			case PIERRE_AME:
				if(place == ObjectPosition.ARME)
					return true;
				break;
			
			case ANNEAU:
				if(place == ObjectPosition.ANNEAU1 || place == ObjectPosition.ANNEAU2)
					return true;
				break;
			
			case CEINTURE:
				if(place == ObjectPosition.CEINTURE)
					return true;
				break;
			
			case BOTTES:
				if(place == ObjectPosition.BOTTES)
					return true;
				break;
			
			case COIFFE:
				if(place == ObjectPosition.COIFFE)
					return true;
				break;
			
			case CAPE:
			case SAC_DOS:
				if(place == ObjectPosition.CAPE)
					return true;
				break;
			
			case FAMILIER:
				if(place == ObjectPosition.FAMILIER)
					return true;
				break;
			
			case DOFUS:
				if(place == ObjectPosition.DOFUS1 || place == ObjectPosition.DOFUS2
				|| place == ObjectPosition.DOFUS3 || place == ObjectPosition.DOFUS4
				|| place == ObjectPosition.DOFUS5 || place == ObjectPosition.DOFUS6)
					return true;
				break;
			
			case BOUCLIER:
				if(place == ObjectPosition.BOUCLIER)
					return true;
				break;
			
			//Barre d'objets TODO : Normalement le client bloque les items interdits
			case POTION:
			case PARCHO_EXP:
			case BOOST_FOOD:
			case PAIN:
			case BIERE:
			case POISSON:
			case BONBON:
			case COMESTI_POISSON:
			case VIANDE:
			case VIANDE_CONSERVEE:
			case VIANDE_COMESTIBLE:
			case TEINTURE:
			case MAITRISE:
			case BOISSON:
			case PIERRE_AME_PLEINE:
			case PARCHO_RECHERCHE:
			case CADEAUX:
			case OBJET_ELEVAGE:
			case OBJET_UTILISABLE:
			case PRISME:
			case FEE_ARTIFICE:
			case DONS:
				if(place.getValue() >= 35 && place.getValue() <= 48)
					return true;
				break;			
		}
		return false;
	}
	
	public static void onLevelUpSpells(Player perso,int lvl)
	{
		switch(perso.getClasse().getId())
		{
			case CLASS_FECA:
				if(lvl == 3)
					perso.learnSpell(4, 1, true, false, false);//Renvoie de sort
				if(lvl == 6)
					perso.learnSpell(2, 1, true, false, false);//Aveuglement
				if(lvl == 9)
					perso.learnSpell(1, 1, true, false, false);//Armure Incandescente
				if(lvl == 13)
					perso.learnSpell(9, 1, true, false, false);//Attaque nuageuse
				if(lvl == 17)
					perso.learnSpell(18, 1, true, false, false);//Armure Aqueuse
				if(lvl == 21)
					perso.learnSpell(20, 1, true, false, false);//Immunit?
				if(lvl == 26)
					perso.learnSpell(14, 1, true, false, false);//Armure Venteuse
				if(lvl == 31)
					perso.learnSpell(19, 1, true, false, false);//Bulle
				if(lvl == 36)
					perso.learnSpell(5, 1, true, false, false);//Tr?ve
				if(lvl == 42)
					perso.learnSpell(16, 1, true, false, false);//Science du b?ton
				if(lvl == 48)
					perso.learnSpell(8, 1, true, false, false);//Retour du b?ton
				if(lvl == 54)
					perso.learnSpell(12, 1, true, false, false);//glyphe d'Aveuglement
				if(lvl == 60)
					perso.learnSpell(11, 1, true, false, false);//T?l?portation
				if(lvl == 70)
					perso.learnSpell(10, 1, true, false, false);//Glyphe Enflamm?
				if(lvl == 80)
					perso.learnSpell(7, 1, true, false, false);//Bouclier F?ca
				if(lvl == 90)
					perso.learnSpell(15, 1, true, false, false);//Glyphe d'Immobilisation
				if(lvl == 100)
					perso.learnSpell(13, 1, true, false, false);//Glyphe de Silence
				if(lvl == 200)
					perso.learnSpell(1901, 1, true, false, false);//Invocation de Dopeul F?ca
			break;
			
			case CLASS_OSAMODAS:
				if(lvl == 3)
					perso.learnSpell(26, 1, true, false, false);//B?n?diction Animale
				if(lvl == 6)
					perso.learnSpell(22, 1, true, false, false);//D?placement F?lin
				if(lvl == 9)
					perso.learnSpell(35, 1, true, false, false);//Invocation de Bouftou
				if(lvl == 13)
					perso.learnSpell(28, 1, true, false, false);//Crapaud
				if(lvl == 17)
					perso.learnSpell(37, 1, true, false, false);//Invocation de Prespic
				if(lvl == 21)
					perso.learnSpell(30, 1, true, false, false);//Fouet
				if(lvl == 26)
					perso.learnSpell(27, 1, true, false, false);//Piq?re Motivante
				if(lvl == 31)
					perso.learnSpell(24, 1, true, false, false);//Corbeau
				if(lvl == 36)
					perso.learnSpell(33, 1, true, false, false);//Griffe Cinglante
				if(lvl == 42)
					perso.learnSpell(25, 1, true, false, false);//Soin Animal
				if(lvl == 48)
					perso.learnSpell(38, 1, true, false, false);//Invocation de Sanglier
				if(lvl == 54)
					perso.learnSpell(36, 1, true, false, false);//Frappe du Craqueleur
				if(lvl == 60)
					perso.learnSpell(32, 1, true, false, false);//R?sistance Naturelle
				if(lvl == 70)
					perso.learnSpell(29, 1, true, false, false);//Crocs du Mulou
				if(lvl == 80)
					perso.learnSpell(39, 1, true, false, false);//Invocation de Bwork Mage
				if(lvl == 90)
					perso.learnSpell(40, 1, true, false, false);//Invocation de Craqueleur
				if(lvl == 100)
					perso.learnSpell(31, 1, true, false, false);//Invocation de Dragonnet Rouge
				if(lvl == 200)
					perso.learnSpell(1902, 1, true, false, false);//Invocation de Dopeul Osamodas
			break;

			case CLASS_ENUTROF:
				if(lvl == 3)
					perso.learnSpell(49, 1, true, false, false);//Pelle Fantomatique
				if(lvl == 6)
					perso.learnSpell(42, 1, true, false, false);//Chance
				if(lvl == 9)
					perso.learnSpell(47, 1, true, false, false);//Bo?te de Pandore
				if(lvl == 13)
					perso.learnSpell(48, 1, true, false, false);//Remblai
				if(lvl == 17)
					perso.learnSpell(45, 1, true, false, false);//Cl? R?ductrice
				if(lvl == 21)
					perso.learnSpell(53, 1, true, false, false);//Force de l'Age
				if(lvl == 26)
					perso.learnSpell(46, 1, true, false, false);//D?sinvocation
				if(lvl == 31)
					perso.learnSpell(52, 1, true, false, false);//Cupidit?
				if(lvl == 36)
					perso.learnSpell(44, 1, true, false, false);//Roulage de Pelle
				if(lvl == 42)
					perso.learnSpell(50, 1, true, false, false);//Maladresse
				if(lvl == 48)
					perso.learnSpell(54, 1, true, false, false);//Maladresse de Masse
				if(lvl == 54)
					perso.learnSpell(55, 1, true, false, false);//Acc?l?ration
				if(lvl == 60)
					perso.learnSpell(56, 1, true, false, false);//Pelle du Jugement
				if(lvl == 70)
					perso.learnSpell(58, 1, true, false, false);//Pelle Massacrante
				if(lvl == 80)
					perso.learnSpell(59, 1, true, false, false);//Corruption
				if(lvl == 90)
					perso.learnSpell(57, 1, true, false, false);//Pelle Anim?e
				if(lvl == 100)
					perso.learnSpell(60, 1, true, false, false);//Coffre Anim?
				if(lvl == 200)
					perso.learnSpell(1903, 1, true, false, false);//Invocation de Dopeul Enutrof
			break;

			case CLASS_SRAM:
				if(lvl == 3)
					perso.learnSpell(66, 1, true, false, false);//Poison insidieux
				if(lvl == 6)
					perso.learnSpell(68, 1, true, false, false);//Fourvoiement
				if(lvl == 9)
					perso.learnSpell(63, 1, true, false, false);//Coup Sournois
				if(lvl == 13)
					perso.learnSpell(74, 1, true, false, false);//Double
				if(lvl == 17)
					perso.learnSpell(64, 1, true, false, false);//Rep?rage
				if(lvl == 21)
					perso.learnSpell(79, 1, true, false, false);//Pi?ge de Masse
				if(lvl == 26)
					perso.learnSpell(78, 1, true, false, false);//Invisibilit? d'Autrui
				if(lvl == 31)
					perso.learnSpell(71, 1, true, false, false);//Pi?ge Empoisonn?
				if(lvl == 36)
					perso.learnSpell(62, 1, true, false, false);//Concentration de Chakra
				if(lvl == 42)
					perso.learnSpell(69, 1, true, false, false);//Pi?ge d'Immobilisation
				if(lvl == 48)
					perso.learnSpell(77, 1, true, false, false);//Pi?ge de Silence
				if(lvl == 54)
					perso.learnSpell(73, 1, true, false, false);//Pi?ge r?pulsif
				if(lvl == 60)
					perso.learnSpell(67, 1, true, false, false);//Peur
				if(lvl == 70)
					perso.learnSpell(70, 1, true, false, false);//Arnaque
				if(lvl == 80)
					perso.learnSpell(75, 1, true, false, false);//Pulsion de Chakra
				if(lvl == 90)
					perso.learnSpell(76, 1, true, false, false);//Attaque Mortelle
				if(lvl == 100)
					perso.learnSpell(80, 1, true, false, false);//Pi?ge Mortel
				if(lvl == 200)
					perso.learnSpell(1904, 1, true, false, false);//Invocation de Dopeul Sram
			break;

			case CLASS_XELOR:
				if(lvl == 3)
					perso.learnSpell(84, 1, true, false, false);//Gelure
				if(lvl == 6)
					perso.learnSpell(100, 1, true, false, false);//Sablier de X?lor
				if(lvl == 9)
					perso.learnSpell(92, 1, true, false, false);//Rayon Obscur
				if(lvl == 13)
					perso.learnSpell(88, 1, true, false, false);//T?l?portation
				if(lvl == 17)
					perso.learnSpell(93, 1, true, false, false);//Fl?trissement
				if(lvl == 21)
					perso.learnSpell(85, 1, true, false, false);//Flou
				if(lvl == 26)
					perso.learnSpell(96, 1, true, false, false);//Poussi?re Temporelle
				if(lvl == 31)
					perso.learnSpell(98, 1, true, false, false);//Vol du Temps
				if(lvl == 36)
					perso.learnSpell(86, 1, true, false, false);//Aiguille Chercheuse
				if(lvl == 42)
					perso.learnSpell(89, 1, true, false, false);//D?vouement
				if(lvl == 48)
					perso.learnSpell(90, 1, true, false, false);//Fuite
				if(lvl == 54)
					perso.learnSpell(87, 1, true, false, false);//D?motivation
				if(lvl == 60)
					perso.learnSpell(94, 1, true, false, false);//Protection Aveuglante
				if(lvl == 70)
					perso.learnSpell(99, 1, true, false, false);//Momification
				if(lvl == 80)
					perso.learnSpell(95, 1, true, false, false);//Horloge
				if(lvl == 90)
					perso.learnSpell(91, 1, true, false, false);//Frappe de X?lor
				if(lvl == 100)
					perso.learnSpell(97, 1, true, false, false);//Cadran de X?lor
				if(lvl == 200)
					perso.learnSpell(1905, 1, true, false, false);//Invocation de Dopeul X?lor
			break;

			case CLASS_ECAFLIP:
				if(lvl == 3)
					perso.learnSpell(109, 1, true, false, false);//Bluff
				if(lvl == 6)
					perso.learnSpell(113, 1, true, false, false);//Perception
				if(lvl == 9)
					perso.learnSpell(111, 1, true, false, false);//Contrecoup
				if(lvl == 13)
					perso.learnSpell(104, 1, true, false, false);//Tr?fle
				if(lvl == 17)
					perso.learnSpell(119, 1, true, false, false);//Tout ou rien
				if(lvl == 21)
					perso.learnSpell(101, 1, true, false, false);//Roulette
				if(lvl == 26)
					perso.learnSpell(107, 1, true, false, false);//Topkaj
				if(lvl == 31)
					perso.learnSpell(116, 1, true, false, false);//Langue R?peuse
				if(lvl == 36)
					perso.learnSpell(106, 1, true, false, false);//Roue de la Fortune
				if(lvl == 42)
					perso.learnSpell(117, 1, true, false, false);//Griffe Invocatrice
				if(lvl == 48)
					perso.learnSpell(108, 1, true, false, false);//Esprit F?lin
				if(lvl == 54)
					perso.learnSpell(115, 1, true, false, false);//Odorat
				if(lvl == 60)
					perso.learnSpell(118, 1, true, false, false);//R?flexes
				if(lvl == 70)
					perso.learnSpell(110, 1, true, false, false);//Griffe Joueuse
				if(lvl == 80)
					perso.learnSpell(112, 1, true, false, false);//Griffe de Ceangal
				if(lvl == 90)
					perso.learnSpell(114, 1, true, false, false);//Rekop
				if(lvl == 100)
					perso.learnSpell(120, 1, true, false, false);//Destin d'Ecaflip
				if(lvl == 200)
					perso.learnSpell(1906, 1, true, false, false);//Invocation de Dopeul Ecaflip
			break;

			case CLASS_ENIRIPSA:
				if(lvl == 3)
					perso.learnSpell(124, 1, true, false, false);//Mot Soignant
				if(lvl == 6)
					perso.learnSpell(122, 1, true, false, false);//Mot Blessant
				if(lvl == 9)
					perso.learnSpell(126, 1, true, false, false);//Mot Stimulant
				if(lvl == 13)
					perso.learnSpell(127, 1, true, false, false);//Mot de Pr?vention
				if(lvl == 17)
					perso.learnSpell(123, 1, true, false, false);//Mot Drainant
				if(lvl == 21)
					perso.learnSpell(130, 1, true, false, false);//Mot Revitalisant
				if(lvl == 26)
					perso.learnSpell(131, 1, true, false, false);//Mot de R?g?n?ration
				if(lvl == 31)
					perso.learnSpell(132, 1, true, false, false);//Mot d'Epine
				if(lvl == 36)
					perso.learnSpell(133, 1, true, false, false);//Mot de Jouvence
				if(lvl == 42)
					perso.learnSpell(134, 1, true, false, false);//Mot Vampirique
				if(lvl == 48)
					perso.learnSpell(135, 1, true, false, false);//Mot de Sacrifice
				if(lvl == 54)
					perso.learnSpell(129, 1, true, false, false);//Mot d'Amiti?
				if(lvl == 60)
					perso.learnSpell(136, 1, true, false, false);//Mot d'Immobilisation
				if(lvl == 70)
					perso.learnSpell(137, 1, true, false, false);//Mot d'Envol
				if(lvl == 80)
					perso.learnSpell(138, 1, true, false, false);//Mot de Silence
				if(lvl == 90)
					perso.learnSpell(139, 1, true, false, false);//Mot d'Altruisme
				if(lvl == 100)
					perso.learnSpell(140, 1, true, false, false);//Mot de Reconstitution
				if(lvl == 200)
					perso.learnSpell(1907, 1, true, false, false);//Invocation de Dopeul Eniripsa
			break;

			case CLASS_IOP:
				if(lvl == 3)
					perso.learnSpell(144, 1, true, false, false);//Compulsion
				if(lvl == 6)
					perso.learnSpell(145, 1, true, false, false);//Ep?e Divine
				if(lvl == 9)
					perso.learnSpell(146, 1, true, false, false);//Ep?e du Destin
				if(lvl == 13)
					perso.learnSpell(147, 1, true, false, false);//Guide de Bravoure
				if(lvl == 17)
					perso.learnSpell(148, 1, true, false, false);//Amplification
				if(lvl == 21)
					perso.learnSpell(154, 1, true, false, false);//Ep?e Destructrice
				if(lvl == 26)
					perso.learnSpell(150, 1, true, false, false);//Couper
				if(lvl == 31)
					perso.learnSpell(151, 1, true, false, false);//Souffle
				if(lvl == 36)
					perso.learnSpell(155, 1, true, false, false);//Vitalit?
				if(lvl == 42)
					perso.learnSpell(152, 1, true, false, false);//Ep?e du Jugement
				if(lvl == 48)
					perso.learnSpell(153, 1, true, false, false);//Puissance
				if(lvl == 54)
					perso.learnSpell(149, 1, true, false, false);//Mutilation
				if(lvl == 60)
					perso.learnSpell(156, 1, true, false, false);//Temp?te de Puissance
				if(lvl == 70)
					perso.learnSpell(157, 1, true, false, false);//Ep?e C?leste
				if(lvl == 80)
					perso.learnSpell(158, 1, true, false, false);//Concentration
				if(lvl == 90)
					perso.learnSpell(160, 1, true, false, false);//Ep?e de Iop
				if(lvl == 100)
					perso.learnSpell(159, 1, true, false, false);//Col?re de Iop
				if(lvl == 200)
					perso.learnSpell(1908, 1, true, false, false);//Invocation de Dopeul Iop
			break;

			case CLASS_CRA:
				if(lvl == 3)
					perso.learnSpell(163, 1, true, false, false);//Fl?che Glac?e
				if(lvl == 6)
					perso.learnSpell(165, 1, true, false, false);//Fl?che enflamm?e
				if(lvl == 9)
					perso.learnSpell(172, 1, true, false, false);//Tir Eloign?
				if(lvl == 13)
					perso.learnSpell(167, 1, true, false, false);//Fl?che d'Expiation
				if(lvl == 17)
					perso.learnSpell(168, 1, true, false, false);//Oeil de Taupe
				if(lvl == 21)
					perso.learnSpell(162, 1, true, false, false);//Tir Critique
				if(lvl == 26)
					perso.learnSpell(170, 1, true, false, false);//Fl?che d'Immobilisation
				if(lvl == 31)
					perso.learnSpell(171, 1, true, false, false);//Fl?che Punitive
				if(lvl == 36)
					perso.learnSpell(166, 1, true, false, false);//Tir Puissant
				if(lvl == 42)
					perso.learnSpell(173, 1, true, false, false);//Fl?che Harcelante
				if(lvl == 48)
					perso.learnSpell(174, 1, true, false, false);//Fl?che Cinglante
				if(lvl == 54)
					perso.learnSpell(176, 1, true, false, false);//Fl?che Pers?cutrice
				if(lvl == 60)
					perso.learnSpell(175, 1, true, false, false);//Fl?che Destructrice
				if(lvl == 70)
					perso.learnSpell(178, 1, true, false, false);//Fl?che Absorbante
				if(lvl == 80)
					perso.learnSpell(177, 1, true, false, false);//Fl?che Ralentissante
				if(lvl == 90)
					perso.learnSpell(179, 1, true, false, false);//Fl?che Explosive
				if(lvl == 100)
					perso.learnSpell(180, 1, true, false, false);//Ma?trise de l'Arc
				if(lvl == 200)
					perso.learnSpell(1909, 1, true, false, false);//Invocation de Dopeul Cra
			break;

			case CLASS_SADIDA:
				if(lvl == 3)
					perso.learnSpell(198, 1, true, false, false);//Sacrifice Poupesque
				if(lvl == 6)
					perso.learnSpell(195, 1, true, false, false);//Larme
				if(lvl == 9)
					perso.learnSpell(182, 1, true, false, false);//Invocation de la Folle
				if(lvl == 13)
					perso.learnSpell(192, 1, true, false, false);//Ronce Apaisante
				if(lvl == 17)
					perso.learnSpell(197, 1, true, false, false);//Puissance Sylvestre
				if(lvl == 21)
					perso.learnSpell(189, 1, true, false, false);//Invocation de la Sacrifi?e
				if(lvl == 26)
					perso.learnSpell(181, 1, true, false, false);//Tremblement
				if(lvl == 31)
					perso.learnSpell(199, 1, true, false, false);//Connaissance des Poup?es
				if(lvl == 36)
					perso.learnSpell(191, 1, true, false, false);//Ronce Multiples
				if(lvl == 42)
					perso.learnSpell(186, 1, true, false, false);//Arbre
				if(lvl == 48)
					perso.learnSpell(196, 1, true, false, false);//Vent Empoisonn?
				if(lvl == 54)
					perso.learnSpell(190, 1, true, false, false);//Invocation de la Gonflable
				if(lvl == 60)
					perso.learnSpell(194, 1, true, false, false);//Ronces Agressives
				if(lvl == 70)
					perso.learnSpell(185, 1, true, false, false);//Herbe Folle
				if(lvl == 80)
					perso.learnSpell(184, 1, true, false, false);//Feu de Brousse
				if(lvl == 90)
					perso.learnSpell(188, 1, true, false, false);//Ronce Insolente
				if(lvl == 100)
					perso.learnSpell(187, 1, true, false, false);//Invocation de la Surpuissante
				if(lvl == 200)
					perso.learnSpell(1910, 1, true, false, false);//Invocation de Dopeul Sadida
			break;

			case CLASS_SACRIEUR:
				if(lvl == 3)
					perso.learnSpell(444, 1, true, false, false);//D?robade
				if(lvl == 6)
					perso.learnSpell(449, 1, true, false, false);//D?tour
				if(lvl == 9)
					perso.learnSpell(436, 1, true, false, false);//Assaut
				if(lvl == 13)
					perso.learnSpell(437, 1, true, false, false);//Ch?timent Agile
				if(lvl == 17)
					perso.learnSpell(439, 1, true, false, false);//Dissolution
				if(lvl == 21)
					perso.learnSpell(433, 1, true, false, false);//Ch?timent Os?
				if(lvl == 26)
					perso.learnSpell(443, 1, true, false, false);//Ch?timent Spirituel
				if(lvl == 31)
					perso.learnSpell(440, 1, true, false, false);//Sacrifice
				if(lvl == 36)
					perso.learnSpell(442, 1, true, false, false);//Absorption
				if(lvl == 42)
					perso.learnSpell(441, 1, true, false, false);//Ch?timent Vilatesque
				if(lvl == 48)
					perso.learnSpell(445, 1, true, false, false);//Coop?ration
				if(lvl == 54)
					perso.learnSpell(438, 1, true, false, false);//Transposition
				if(lvl == 60)
					perso.learnSpell(446, 1, true, false, false);//Punition
				if(lvl == 70)
					perso.learnSpell(447, 1, true, false, false);//Furie
				if(lvl == 80)
					perso.learnSpell(448, 1, true, false, false);//Ep?e Volante
				if(lvl == 90)
					perso.learnSpell(435, 1, true, false, false);//Tansfert de Vie
				if(lvl == 100)
					perso.learnSpell(450, 1, true, false, false);//Folie Sanguinaire
				if(lvl == 200)
					perso.learnSpell(1911, 1, true, false, false);//Invocation de Dopeul Sacrieur
			break;

			case CLASS_PANDAWA:
				if(lvl == 3)
					perso.learnSpell(689, 1, true, false, false);//Epouvante
				if(lvl == 6)
					perso.learnSpell(690, 1, true, false, false);//Souffle Alcoolis?
				if(lvl == 9)
					perso.learnSpell(691, 1, true, false, false);//Vuln?rabilit? Aqueuse
				if(lvl == 13)
					perso.learnSpell(688, 1, true, false, false);//Vuln?rabilit? Incandescente
				if(lvl == 17)
					perso.learnSpell(693, 1, true, false, false);//Karcham
				if(lvl == 21)
					perso.learnSpell(694, 1, true, false, false);//Vuln?rabilit? Venteuse
				if(lvl == 26)
					perso.learnSpell(695, 1, true, false, false);//Stabilisation
				if(lvl == 31)
					perso.learnSpell(696, 1, true, false, false);//Chamrak
				if(lvl == 36)
					perso.learnSpell(697, 1, true, false, false);//Vuln?rabilit? Terrestre
				if(lvl == 42)
					perso.learnSpell(698, 1, true, false, false);//Souillure
				if(lvl == 48)
					perso.learnSpell(699, 1, true, false, false);//Lait de Bambou
				if(lvl == 54)
					perso.learnSpell(700, 1, true, false, false);//Vague ? Lame
				if(lvl == 60)
					perso.learnSpell(701, 1, true, false, false);//Col?re de Zato?shwan
				if(lvl == 70)
					perso.learnSpell(702, 1, true, false, false);//Flasque Explosive
				if(lvl == 80)
					perso.learnSpell(703, 1, true, false, false);//Pandatak
				if(lvl == 90)
					perso.learnSpell(704, 1, true, false, false);//Pandanlku
				if(lvl == 100)
					perso.learnSpell(705, 1, true, false, false);//Lien Spiritueux
				if(lvl == 200)
					perso.learnSpell(1912, 1, true, false, false);//Invocation de Dopeul Pandawa
			break;
		}
	}

	public static int getGlyphColor(int spell)
	{
		switch(spell)
		{
			case 10://Enflamm?
			case 2033://Dopeul
				return 4;//Rouge
			case 12://Aveuglement
			case 2034://Dopeul
				return 3;
			case 13://Silence
			case 2035://Dopeul
				return 6;//Bleu
			case 15://Immobilisation
			case 2036://Dopeul
				return 5;//Vert
			case 17://Aggressif
			case 2037://Dopeul
				return 2;
			//case 476://Blop
			default:
				return 4;
		}
	}

	public static int getTrapsColor(int spell)
	{
		switch(spell)
		{
			case 65://Sournois
				return 7;
			case 69://Immobilisation
				return 10;
			case 71://Empoisonn?e
			case 2068://Dopeul
				return 9;
			case 73://Repulsif
				return 12;
			case 77://Silence
			case 2071://Dopeul
				return 11;
			case 79://Masse
			case 2072://Dopeul
				return 8;
			case 80://Mortel
				return 13;
			default:
				return 7;
		}
	}
	
	public static Stats getMountStats(int color,int lvl)
	{
		Stats stats = new Stats();
		switch(color)
		{
			//Amande sauvage
			case 1:break;
			//Ebene
			case 3:
				stats.addOneStat(STATS_ADD_VITA, lvl/2);
				stats.addOneStat(STATS_ADD_AGIL, (int)(lvl/1.25));//100/1.25 = 80
			break;
			//Rousse |
			case 10:
			stats.addOneStat(STATS_ADD_VITA, lvl); //100/1 = 100
			break;
			//Amande
			case 20:
			stats.addOneStat(STATS_ADD_INIT, lvl*10); // 100*10 = 1000
			break;
			//Dor?e
			case 18:
			stats.addOneStat(STATS_ADD_VITA, (lvl/2)); 
			stats.addOneStat(STATS_ADD_SAGE, (int)(lvl/2.50)); // 100/2.50 = 40
			break;
			//Rousse-Amande
			case 38:
			stats.addOneStat(STATS_ADD_INIT, lvl*5); // 100*5 = 500
			stats.addOneStat(STATS_ADD_VITA, lvl); 
			stats.addOneStat(STATS_CREATURE, (lvl/50)); // 100/50 = 2
			break;
			//Rousse-Dor?e
			case 46:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_SAGE, (lvl/4)); //100/4 = 25
		    break;
			//Amande-Dor?e
			case 33:
			stats.addOneStat(STATS_ADD_INIT, lvl*5);
			stats.addOneStat(STATS_ADD_SAGE, (lvl/4));
			stats.addOneStat(STATS_ADD_VITA, (lvl/2));
			stats.addOneStat(STATS_CREATURE, (lvl/100)); // 100/100 = 1
			break;
			//Indigo |
			case 17:
			stats.addOneStat(STATS_ADD_CHAN, (int)(lvl/1.25));
			stats.addOneStat(STATS_ADD_VITA, (lvl/2));
			break;
			//Rousse-Indigo
			case 62:
			stats.addOneStat(STATS_ADD_VITA,(int)(lvl*1.50)); // 100*1.50 = 150
			stats.addOneStat(STATS_ADD_CHAN, (int)(lvl/1.65));
			break;
			//Rousse-Eb?ne
			case 12:
			stats.addOneStat(STATS_ADD_VITA,(int)(lvl*1.50));
			stats.addOneStat(STATS_ADD_AGIL, (int)(lvl/1.65));
			break;
			//Amande-Indigo
			case 36:
			stats.addOneStat(STATS_ADD_INIT, lvl*5);
			stats.addOneStat(STATS_ADD_VITA, (lvl/2)); 
			stats.addOneStat(STATS_ADD_CHAN, (int)(lvl/1.65));
			stats.addOneStat(STATS_CREATURE, (lvl/100));
			break;
			//Pourpre | Stade 4
			case 19:
			stats.addOneStat(STATS_ADD_FORC, (int)(lvl/1.25));
			stats.addOneStat(STATS_ADD_VITA, (lvl/2));
			break;
			//Orchid?e
			case 22:
			stats.addOneStat(STATS_ADD_INTE, (int)(lvl/1.25));
			stats.addOneStat(STATS_ADD_VITA, (lvl/2));
			break;
			//Dor?e-Orchid?e |
			case 48:
			stats.addOneStat(STATS_ADD_VITA, (lvl));
			stats.addOneStat(STATS_ADD_SAGE, (lvl/4));
		    stats.addOneStat(STATS_ADD_INTE, (int)(lvl/1.65));
			break;
			//Indigo-Pourpre
			case 65:
			stats.addOneStat(STATS_ADD_VITA, (lvl));
			stats.addOneStat(STATS_ADD_CHAN, (lvl/2));
			stats.addOneStat(STATS_ADD_FORC, (lvl/2));
			break;
			//Indigo-Orchid?e
			case 67:
			stats.addOneStat(STATS_ADD_VITA, (lvl));
			stats.addOneStat(STATS_ADD_CHAN, (lvl/2));
			stats.addOneStat(STATS_ADD_INTE, (lvl/2));
			break;
			//Eb?ne-Pourpre
			case 54:
			stats.addOneStat(STATS_ADD_VITA, (lvl));
			stats.addOneStat(STATS_ADD_FORC, (lvl/2));
			stats.addOneStat(STATS_ADD_AGIL, (lvl/2));
			break;
			//Eb?ne-Orchid?e
			case 53:
			stats.addOneStat(STATS_ADD_VITA, (lvl));
			stats.addOneStat(STATS_ADD_AGIL, (lvl/2));
			stats.addOneStat(STATS_ADD_INTE, (lvl/2));
			break;
			//Pourpre-Orchid?e
			case 76:
			stats.addOneStat(STATS_ADD_VITA, (lvl));
			stats.addOneStat(STATS_ADD_INTE, (lvl/2));
			stats.addOneStat(STATS_ADD_FORC, (lvl/2));
			break;
			// Amande-Ebene	| Nami-start
			case 37:
			stats.addOneStat(STATS_ADD_INIT, lvl*5);
			stats.addOneStat(STATS_ADD_VITA, (lvl/2)); 
			stats.addOneStat(STATS_ADD_AGIL, (int)(lvl/1.65));
			stats.addOneStat(STATS_CREATURE, (lvl/100));
			break;
			// Amande-Rousse
			case 44:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_SAGE, (lvl/4));
			stats.addOneStat(STATS_ADD_CHAN, (int)(lvl/1.65));
			break;
			// Dor?e-Eb?ne
			case 42:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_SAGE, (lvl/4));
			stats.addOneStat(STATS_ADD_AGIL, (int)(lvl/1.65));
			break;
			// Indigo-Eb?ne
			case 51:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_CHAN, (lvl/2));
			stats.addOneStat(STATS_ADD_AGIL, (lvl/2));
			break;
			// Rousse-Pourpre
			case 71:
			stats.addOneStat(STATS_ADD_VITA, (int)(lvl*1.5));
			stats.addOneStat(STATS_ADD_FORC, (int)(lvl/1.65));
			break;
			// Rousse-Orchid?e
			case 70:
			stats.addOneStat(STATS_ADD_VITA, (int)(lvl*1.5));
			stats.addOneStat(STATS_ADD_INTE, (int)(lvl/1.65));
			break;
			// Amande-Pourpre
			case 41:
			stats.addOneStat(STATS_ADD_INIT, lvl*5);
			stats.addOneStat(STATS_ADD_VITA, (lvl/2)); 
			stats.addOneStat(STATS_ADD_FORC, (int)(lvl/1.65));
			stats.addOneStat(STATS_CREATURE, (lvl/100));
			break;
			// Amande-Orchid?e
			case 40:
			stats.addOneStat(STATS_ADD_INIT, lvl*5);
			stats.addOneStat(STATS_ADD_VITA, (lvl/2)); 
			stats.addOneStat(STATS_ADD_INTE, (int)(lvl/1.65));
			stats.addOneStat(STATS_CREATURE, (lvl/100));
			break;
			// Dor?e-Pourpre
			case 49:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_SAGE, (lvl/4));
			stats.addOneStat(STATS_ADD_FORC, (int)(lvl/1.65));
			break;
			// Ivoire
			case 16:
			stats.addOneStat(STATS_ADD_VITA, (lvl/2));
			stats.addOneStat(STATS_ADD_PERDOM, (lvl/2));
			break;
	        // Turquoise
			case 15:
			stats.addOneStat(STATS_ADD_VITA, (lvl/2));
			stats.addOneStat(STATS_ADD_PROS, (int)(lvl/1.25));
			break;
			//Rousse-Ivoire
			case 11:
			stats.addOneStat(STATS_ADD_VITA, (lvl*2)); // 100*2 = 200
			stats.addOneStat(STATS_ADD_PERDOM, (int)(lvl/2.5)); // = 40
			break;
			//Rousse-Turquoise
			case 69:
			stats.addOneStat(STATS_ADD_VITA, (lvl*2));
			stats.addOneStat(STATS_ADD_PROS, (int)(lvl/2.50));
			break;
			//Amande-Turquoise
			case 39:
			stats.addOneStat(STATS_ADD_INIT, lvl*5);
			stats.addOneStat(STATS_ADD_VITA, (lvl/2));
			stats.addOneStat(STATS_ADD_PROS, (int)(lvl/2.50));
			stats.addOneStat(STATS_CREATURE, (lvl/100));
			break;
			//Dor?e-Ivoire
			case 45:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_PERDOM, (int)(lvl/2.5));
			stats.addOneStat(STATS_ADD_SAGE, (lvl/4));
			break;
			//Dor?e-Turquoise
			case 47:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_PROS, (int)(lvl/2.50));
			stats.addOneStat(STATS_ADD_SAGE, (lvl/4));
			break;
			//Indigo-Ivoire
			case 61:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_CHAN, (int)(lvl/2.50));
			stats.addOneStat(STATS_ADD_PERDOM, (int)(lvl/2.5));
			break;
			//Indigo-Turquoise
			case 63:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_CHAN, (int)(lvl/1.65));
			stats.addOneStat(STATS_ADD_PERDOM, (int)(lvl/2.5));
			break;
			//Eb?ne-Ivoire
			case 9:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_AGIL, (int)(lvl/2.50));
			stats.addOneStat(STATS_ADD_PERDOM, (int)(lvl/2.5));
			break;
			//Eb?ne-Turquoise
			case 52:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_AGIL, (int)(lvl/1.65));
			stats.addOneStat(STATS_ADD_PROS, (int)(lvl/2.50));
			break;
			//Pourpre-Ivoire
			case 68:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_FORC, (int)(lvl/1.65));
			stats.addOneStat(STATS_ADD_PERDOM, (int)(lvl/2.5));
			break;
			//Pourpre-Turquoise
			case 73:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_FORC, (int)(lvl/1.65));
			stats.addOneStat(STATS_ADD_PROS, (int)(lvl/2.50));
			break;
			//Orchid?e-Turquoise
			case 72:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_INTE, (int)(lvl/1.65));
			stats.addOneStat(STATS_ADD_PERDOM, (int)(lvl/2.5));
			break;
			//Ivoire-Turquoise
			case 66:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_PERDOM, (int)(lvl/2.5));
			stats.addOneStat(STATS_ADD_PROS, (int)(lvl/2.50));
			break;
			// Emeraude
			case 21:
			stats.addOneStat(STATS_ADD_VITA, lvl*2);
			stats.addOneStat(STATS_ADD_PM, (lvl/100));
			break;
			// Prune
			case 23:
			stats.addOneStat(STATS_ADD_VITA, lvl*2); // 100*2 = 200
			stats.addOneStat(STATS_ADD_PO, (lvl/50));
			break;
			//Emeraude-Rousse
			case 57:
			stats.addOneStat(STATS_ADD_VITA, lvl*3); // 100*3 = 300
			stats.addOneStat(STATS_ADD_PM, (lvl/100));
			break;
			//Rousse-Prune
			case 84:
			stats.addOneStat(STATS_ADD_VITA, lvl*3);
			stats.addOneStat(STATS_ADD_PO, (lvl/100));
			break;
			//Amande-Emeraude
			case 35:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_PM, (lvl/100));
			stats.addOneStat(STATS_CREATURE, (lvl/100));
			stats.addOneStat(STATS_ADD_INIT, lvl*5);
			break;
			//Amande-Prune
			case 77:
			stats.addOneStat(STATS_ADD_VITA, lvl*2);
			stats.addOneStat(STATS_ADD_INIT, lvl*5);
			stats.addOneStat(STATS_ADD_PO, (lvl/100));
			stats.addOneStat(STATS_CREATURE, (lvl/100));
			break;
			//Dor?e-Emeraude
			case 43:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_SAGE, (lvl/4));
			stats.addOneStat(STATS_ADD_PM, (lvl/100));
			break;
			//Dor?e-Prune
			case 78:
			stats.addOneStat(STATS_ADD_VITA, lvl*2);
			stats.addOneStat(STATS_ADD_SAGE, (lvl/4));
			stats.addOneStat(STATS_ADD_PO, (lvl/100));
			break;
			//Indigo-Emeraude
			case 55:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_CHAN, (int)(lvl/3.33));
			stats.addOneStat(STATS_ADD_PM, (lvl/100));
			break;
			//Indigo-Prune
			case 82:
			stats.addOneStat(STATS_ADD_VITA, lvl*2);
			stats.addOneStat(STATS_ADD_CHAN, (int)(lvl/1.65));
			stats.addOneStat(STATS_ADD_PO, (lvl/100));
			break;
			//Eb?ne-Emeraude
			case 50:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_AGIL, (int)(lvl/3.33));
			stats.addOneStat(STATS_ADD_PM, (lvl/100));
			break;
			//Eb?ne-Prune
			case 79:
			stats.addOneStat(STATS_ADD_VITA, lvl*2);
			stats.addOneStat(STATS_ADD_AGIL, (int)(lvl/1.65));
			stats.addOneStat(STATS_ADD_PO, (lvl/100));
			break;
			//Pourpre-Emeraude
			case 60:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_FORC, (int)(lvl/3.33));
			stats.addOneStat(STATS_ADD_PM, (lvl/100));
			break;
			//Pourpre-Prune
			case 87:
			stats.addOneStat(STATS_ADD_VITA, lvl*2);
			stats.addOneStat(STATS_ADD_FORC, (int)(lvl/1.65));
			stats.addOneStat(STATS_ADD_PO, (lvl/100));
			break;
			//Orchid?e-Emeraude
			case 59:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_INTE, (int)(lvl/3.33));
			stats.addOneStat(STATS_ADD_PM, (lvl/100));
			break;
			//Orchid?e-Prune
			case 86:
			stats.addOneStat(STATS_ADD_VITA, lvl*2);
			stats.addOneStat(STATS_ADD_INTE, (int)(lvl/1.65));
			stats.addOneStat(STATS_ADD_PO, (lvl/100));
			break;
			//Ivoire-Emeraude
			case 56:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_PERDOM, (int)(lvl/3.33));
			stats.addOneStat(STATS_ADD_PM, (lvl/100));
			break;
			//Ivoire-Prune
			case 83:
			stats.addOneStat(STATS_ADD_VITA, lvl*2);
			stats.addOneStat(STATS_ADD_PERDOM, (int)(lvl/1.65));
			stats.addOneStat(STATS_ADD_PO, (lvl/100));
			break;
			//Turquoise-Emeraude
			case 58:
			stats.addOneStat(STATS_ADD_VITA, lvl);
			stats.addOneStat(STATS_ADD_PROS, (int)(lvl/3.33));
			stats.addOneStat(STATS_ADD_PM, (lvl/100));
			break;
			//Turquoise-Prune
			case 85:
			stats.addOneStat(STATS_ADD_VITA, lvl*2);
			stats.addOneStat(STATS_ADD_PROS, (int)(lvl/1.65));
			stats.addOneStat(STATS_ADD_PO, (lvl/100));
			break;
			//Emeraude-Prune
			case 80:
		    stats.addOneStat(STATS_ADD_VITA, lvl*2);
			stats.addOneStat(STATS_ADD_PM, (lvl/100));
			stats.addOneStat(STATS_ADD_PO, (lvl/100));
			break;
			//Armure
			case 88:
			stats.addOneStat(STATS_ADD_PERDOM, (lvl/2));
			stats.addOneStat(STATS_ADD_RP_AIR, (lvl/20));
			stats.addOneStat(STATS_ADD_RP_EAU, (lvl/20));
			stats.addOneStat(STATS_ADD_RP_TER, (lvl/20));
			stats.addOneStat(STATS_ADD_RP_FEU, (lvl/20));
			stats.addOneStat(STATS_ADD_RP_NEU, (lvl/20));
			break;
		}
		return stats;
	}
	
	public static ObjectTemplate getParchoTemplateByMountColor(int color)
	{
		switch(color)
		{
			//Ammande sauvage
			case 2: return World.data.getObjectTemplate(7807);
			//Ebene | Page 1
			case 3: return World.data.getObjectTemplate(7808);
			//Rousse sauvage
			case 4: return World.data.getObjectTemplate(7809);
			//Ebene-ivoire
			case 9: return World.data.getObjectTemplate(7810);
			//Rousse
			case 10: return World.data.getObjectTemplate(7811);
			//Ivoire-Rousse
			case 11: return World.data.getObjectTemplate(7812);
			//Ebene-rousse
			case 12: return World.data.getObjectTemplate(7813);
			//Turquoise
			case 15: return World.data.getObjectTemplate(7814);
			//Ivoire
			case 16: return World.data.getObjectTemplate(7815);
			//Indigo
			case 17: return World.data.getObjectTemplate(7816);
			//Dor?e
			case 18: return World.data.getObjectTemplate(7817);
			//Pourpre
			case 19: return World.data.getObjectTemplate(7818);
			//Amande
			case 20: return World.data.getObjectTemplate(7819);
			//Emeraude
			case 21: return World.data.getObjectTemplate(7820);
			//Orchid?e
			case 22: return World.data.getObjectTemplate(7821);
			//Prune
			case 23: return World.data.getObjectTemplate(7822);
			//Amande-Dor?e
			case 33: return World.data.getObjectTemplate(7823);
			//Amande-Ebene
			case 34: return World.data.getObjectTemplate(7824);
			//Amande-Emeraude
			case 35: return World.data.getObjectTemplate(7825);
			//Amande-Indigo
			case 36: return World.data.getObjectTemplate(7826);
			//Amande-Ivoire
			case 37: return World.data.getObjectTemplate(7827);
			//Amande-Rousse
			case 38: return World.data.getObjectTemplate(7828);
			//Amande-Turquoise
			case 39: return World.data.getObjectTemplate(7829);
			//Amande-Orchid?e
			case 40: return World.data.getObjectTemplate(7830);
			//Amande-Pourpre
			case 41: return World.data.getObjectTemplate(7831);
			//Dor?e-Eb?ne
			case 42: return World.data.getObjectTemplate(7832);
			//Dor?e-Emeraude
			case 43: return World.data.getObjectTemplate(7833);
			//Dor?e-Indigo
			case 44: return World.data.getObjectTemplate(7834);
			//Dor?e-Ivoire
			case 45: return World.data.getObjectTemplate(7835);
			//Dor?e-Rousse | Page 2
			case 46: return World.data.getObjectTemplate(7836);
			//Dor?e-Turquoise
			case 47: return World.data.getObjectTemplate(7837);
			//Dor?e-Orchid?e
			case 48: return World.data.getObjectTemplate(7838);
			//Dor?e-Pourpre
			case 49: return World.data.getObjectTemplate(7839);
			//Eb?ne-Emeraude
			case 50: return World.data.getObjectTemplate(7840);
			//Eb?ne-Indigo
			case 51: return World.data.getObjectTemplate(7841);
			//Eb?ne-Turquoise
			case 52: return World.data.getObjectTemplate(7842);
			//Eb?ne-Orchid?e
			case 53: return World.data.getObjectTemplate(7843);
			//Eb?ne-Pourpre
			case 54: return World.data.getObjectTemplate(7844);
			//Emeraude-Indigo
			case 55: return World.data.getObjectTemplate(7845);
			//Emeraude-Ivoire
			case 56: return World.data.getObjectTemplate(7846);
			//Emeraude-Rousse
			case 57: return World.data.getObjectTemplate(7847);
			//Emeraude-Turquoise
			case 58: return World.data.getObjectTemplate(7848);
			//Emeraude-Orchid?e
			case 59: return World.data.getObjectTemplate(7849);
			//Emeraude-Pourpre
			case 60: return World.data.getObjectTemplate(7850);
			//Indigo-Ivoire
			case 61: return World.data.getObjectTemplate(7851);
			//Indigo-Rousse
			case 62: return World.data.getObjectTemplate(7852);
			//Indigo-Turquoise
			case 63: return World.data.getObjectTemplate(7853);
			//Indigo-Orchid?e
			case 64: return World.data.getObjectTemplate(7854);
			//Indigo-Pourpre
			case 65: return World.data.getObjectTemplate(7855);
			//Ivoire-Turquoise
			case 66: return World.data.getObjectTemplate(7856);
			//Ivoire-Ochid?e
			case 67: return World.data.getObjectTemplate(7857);
			//Ivoire-Pourpre
			case 68: return World.data.getObjectTemplate(7858);
			//Turquoise-Rousse
			case 69: return World.data.getObjectTemplate(7859);
			//Ochid?e-Rousse
			case 70: return World.data.getObjectTemplate(7860);
			//Pourpre-Rousse
			case 71: return World.data.getObjectTemplate(7861);
			//Turquoise-Orchid?e
			case 72: return World.data.getObjectTemplate(7862);
			//Turquoise-Pourpre
			case 73: return World.data.getObjectTemplate(7863);
			//Dor?e sauvage
			case 74: return World.data.getObjectTemplate(7864);
			//Squelette
			case 75: return World.data.getObjectTemplate(7865);
			//Orchid?e-Pourpre
			case 76: return World.data.getObjectTemplate(7866);
			//Prune-Amande
			case 77: return World.data.getObjectTemplate(7867);
			//Prune-Dor?e
			case 78: return World.data.getObjectTemplate(7868);
			//Prune-Eb?ne
			case 79: return World.data.getObjectTemplate(7869);
			//Prune-Emeraude
			case 80: return World.data.getObjectTemplate(7870);
			//Prune et Indigo
			case 82: return World.data.getObjectTemplate(7871);
			//Prune-Ivoire
			case 83: return World.data.getObjectTemplate(7872);
			//Prune-Rousse
			case 84: return World.data.getObjectTemplate(7873);
			//Prune-Turquoise
			case 85: return World.data.getObjectTemplate(7874);
			//Prune-Orchid?e
			case 86: return World.data.getObjectTemplate(7875);
			//Prune-Pourpre
			case 87: return World.data.getObjectTemplate(7876);
			//Armure
			case 88: return World.data.getObjectTemplate(9582);		
		}
		return null;
	}
	
	
	public static int getMountColorByParchoTemplate(int tID)
	{
		for(int a = 1;a<100;a++)if(getParchoTemplateByMountColor(a)!=null)if(getParchoTemplateByMountColor(a).getId() == tID)return a; 
		return -1;
	}
	
	public static int getNearCellidUnused(Player _perso)
	{
		int cellFront = 0;
		int cellBack = 0;
		int cellRight = 0;
		int cellLeft = 0;
		if(_perso.getMap().getSubArea().getArea().getId() == 7 || _perso.getMap().getSubArea().getArea().getId() == 11)
		{
			cellFront = 19;
			cellBack = -19;
			cellRight = 18;
			cellLeft = -18;
		}else
		{
			cellFront = 15;
			cellBack = -15;
			cellRight = 14;
			cellLeft = -14;
		}
		if(_perso.getMap().getCases().get(_perso.getCell().getId()+cellFront).getObject() == null
				&& _perso.getMap().getCases().get(_perso.getCell().getId()+cellFront).getPlayers().isEmpty()
				&& _perso.getMap().getCases().get(_perso.getCell().getId()+cellFront).isWalkable(false))
		{
			return cellFront;
		}else
		if(_perso.getMap().getCases().get(_perso.getCell().getId()-cellBack).getObject() == null
				&& _perso.getMap().getCases().get(_perso.getCell().getId()-cellBack).getPlayers().isEmpty()
				&& _perso.getMap().getCases().get(_perso.getCell().getId()-cellBack).isWalkable(false))
		{
			return cellBack;
		}else
		if(_perso.getMap().getCases().get(_perso.getCell().getId()+cellRight).getObject() == null
				&& _perso.getMap().getCases().get(_perso.getCell().getId()+cellRight).getPlayers().isEmpty()
				&& _perso.getMap().getCases().get(_perso.getCell().getId()+cellRight).isWalkable(false))
		{
			return cellRight;
		}else
		if(_perso.getMap().getCases().get(_perso.getCell().getId()-cellLeft).getObject() == null
				&& _perso.getMap().getCases().get(_perso.getCell().getId()-cellLeft).getPlayers().isEmpty()
				&& _perso.getMap().getCases().get(_perso.getCell().getId()-cellLeft).isWalkable(false))
		{
			return cellLeft;
		}
		
		return -1;
	}
	
	
	
	public static int getProtectorLevelByAttacker(Fighter fighter) {
		int level = fighter.getLvl();
		int value;
		
		if(level <= 50)
			value = 50;
		else if (level <= 80)
			value = 80;
		else if (level <= 110)
			value = 110;
		else if (level <= 140)
			value = 140;
		else
			value = 170;
		return value;
	}
}