package game.packet.game;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import objects.Fight;
import objects.Fighter;
import objects.House;
import objects.Percepteur;
import objects.Carte.Case;
import objects.Sort.SortStats;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;

import common.Constants;
import common.CryptManager;
import common.Pathfinding;
import common.SocketManager;

import core.Log;
import core.World;
import game.GameAction;
import game.GameClient;

@Packet("GA")
public class SendActions implements PacketParser {

	public static ReentrantLock locker = new ReentrantLock();
	
	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() != null)
			parseGameActionPacket(client, packet);
	}
	
	public static void parseGameActionPacket(GameClient client, String packet)
	{
		int action, next = 0;
		
		try	{
			action = Integer.parseInt(packet.substring(2,5));
		} catch(NumberFormatException e) {return;}	
		
		if(client.getActions().size() > 0)
			next = (Integer)(client.getActions().keySet().toArray()[client.getActions().size()-1])+1;
		
		GameAction GA = new GameAction(next, action, packet);
		
		switch(action)
		{
			case 1://Deplacement
				game_parseDeplacementPacket(client, GA);
			break;
			
			case 300://Sort
				game_tryCastSpell(client, packet);
			break;
			
			case 303://Attaque CaC
				game_tryCac(client, packet);
			break;
			
			case 500://Action Sur Map
				game_action(client, GA);
			break;
			
			case 507://Panneau int�rieur de la maison
				house_action(client, packet);
			break;
			
			case 618://Mariage oui
				client.getPlayer().setisOK(Integer.parseInt(packet.substring(5,6)));
				SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(client.getPlayer().get_curCarte(), "", client.getPlayer().get_GUID(), client.getPlayer().get_name(), "Oui");
				if(World.data.getMarried(0).getisOK() > 0 && World.data.getMarried(1).getisOK() > 0)
					World.data.Wedding(World.data.getMarried(0), World.data.getMarried(1), 1);
				if(World.data.getMarried(0) != null && World.data.getMarried(1) != null)
					World.data.PriestRequest((World.data.getMarried(0)==client.getPlayer()?World.data.getMarried(1):World.data.getMarried(0)), (World.data.getMarried(0)==client.getPlayer()?World.data.getMarried(1).get_curCarte():World.data.getMarried(0).get_curCarte()), client.getPlayer().get_isTalkingWith());
			break;
			case 619://Mariage non
				client.getPlayer().setisOK(0);
				SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(client.getPlayer().get_curCarte(), "", client.getPlayer().get_GUID(), client.getPlayer().get_name(), "Non");
				World.data.Wedding(World.data.getMarried(0), World.data.getMarried(1), 0);
			break;
			
			case 900://Demande Defie
				game_ask_duel(client, packet);
			break;
			case 901://Accepter Defie
				game_accept_duel(client, packet);
			break;
			case 902://Refus/Anuler Defie
				game_cancel_duel(client, packet);
			break;
			case 903://Rejoindre combat
				game_join_fight(client, packet);
			break;
			case 906://Agresser
				game_aggro(client, packet);
			break;
			case 909://Perco
				game_perco(client, packet);
			break;
		}	
	}
	
		public static void house_action(GameClient client, String packet) {
			int action = Integer.parseInt(packet.substring(5));
			House house = client.getPlayer().getInHouse();
			if(house == null) 
				return;
			switch(action)
			{
				case 81://V�rouiller maison
					house.Lock(client.getPlayer());
				break;
				case 97://Acheter maison
					house.BuyIt(client.getPlayer());
				break;
				case 98://Vendre
				case 108://Modifier prix de vente
					house.SellIt(client.getPlayer());
				break;
			}
		}
		
		public static void game_perco(GameClient client, String packet)
		{
			try
			{
				if(client.getPlayer() == null)return;
				if(client.getPlayer().get_fight() != null)return;
				if(client.getPlayer().get_isTalkingWith() != 0 ||
				   client.getPlayer().get_isTradingWith() != 0 ||
				   client.getPlayer().getCurJobAction() != null ||
				   client.getPlayer().get_curExchange() != null ||
				   client.getPlayer().is_away())
						{
							return;
						}
				int id = Integer.parseInt(packet.substring(5));
				Percepteur target = World.data.getPerco(id);
				if(target == null || target.get_inFight() > 0) return;
				if(target.get_Exchange())
				{
					
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1180");
					return;
				}
				SocketManager.GAME_SEND_GA_PACKET_TO_MAP(client.getPlayer().get_curCarte(),"", 909, client.getPlayer().get_GUID()+"", id+"");
				client.getPlayer().get_curCarte().startFigthVersusPercepteur(client.getPlayer(), target);
			}catch(Exception e){};
		}
		
		public static void game_aggro(GameClient client, String packet) {
			locker.lock();
			try {
				if(client.getPlayer() == null)return;
				if(client.getPlayer().get_fight() != null)return;
				int id = Integer.parseInt(packet.substring(5));
				Player target = World.data.getPersonnage(id);
				if(target == null || !target.isOnline() || target.get_fight() != null
					|| target.get_curCarte().get_id() != client.getPlayer().get_curCarte().get_id()
					|| target.get_align() == client.getPlayer().get_align()
					|| client.getPlayer().get_curCarte().get_placesStr().equalsIgnoreCase("|")
					|| !target.canAggro())
					return;
				
				client.getPlayer().toggleWings('+');
				SocketManager.GAME_SEND_GA_PACKET_TO_MAP(client.getPlayer().get_curCarte(),"", 906, client.getPlayer().get_GUID()+"", id+"");
				
				if(target.get_align() == 0) {
					client.getPlayer().setDeshonor(client.getPlayer().getDeshonor()+1);
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "084;1");
					client.getPlayer().get_curCarte().newFight(client.getPlayer(), target, Constants.FIGHT_TYPE_AGRESSION, true);
				} else 
					client.getPlayer().get_curCarte().newFight(client.getPlayer(), target, Constants.FIGHT_TYPE_AGRESSION, false);
				
			}catch(Exception e){
				
			} finally {
				locker.unlock();
			}
		}
	
		public static void game_action(GameClient client, GameAction GA)
		{
			String packet = GA.getPacket().substring(5);
			int cell = -1, action = -1;
			
			try {
				cell = Integer.parseInt(packet.split(";")[0]);
				action = Integer.parseInt(packet.split(";")[1]);
			} catch(Exception e) {}
			//Si packet invalide, ou cellule introuvable
			if(cell == -1 || action == -1 || client.getPlayer() == null || client.getPlayer().get_curCarte() == null ||
					client.getPlayer().get_curCarte().getCase(cell) == null)
				return;
			GA.setArgs(cell+";"+action);
			client.getPlayer().get_compte().getGameClient().addAction(GA);
		}
	
		public static void game_tryCac(GameClient client, String packet)
		{
			try
			{
				if(client.getPlayer().get_fight() ==null)return;
				int cellID = -1;
				try
				{
					cellID = Integer.parseInt(packet.substring(5));
				}catch(Exception e){return;};
				
				client.getPlayer().get_fight().tryCaC(client.getPlayer(),cellID);
			}catch(Exception e){};
		}
	
		public static void game_tryCastSpell(GameClient client, String packet)
		{
			try
			{
				String[] splt = packet.split(";");
				int spellID = Integer.parseInt(splt[0].substring(5));
				int caseID = Integer.parseInt(splt[1]);
				if(client.getPlayer().get_fight() != null)
				{
					SortStats SS = client.getPlayer().getSortStatBySortIfHas(spellID);
					if(SS == null)return;
					client.getPlayer().get_fight().tryCastSpell(client.getPlayer().get_fight().getFighterByPerso(client.getPlayer()),SS,caseID);
				}
			}catch(NumberFormatException e){return;};
		}
	
		public static void game_join_fight(GameClient client, String packet)
		{
			String[] infos = packet.substring(5).split(";");
			if(infos.length == 1)
			{
				try
				{
					Fight F = client.getPlayer().get_curCarte().getFight(Integer.parseInt(infos[0]));
					F.joinAsSpect(client.getPlayer());
				}catch(Exception e){return;};
			}else
			{
				try
				{
					int guid = Integer.parseInt(infos[1]);
					if(client.getPlayer().is_away())
					{
						SocketManager.GAME_SEND_GA903_ERROR_PACKET(client,'o',guid);
						return;
					}
					if(World.data.getPersonnage(guid) == null)return;
					World.data.getPersonnage(guid).get_fight().joinFight(client.getPlayer(),guid);
				}catch(Exception e){return;};
			}
		}
	
		public static void game_accept_duel(GameClient client, String packet)
		{
			int guid = -1;
			try{guid = Integer.parseInt(packet.substring(5));}catch(NumberFormatException e){return;};
			if(client.getPlayer().get_duelID() != guid || client.getPlayer().get_duelID() == -1)return;
			SocketManager.GAME_SEND_MAP_START_DUEL_TO_MAP(client.getPlayer().get_curCarte(),client.getPlayer().get_duelID(),client.getPlayer().get_GUID());
			Fight fight = client.getPlayer().get_curCarte().newFight(World.data.getPersonnage(client.getPlayer().get_duelID()),client.getPlayer(),Constants.FIGHT_TYPE_CHALLENGE, false);
			client.getPlayer().set_fight(fight);
			World.data.getPersonnage(client.getPlayer().get_duelID()).set_fight(fight);
		}
	
		public static void game_cancel_duel(GameClient client, String packet)
		{
			try
			{
				if(client.getPlayer().get_duelID() == -1)return;
				SocketManager.GAME_SEND_CANCEL_DUEL_TO_MAP(client.getPlayer().get_curCarte(),client.getPlayer().get_duelID(),client.getPlayer().get_GUID());
				World.data.getPersonnage(client.getPlayer().get_duelID()).set_away(false);
				World.data.getPersonnage(client.getPlayer().get_duelID()).set_duelID(-1);
				client.getPlayer().set_away(false);
				client.getPlayer().set_duelID(-1);	
			}catch(NumberFormatException e){return;};
		}
	
		public static void game_ask_duel(GameClient client, String packet)
		{
			if(client.getPlayer().get_curCarte().get_placesStr().equalsIgnoreCase("|"))
			{
				SocketManager.GAME_SEND_DUEL_Y_AWAY(client, client.getPlayer().get_GUID());
				return;
			}
			try
			{
				int guid = Integer.parseInt(packet.substring(5));
				if(client.getPlayer().is_away() || client.getPlayer().get_fight() != null) {
					SocketManager.GAME_SEND_DUEL_Y_AWAY(client, client.getPlayer().get_GUID());
					return;
				}
				
				Player Target = World.data.getPersonnage(guid);
				
				if(Target == null) 
					return;
				if(Target.is_away() || Target.get_fight() != null || Target.get_curCarte().get_id() != client.getPlayer().get_curCarte().get_id()) {
					SocketManager.GAME_SEND_DUEL_E_AWAY(client, client.getPlayer().get_GUID());
					return;
				}
				
				client.getPlayer().set_duelID(guid);
				client.getPlayer().set_away(true);
				World.data.getPersonnage(guid).set_duelID(client.getPlayer().get_GUID());
				World.data.getPersonnage(guid).set_away(true);
				SocketManager.GAME_SEND_MAP_NEW_DUEL_TO_MAP(client.getPlayer().get_curCarte(),client.getPlayer().get_GUID(),guid);
			}catch(NumberFormatException e){return;}
		}
	
		public static void game_parseDeplacementPacket(GameClient client, GameAction GA)
		{
			String path = GA.getPacket().substring(5);
			if(client.getPlayer().get_fight() == null)
			{
				if(client.getPlayer().getPodUsed() > client.getPlayer().getMaxPod())
				{
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "112");
					SocketManager.GAME_SEND_GA_PACKET(client, "", "0", "", "");
					client.removeAction(GA);
					return;
				}
				
				AtomicReference<String> pathRef = new AtomicReference<String>(path);
				int result = Pathfinding.isValidPath(client.getPlayer().get_curCarte(), client.getPlayer().get_curCell().getID(), pathRef, null);
				
				Case targetCell = client.getPlayer().get_curCarte().getCase(CryptManager.cellCode_To_ID(path.substring(path.length()-2)));
				
				//Si d�placement inutile
				if(result == 0)
				{
					if(targetCell != null)
					{
						if(targetCell.getObject() != null)
						{
							if(targetCell.getObject().getID() == 1324) {
								Constants.applyPlotIOAction(client.getPlayer(),client.getPlayer().get_curCarte().get_id(),targetCell.getID());
							}else if(targetCell.getObject().getID() == 542) {
								if(client.getPlayer()._isGhosts) client.getPlayer().set_Alive();
							}
							SocketManager.GAME_SEND_GA_PACKET(client, "", "0", "", "");					
							client.removeAction(GA);
							return;
						}
					}
					SocketManager.GAME_SEND_GA_PACKET(client, "", "0", "", "");
					client.removeAction(GA);
					return;
				}
				
				if(result != -1000 && result < 0)
					result = -result;
				
				//On prend en compte le nouveau path
				path = pathRef.get();
				//Si le path est invalide
				if(result == -1000)
				{
					Log.addToLog(client.getPlayer().get_name()+"("+client.getPlayer().get_GUID()+") Tentative de  deplacement avec un path invalide");
					path = CryptManager.getHashedValueByInt(client.getPlayer().get_orientation())+CryptManager.cellID_To_Code(client.getPlayer().get_curCell().getID());	
				}
				//On sauvegarde le path dans la variable
				GA.setArgs(path);
				
				SocketManager.GAME_SEND_GA_PACKET_TO_MAP(client.getPlayer().get_curCarte(), ""+GA.getId(), 1, client.getPlayer().get_GUID()+"", "a"+CryptManager.cellID_To_Code(client.getPlayer().get_curCell().getID())+path);
				client.addAction(GA);
				if(client.getPlayer().isSitted())client.getPlayer().setSitted(false);
				client.getPlayer().set_away(true);
			}else
			{
				Fighter F = client.getPlayer().get_fight().getFighterByPerso(client.getPlayer());
				if(F == null)return;
				GA.setArgs(path);
				client.getPlayer().get_fight().fighterDeplace(F,GA);
			}
		}
}