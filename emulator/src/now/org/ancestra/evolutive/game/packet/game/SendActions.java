package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.CryptManager;
import org.ancestra.evolutive.common.Pathfinding;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.collector.Collector;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.game.GameAction;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.InteractiveObject;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@Packet("GA")
public class SendActions implements PacketParser {

	public static ReentrantLock locker = new ReentrantLock();

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() != null) {
            parseGameActionPacket(client, packet);
        }
	}
	
	public static void parseGameActionPacket(GameClient client, String packet) {
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
				client.getPlayer().setIsOK(Integer.parseInt(packet.substring(5,6)));
				SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(client.getPlayer().getMap(), "", client.getPlayer().getId(), client.getPlayer().getName(), "Oui");
				if(World.data.getMarried(0).getIsOK() > 0 && World.data.getMarried(1).getIsOK() > 0)
					World.data.Wedding(World.data.getMarried(0), World.data.getMarried(1), 1);
				if(World.data.getMarried(0) != null && World.data.getMarried(1) != null)
					World.data.PriestRequest((World.data.getMarried(0)==client.getPlayer()?World.data.getMarried(1):World.data.getMarried(0)), (World.data.getMarried(0)==client.getPlayer()?World.data.getMarried(1).getMap():World.data.getMarried(0).getMap()), client.getPlayer().getIsTalkingWith());
			break;
			case 619://Mariage non
				client.getPlayer().setIsOK(0);
				SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(client.getPlayer().getMap(), "", client.getPlayer().getId(), client.getPlayer().getName(), "Non");
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
			House house = client.getPlayer().getCurHouse();
			if(house == null) 
				return;
			switch(action)
			{
				case 81://V�rouiller maison
					house.lock(client.getPlayer());
				break;
				case 97://Acheter maison
					house.buyIt(client.getPlayer());
				break;
				case 98://Vendre
				case 108://Modifier prix de vente
					house.sellIt(client.getPlayer());
				break;
			}
		}
		
		public static void game_perco(GameClient client, String packet)
		{
			try
			{
				if(client.getPlayer() == null)return;
				if(client.getPlayer().getFight() != null)return;
				if(client.getPlayer().getIsTalkingWith() != 0 ||
				   client.getPlayer().getIsTradingWith() != 0 ||
				   client.getPlayer().getCurJobAction() != null ||
				   client.getPlayer().getCurExchange() != null ||
				   client.getPlayer().isAway())
						{
							return;
						}
				int id = Integer.parseInt(packet.substring(5));
				Collector target = World.data.getPerco(id);
				if(target == null || target.get_inFight() > 0) return;
				if(target.get_Exchange())
				{
					
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1180");
					return;
				}
				SocketManager.GAME_SEND_GA_PACKET_TO_MAP(client.getPlayer().getMap(),"", 909, client.getPlayer().getId()+"", id+"");
				client.getPlayer().getMap().startFigthVersusPercepteur(client.getPlayer(), target);
			}catch(Exception e){};
		}
		
		public static void game_aggro(GameClient client, String packet) {
			locker.lock();
			try {
				if(client.getPlayer() == null)return;
				if(client.getPlayer().getFight() != null)return;
				int id = Integer.parseInt(packet.substring(5));
				Player target = World.data.getPlayer(id);
				if(target == null || !target.isOnline() || target.getFight() != null
					|| target.getMap().getId() != client.getPlayer().getMap().getId()
					|| target.getAlign() == client.getPlayer().getAlign()
					|| client.getPlayer().getMap().getPlaces().equalsIgnoreCase("|")
					|| !target.isCanAggro())
					return;
				
				client.getPlayer().toggleWings('+');
				SocketManager.GAME_SEND_GA_PACKET_TO_MAP(client.getPlayer().getMap(),"", 906, client.getPlayer().getId()+"", id+"");
				
				if(target.getAlign() == Alignement.NEUTRE) {
					client.getPlayer().setDeshonor(client.getPlayer().getDeshonor()+1);
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "084;1");
					client.getPlayer().getMap().newFight(client.getPlayer(), target, Constants.FIGHT_TYPE_AGRESSION, true);
				} else 
					client.getPlayer().getMap().newFight(client.getPlayer(), target, Constants.FIGHT_TYPE_AGRESSION, false);
				
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
			if(cell == -1 || action == -1 || client.getPlayer() == null || client.getPlayer().getMap() == null ||
					client.getPlayer().getMap().getCases().get(cell) == null)
				return;
			GA.setArgs(cell+";"+action);
			client.getPlayer().getAccount().getGameClient().addAction(GA);
            if(client.getLastPacketSent().equals("GA;0")){
                client.getPlayer().getMap().getCases().get(cell).startAction(client.getPlayer(),GA);
            }
		}
	
		public static void game_tryCac(GameClient client, String packet)
		{
			try
			{
				if(client.getPlayer().getFight() ==null)return;
				int cellID = -1;
				try
				{
					cellID = Integer.parseInt(packet.substring(5));
				}catch(Exception e){return;};
				
				client.getPlayer().getFight().tryCaC(client.getPlayer(),cellID);
			}catch(Exception e){};
		}
	
		public static void game_tryCastSpell(GameClient client, String packet)
		{
			try
			{
				String[] splt = packet.split(";");
				int spellID = Integer.parseInt(splt[0].substring(5));
				int caseID = Integer.parseInt(splt[1]);
				if(client.getPlayer().getFight() != null)
				{
					SpellStats SS = client.getPlayer().getSortStatBySortIfHas(spellID);
					if(SS == null)return;
					client.getPlayer().getFight().tryCastSpell(client.getPlayer().getFight().getFighterByPerso(client.getPlayer()),SS,caseID);
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
					Fight F = client.getPlayer().getMap().getFights().get(Integer.parseInt(infos[0]));
					F.joinAsSpect(client.getPlayer());
				}catch(Exception e){return;};
			}else
			{
				try
				{
					int guid = Integer.parseInt(infos[1]);
					if(client.getPlayer().isAway())
					{
						SocketManager.GAME_SEND_GA903_ERROR_PACKET(client,'o',guid);
						return;
					}
					if(World.data.getPlayer(guid) == null)return;
					World.data.getPlayer(guid).getFight().joinFight(client.getPlayer(),guid);
				}catch(Exception e){return;};
			}
		}
	
		public static void game_accept_duel(GameClient client, String packet)
		{
			int guid = -1;
			try{guid = Integer.parseInt(packet.substring(5));}catch(NumberFormatException e){return;};
			if(client.getPlayer().getDuel() != guid || client.getPlayer().getDuel() == -1)return;
			SocketManager.GAME_SEND_MAP_START_DUEL_TO_MAP(client.getPlayer().getMap(),client.getPlayer().getDuel(),client.getPlayer().getId());
			Fight fight = client.getPlayer().getMap().newFight(World.data.getPlayer(client.getPlayer().getDuel()),client.getPlayer(),Constants.FIGHT_TYPE_CHALLENGE, false);
			client.getPlayer().setFight(fight);
			World.data.getPlayer(client.getPlayer().getDuel()).setFight(fight);
		}
	
		public static void game_cancel_duel(GameClient client, String packet)
		{
			try
			{
				if(client.getPlayer().getDuel() == -1)return;
				SocketManager.GAME_SEND_CANCEL_DUEL_TO_MAP(client.getPlayer().getMap(),client.getPlayer().getDuel(),client.getPlayer().getId());
				World.data.getPlayer(client.getPlayer().getDuel()).setAway(false);
				World.data.getPlayer(client.getPlayer().getDuel()).setDuel(-1);
				client.getPlayer().setAway(false);
				client.getPlayer().setDuel(-1);	
			}catch(NumberFormatException e){return;};
		}
	
		public static void game_ask_duel(GameClient client, String packet)
		{
			if(client.getPlayer().getMap().getPlaces().equalsIgnoreCase("|"))
			{
				SocketManager.GAME_SEND_DUEL_Y_AWAY(client, client.getPlayer().getId());
				return;
			}
			try
			{
				int guid = Integer.parseInt(packet.substring(5));
				if(client.getPlayer().isAway() || client.getPlayer().getFight() != null) {
					SocketManager.GAME_SEND_DUEL_Y_AWAY(client, client.getPlayer().getId());
					return;
				}
				
				Player Target = World.data.getPlayer(guid);
				
				if(Target == null) 
					return;
				if(Target.isAway() || Target.getFight() != null || Target.getMap().getId() != client.getPlayer().getMap().getId()) {
					SocketManager.GAME_SEND_DUEL_E_AWAY(client, client.getPlayer().getId());
					return;
				}
				
				client.getPlayer().setDuel(guid);
				client.getPlayer().setAway(true);
				World.data.getPlayer(guid).setDuel(client.getPlayer().getId());
				World.data.getPlayer(guid).setAway(true);
				SocketManager.GAME_SEND_MAP_NEW_DUEL_TO_MAP(client.getPlayer().getMap(),client.getPlayer().getId(),guid);
			}catch(NumberFormatException e){return;}
		}
	
		public static void game_parseDeplacementPacket(GameClient client, GameAction GA)
		{
			String path = GA.getPacket().substring(5);
			if(client.getPlayer().getFight() == null)
			{
				if(client.getPlayer().getPodUsed() > client.getPlayer().getMaxPod())
				{
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "112");
					SocketManager.GAME_SEND_GA_PACKET(client, "", "0", "", "");
					client.removeAction(GA);
					return;
				}
				
				AtomicReference<String> pathRef = new AtomicReference<>(path);
				int result = Pathfinding.isValidPath(client.getPlayer().getMap(), client.getPlayer().getCell().getId(), pathRef, null);
				Case targetCell = client.getPlayer().getMap().getCases().get(CryptManager.cellCode_To_ID(path.substring(path.length()-2)));
				
				//Si d�placement inutile
				if(result == 0)
				{
					if(targetCell != null)
					{
						InteractiveObject IO = targetCell.getInteractiveObject();
						if(IO != null) {							
							IO.getActionIO(client.getPlayer(), targetCell);
							IO.getSignIO(client.getPlayer(), targetCell.getId());
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
					Log.addToLog(client.getPlayer().getName()+"("+client.getPlayer().getId()+") Tentative de  deplacement avec un path invalide");
					path = CryptManager.getHashedValueByInt(client.getPlayer().getOrientation())+CryptManager.cellID_To_Code(client.getPlayer().getCell().getId());
				}
				//On sauvegarde le path dans la variable
				GA.setArgs(path);
				
				SocketManager.GAME_SEND_GA_PACKET_TO_MAP(client.getPlayer().getMap(), ""+GA.getId(), 1, client.getPlayer().getId()+"", "a"+CryptManager.cellID_To_Code(client.getPlayer().getCell().getId())+path);
				client.addAction(GA);
				client.getPlayer().setEmoteActive(0);
				client.getPlayer().setAway(true);
			}else
			{
				Fighter F = client.getPlayer().getFight().getFighterByPerso(client.getPlayer());
				if(F == null)return;
				GA.setArgs(path);
				client.getPlayer().getFight().fighterDeplace(F,GA);
			}
		}
}