package org.ancestra.evolutive.common;

import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.monster.MobGroup;
import org.ancestra.evolutive.entity.npc.Npc;
import org.ancestra.evolutive.entity.npc.NpcQuestion;
import org.ancestra.evolutive.entity.npc.NpcTemplate;
import org.ancestra.evolutive.enums.EmulatorInfos;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.job.JobStat;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.map.MountPark;
import org.ancestra.evolutive.object.ItemSet;
import org.ancestra.evolutive.object.Objet;
import org.ancestra.evolutive.object.Objet.ObjTemplate;
import org.ancestra.evolutive.other.Action;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

public class Commands {
	Account _compte;
	Player _perso;
	GameClient _out;
	//Sauvegarde
	private boolean _TimerStart = false;
	Timer _timer;
	
	private Timer createTimer(final int time)
	{
	    ActionListener action = new ActionListener ()
	    {
	    	int Time = time;
	        @Override
			public void actionPerformed (ActionEvent event)
	        {
	        	Time = Time-1;
	        	if(Time == 1)
	        	{
	        		SocketManager.GAME_SEND_Im_PACKET_TO_ALL("115;"+Time+" minute");
	        	}else
	        	{
		        	SocketManager.GAME_SEND_Im_PACKET_TO_ALL("115;"+Time+" minutes");
	        	}
	        	if(Time <= 0)
	        	{
	        		for(Player perso : World.data.getOnlinePersos())
	        		{
	        			perso.getAccount().getGameClient().kick();
	        		}
	    			System.exit(0);
	        	}
	        }
	    };
	    // G�n�ration du repeat toutes les minutes.
	    return new Timer (60000, action);//60000
	}
	
	public Commands(Player perso)
	{
		this._compte = perso.getAccount();
		this._perso = perso;
		this._out = _compte.getGameClient();
	}
	
	public void consoleCommand(String packet)
	{
		
		if(_compte.getGmLvl() < 1)
		{
			_compte.getGameClient().closeSocket();
			return;
		}
		
		String msg = packet.substring(2);
		String[] infos = msg.split(" ");
		if(infos.length == 0)return;
		String command = infos[0];
		
		if(Server.config.isCanLog())
		{
			Log.addToMjLog(_compte.getCurIp()+": "+_compte.getName()+" "+_perso.getName()+"=>"+msg);
		}
		
		if(_compte.getGmLvl() == 1)
		{
			commandGmOne(command, infos, msg);
		}else
		if(_compte.getGmLvl() == 2)
		{
			commandGmTwo(command, infos, msg);
		}
		else
		if(_compte.getGmLvl() == 3)
		{
			commandGmThree(command, infos, msg);
		}
		else
		if(_compte.getGmLvl() >= 4)
		{
			commandGmFour(command, infos, msg);
		}
	}
	
	public void commandGmOne(String command, String[] infos, String msg)
	{
		if(_compte.getGmLvl() < 1)
		{
			_compte.getGameClient().closeSocket();
			return;
		}
		if(command.equalsIgnoreCase("INFOS"))
		{
			long uptime = System.currentTimeMillis() - Server.config.getGameServer().getStartTime();
			int jour = (int) (uptime/(1000*3600*24));
			uptime %= (1000*3600*24);
			int hour = (int) (uptime/(1000*3600));
			uptime %= (1000*3600);
			int min = (int) (uptime/(1000*60));
			uptime %= (1000*60);
			int sec = (int) (uptime/(1000));
			
			String mess =	"===========\n"+EmulatorInfos.SOFT_NAME.toString()
				+			"Uptime: "+jour+"j "+hour+"h "+min+"m "+sec+"s\n"
				+			"Joueurs en lignes: "+Server.config.getGameServer().getPlayerNumber()+"\n"
				+			"Record de connexion: "+Server.config.getGameServer().getMaxPlayer()+"\n"
				+			"===========";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			return;
		}else
		if(command.equalsIgnoreCase("REFRESHMOBS"))
		{
			_perso.getCurMap().refreshSpawns();
			String mess = "Mob Spawn refreshed!";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			return;
		}if(command.equalsIgnoreCase("MAPINFO"))
		{
			String mess = 	"==========\n"
						+	"Liste des Npcs de la carte:";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			Maps map = _perso.getCurMap();
			for(Entry<Integer, Npc> entry : map.getNpcs().entrySet())
			{
				mess = entry.getKey()+" "+entry.getValue().getTemplate().getId()+" "+entry.getValue().getCell().getId()+" "+entry.getValue().getTemplate().getInitQuestion();
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			}
			mess = "Liste des groupes de monstres:";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			for(Entry<Integer, MobGroup> entry : map.getMobGroups().entrySet())
			{
				mess = entry.getKey()+" "+entry.getValue().getCell().getId()+" "+entry.getValue().getAlign()+" "+entry.getValue().getMobs().size();
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			}
			mess = "==========";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			return;
		}else
		if(command.equalsIgnoreCase("WHO"))
		{
			String mess = 	"==========\n"
				+			"Liste des joueurs en ligne:";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			for(GameClient client: Server.config.getGameServer().getClients().values()) {
				Player P = client.getPlayer();
				if(P == null)continue;
				mess = P.getName()+"("+P.getUUID()+") ";
				
				switch(P.getClasse().getId())
				{
					case Constants.CLASS_FECA:
						mess += "Fec";
					break;
					case Constants.CLASS_OSAMODAS:
						mess += "Osa";
					break;
					case Constants.CLASS_ENUTROF:
						mess += "Enu";
					break;
					case Constants.CLASS_SRAM:
						mess += "Sra";
					break;
					case Constants.CLASS_XELOR:
						mess += "Xel";
					break;
					case Constants.CLASS_ECAFLIP:
						mess += "Eca";
					break;
					case Constants.CLASS_ENIRIPSA:
						mess += "Eni";
					break;
					case Constants.CLASS_IOP:
						mess += "Iop";
					break;
					case Constants.CLASS_CRA:
						mess += "Cra";
					break;
					case Constants.CLASS_SADIDA:
						mess += "Sad";
					break;
					case Constants.CLASS_SACRIEUR:
						mess += "Sac";
					break;
					case Constants.CLASS_PANDAWA:
						mess += "Pan";
					break;
					default:
						mess += "Unk";
				}
				mess += " ";
				mess += (P.getSex()==0?"M":"F")+" ";
				mess += P.getLevel()+" ";
				mess += P.getCurMap().getId()+"("+P.getCurMap().getX()+"/"+P.getCurMap().getY()+") ";
				mess += P.getFight()==null?"":"Combat ";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			}
			mess = 	"==========\n";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			return;
		}else
		if(command.equalsIgnoreCase("SHOWFIGHTPOS"))
		{
			String mess = "Liste des StartCell [teamID][cellID]:";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			String places = _perso.getCurMap().getPlaces();
			if(places.indexOf('|') == -1 || places.length() <2)
			{
				mess = "Les places n'ont pas ete definies";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
				return;
			}
			String team0 = "",team1 = "";
			String[] p = places.split("\\|");
			try
			{
				team0 = p[0];
			}catch(Exception e){};
			try
			{
				team1 = p[1];
			}catch(Exception e){};
			mess = "Team 0:\n";
			for(int a = 0;a <= team0.length()-2; a+=2)
			{
				String code = team0.substring(a,a+2);
				mess += CryptManager.cellCode_To_ID(code);
			}
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			mess = "Team 1:\n";
			for(int a = 0;a <= team1.length()-2; a+=2)
			{
				String code = team1.substring(a,a+2);
				mess += CryptManager.cellCode_To_ID(code)+" , ";
			}
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			return;
		}else
		if(command.equalsIgnoreCase("CREATEGUILD"))
		{
			Player perso = _perso;
			if(infos.length >1)
			{
				perso = World.data.getPersoByName(infos[1]);
			}
			if(perso == null)
			{
				String mess = "Le personnage n'existe pas.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			
			if(!perso.isOnline())
			{
				String mess = "Le personnage "+perso.getName()+" n'etait pas connecte";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			if(perso.getGuild() != null || perso.getGuildMember() != null)
			{
				String mess = "Le personnage "+perso.getName()+" a deja une guilde";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			SocketManager.GAME_SEND_gn_PACKET(perso);
			String mess = perso.getName()+": Panneau de creation de guilde ouvert";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
			return;
		}else
		if(command.equalsIgnoreCase("TOOGLEAGGRO"))
		{
			Player perso = _perso;
			
			String name = null;
			try
			{
				name = infos[1];
			}catch(Exception e){};
			
			perso = World.data.getPersoByName(name);
			
			if(perso == null)
			{
				String mess = "Le personnage n'existe pas.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			
			perso.setCanAggro(!perso.isCanAggro());
			String mess = perso.getName();
			if(perso.isCanAggro()) mess += " peut maintenant etre aggresser";
			else mess += " ne peut plus etre agresser";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
			
			if(!perso.isOnline())
			{
				mess = "(Le personnage "+perso.getName()+" n'etait pas connecte)";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
			}
		}else
		if(command.equalsIgnoreCase("ANNOUNCE"))
		{
			infos = msg.split(" ",2);
			SocketManager.GAME_SEND_MESSAGE_TO_ALL(infos[1], Server.config.getMotdColor());
			return;
		}else
		if(command.equalsIgnoreCase("DEMORPH"))
		{
			Player target = _perso;
			if(infos.length > 1)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[1]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			int morphID = target.getClasse().getId()*10 + target.getSex();
			target.setGfx(morphID);
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(target.getCurMap(), target.getUUID());
			SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(target.getCurMap(), target);
			String str = "Le joueur a ete transforme";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}
		else
		if(command.equalsIgnoreCase("GONAME") || command.equalsIgnoreCase("JOIN"))
		{
			Player P = World.data.getPersoByName(infos[1]);
			if(P == null)
			{
				String str = "Le personnage n'existe pas";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			short mapID = P.getCurMap().getId();
			int cellID = P.getCurCell().getId();
			
			Player target = _perso;
			if(infos.length > 2)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
				if(target.getFight() != null)
				{
					String str = "La cible est en combat";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			target.teleport(mapID, cellID);
			String str = "Le joueur a ete teleporte";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("NAMEGO"))
		{
			Player target = World.data.getPersoByName(infos[1]);
			if(target == null)
			{
				String str = "Le personnage n'existe pas";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			if(target.getFight() != null)
			{
				String str = "La cible est en combat";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Player P = _perso;
			if(infos.length > 2)//Si un nom de perso est sp�cifi�
			{
				P = World.data.getPersoByName(infos[2]);
				if(P == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			if(P.isOnline())
			{
				short mapID = P.getCurMap().getId();
				int cellID = P.getCurCell().getId();
				target.teleport(mapID, cellID);
				String str = "Le joueur a ete teleporte";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
			}else
			{
				String str = "Le joueur n'est pas en ligne";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
			}
		}else
		if(command.equalsIgnoreCase("NAMEANNOUNCE"))
		{
			infos = msg.split(" ",2);
			String prefix = "["+_perso.getName()+"]";
			SocketManager.GAME_SEND_MESSAGE_TO_ALL(prefix+infos[1], Server.config.getMotdColor());
			return;
		}else
		if(command.equalsIgnoreCase("TELEPORT"))
		{
			short mapID = -1;
			int cellID = -1;
			try
			{
				mapID = Short.parseShort(infos[1]);
				cellID = Integer.parseInt(infos[2]);
			}catch(Exception e){};
			if(mapID == -1 || cellID == -1 || World.data.getCarte(mapID) == null)
			{
				String str = "MapID ou cellID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			if(World.data.getCarte(mapID).getCases().get(cellID) == null)
			{
				String str = "MapID ou cellID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Player target = _perso;
			if(infos.length > 3)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[3]);
				if(target == null  || target.getFight() != null)
				{
					String str = "Le personnage n'a pas ete trouve ou est en combat";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			target.teleport(mapID, cellID);
			String str = "Le joueur a ete teleporte";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("GOMAP"))
		{
			int mapX = 0;
			int mapY = 0;
			int cellID = 0;
			int cont = 0;
			try
			{
				mapX = Integer.parseInt(infos[1]);
				mapY = Integer.parseInt(infos[2]);
				cellID = Integer.parseInt(infos[3]);
				cont = Integer.parseInt(infos[4]);
				
			}catch(Exception e){};
			Maps map = World.database.getMapData().loadMapByPos(mapX, mapY, cont);
			if(map == null)
			{
				String str = "Position ou continent invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			if(map.getCases().get(cellID) == null)
			{
				String str = "CellID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Player target = _perso;
			if(infos.length > 5)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[5]);
				if(target == null || target.getFight() != null)
				{
					String str = "Le personnage n'a pas ete trouve ou est en combat";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
				if(target.getFight() != null)
				{
					String str = "La cible est en combat";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			target.teleport(map.getId(), cellID);
			String str = "Le joueur a ete teleporte";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("DOACTION"))
		{
			//DOACTION NAME TYPE ARGS COND
			if(infos.length < 4)
			{
				String mess = "Nombre d'argument de la commande incorect !";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			int type = -100;
			String args = "",cond = "";
			Player perso = _perso;
			try
			{
				perso = World.data.getPersoByName(infos[1]);
				if(perso == null)perso = _perso;
				type = Integer.parseInt(infos[2]);
				args = infos[3];
				if(infos.length >4)
				cond = infos[4];
			}catch(Exception e)
			{
				String mess = "Arguments de la commande incorect !";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			(new Action(type,args,cond)).apply(perso, null, -1, -1);
			String mess = "Action effectuee !";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
		}else
		{
			String mess = "Commande non reconnue";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
		}
	}
	
	public void commandGmTwo(String command, String[] infos, String msg)
	{
		if(_compte.getGmLvl() < 2)
		{
			_compte.getGameClient().closeSocket();
			return;
		}
		
		if(command.equalsIgnoreCase("MUTE"))
		{
			Player perso = _perso;
			String name = null;
			try
			{
				name = infos[1];
			}catch(Exception e){};
			int time = 0;
			try
			{
				time = Integer.parseInt(infos[2]);
			}catch(Exception e){};
			
			perso = World.data.getPersoByName(name);
			if(perso == null || time < 0)
			{
				String mess = "Le personnage n'existe pas ou la duree est invalide.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			String mess = "Vous avez mute "+perso.getName()+" pour "+time+" secondes";
			if(perso.getAccount() == null)
			{
				mess = "(Le personnage "+perso.getName()+" n'etait pas connecte)";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			perso.getAccount().mute(true,time);
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
			
			if(!perso.isOnline())
			{
				mess = "(Le personnage "+perso.getName()+" n'etait pas connecte)";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
			}else
			{
				SocketManager.GAME_SEND_Im_PACKET(perso, "1124;"+time);
			}
			return;
		}else
		if(command.equalsIgnoreCase("UNMUTE"))
		{
			Player perso = _perso;
			
			String name = null;
			try
			{
				name = infos[1];
			}catch(Exception e){};
			
			perso = World.data.getPersoByName(name);
			if(perso == null)
			{
				String mess = "Le personnage n'existe pas.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			
			perso.getAccount().mute(false,0);
			String mess = "Vous avez unmute "+perso.getName();
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
			
			if(!perso.isOnline())
			{
				mess = "(Le personnage "+perso.getName()+" n'etait pas connecte)";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
			}
		}else
		if(command.equalsIgnoreCase("KICK"))
		{
			Player perso = _perso;
			String name = null;
			try
			{
				name = infos[1];
			}catch(Exception e){};
			perso = World.data.getPersoByName(name);
			if(perso == null)
			{
				String mess = "Le personnage n'existe pas.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			if(perso.isOnline())
			{
				perso.getAccount().getGameClient().kick();
				String mess = "Vous avez kick "+perso.getName();
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
			}
			else
			{
				String mess = "Le personnage "+perso.getName()+" n'est pas connecte";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
			}
		}else
		if(command.equalsIgnoreCase("SPELLPOINT"))
		{
			int pts = -1;
			try
			{
				pts = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			if(pts == -1)
			{
				String str = "Valeur invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Player target = _perso;
			if(infos.length > 2)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			target.addSpellPoint(pts);
			SocketManager.GAME_SEND_STATS_PACKET(target);
			String str = "Le nombre de point de sort a ete modifiee";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("LEARNSPELL"))
		{
			int spell = -1;
			try
			{
				spell = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			if(spell == -1)
			{
				String str = "Valeur invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Player target = _perso;
			if(infos.length > 2)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			
			target.learnSpell(spell, 1, true,true);
			
			String str = "Le sort a ete appris";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("SETALIGN"))
		{
			byte align = -1;
			try
			{
				align = Byte.parseByte(infos[1]);
			}catch(Exception e){};
			if(align < Constants.ALIGNEMENT_NEUTRE || align >Constants.ALIGNEMENT_MERCENAIRE)
			{
				String str = "Valeur invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Player target = _perso;
			if(infos.length > 2)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			
			target.modifAlignement(align);
			
			String str = "L'alignement du joueur a ete modifie";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("SHOWREPONSES"))
		{
			int id = 0;
			try
			{
				id = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			NpcQuestion Q = World.data.getNpcQuestion(id);
			String str = "";
			if(id == 0 || Q == null)
			{
				str = "QuestionID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			str = "Liste des reponses pour la question "+id+": "+Q.getAnswer();
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
			return;
		}else
		if(command.equalsIgnoreCase("HONOR"))
		{
			int honor = 0;
			try
			{
				honor = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			Player target = _perso;
			if(infos.length > 2)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			String str = "Vous avez ajouter "+honor+" honneur a "+target.getName();
			if(target.getAlign() == Constants.ALIGNEMENT_NEUTRE)
			{
				str = "Le joueur est neutre ...";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			target.addHonor(honor);
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
			
		}else
		if(command.equalsIgnoreCase("ADDJOBXP"))
		{
			int job = -1;
			int xp = -1;
			try
			{
				job = Integer.parseInt(infos[1]);
				xp = Integer.parseInt(infos[2]);
			}catch(Exception e){};
			if(job == -1 || xp < 0)
			{
				String str = "Valeurs invalides";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
				Player target = _perso;
			if(infos.length > 3)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[3]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			JobStat SM = target.getMetierByID(job);
			if(SM== null)
			{
				String str = "Le joueur ne connais pas le metier demande";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
				
			SM.addXp(target, xp);
			
			String str = "Le metier a ete experimenter";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("LEARNJOB"))
		{
			int job = -1;
			try
			{
				job = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			if(job == -1 || World.data.getMetier(job) == null)
			{
				String str = "Valeur invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Player target = _perso;
			if(infos.length > 2)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			
			target.learnJob(World.data.getMetier(job));
			
			String str = "Le metier a ete appris";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("CAPITAL"))
		{
			int pts = -1;
			try
			{
				pts = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			if(pts == -1)
			{
				String str = "Valeur invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Player target = _perso;
			if(infos.length > 2)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			target.addCapital(pts);
			SocketManager.GAME_SEND_STATS_PACKET(target);
			String str = "Le capital a ete modifiee";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}
		if(command.equalsIgnoreCase("SIZE"))
		{
			int size = -1;
			try
			{
				size = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			if(size == -1)
			{
				String str = "Taille invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Player target = _perso;
			if(infos.length > 2)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			target.setSize(size);
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(target.getCurMap(), target.getUUID());
			SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(target.getCurMap(), target);
			String str = "La taille du joueur a ete modifiee";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("MORPH"))
		{
			int morphID = -1;
			try
			{
				morphID = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			if(morphID == -1)
			{
				String str = "MorphID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Player target = _perso;
			if(infos.length > 2)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			target.setGfx(morphID);
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(target.getCurMap(), target.getUUID());
			SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(target.getCurMap(), target);
			String str = "Le joueur a ete transforme";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}if(command.equalsIgnoreCase("MOVENPC"))
		{
			int id = 0;
			try
			{
				id = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			Npc npc = _perso.getCurMap().getNpcs().get(id);
			if(id == 0 || npc == null)
			{
				String str = "Npc GUID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			int exC = npc.getCell().getId();
			//on l'efface de la map
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(_perso.getCurMap(), id);
			//on change sa position/orientation
			npc.setCell(_perso.getCurCell());
			npc.setOrientation((byte)_perso.getOrientation());
			//on envoie la modif
			SocketManager.GAME_SEND_ADD_NPC_TO_MAP(_perso.getCurMap(),npc);
			String str = "Le PNJ a ete deplace";
			if(_perso.getOrientation() == 0
			|| _perso.getOrientation() == 2
			|| _perso.getOrientation() == 4
			|| _perso.getOrientation() == 6)
				str += " mais est devenu invisible (orientation diagonale invalide).";
			if(World.database.getNpcData().delete(_perso.getCurMap().getId(),exC)
			&& World.database.getNpcData().create(_perso.getCurMap().getId(),npc.getTemplate().getId(),_perso.getCurCell().getId(),_perso.getOrientation()))
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
			else
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,"Erreur au moment de sauvegarder la position");
		}else	
		if(command.equalsIgnoreCase("ITEMSET"))
		{
			int tID = 0;
			try
			{
				tID = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			ItemSet IS = World.data.getItemSet(tID);
			if(tID == 0 || IS == null)
			{
				String mess = "La panoplie "+tID+" n'existe pas ";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			boolean useMax = false;
			if(infos.length == 3)useMax = infos[2].equals("MAX");//Si un jet est sp�cifi�

			
			for(ObjTemplate t : IS.getItemTemplates())
			{
				Objet obj = t.createNewItem(1,useMax);
				if(_perso.addObjet(obj, true))//Si le joueur n'avait pas d'item similaire
					World.data.addObjet(obj,true);
			}
			String str = "Creation de la panoplie "+tID+" reussie";
			if(useMax) str += " avec des stats maximums";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("LEVEL"))
		{
			int count = 0;
			try
			{
				count = Integer.parseInt(infos[1]);
				if(count < 1)	count = 1;
				if(count > World.data.getExpLevelSize())	count = World.data.getExpLevelSize();
				Player perso = _perso;
				if(infos.length == 3)//Si le nom du perso est sp�cifi�
				{
					String name = infos[2];
					perso = World.data.getPersoByName(name);
					if(perso == null)
						perso = _perso;
				}
				if(perso.getLevel() < count)
				{
					while(perso.getLevel() < count)
					{
						perso.levelUp(false,true);
					}
					if(perso.isOnline())
					{
						SocketManager.GAME_SEND_SPELL_LIST(perso);
						SocketManager.GAME_SEND_NEW_LVL_PACKET(perso.getAccount().getGameClient(),perso.getLevel());
						SocketManager.GAME_SEND_STATS_PACKET(perso);
					}
				}
				String mess = "Vous avez fixer le niveau de "+perso.getName()+" a "+count;
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
			}catch(Exception e)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Valeur incorecte");
				return;
			};
		}else
		if(command.equalsIgnoreCase("PDVPER"))
		{
			int count = 0;
			try
			{
				count = Integer.parseInt(infos[1]);
				if(count < 0)	count = 0;
				if(count > 100)	count = 100;
				Player perso = _perso;
				if(infos.length == 3)//Si le nom du perso est sp�cifi�
				{
					String name = infos[2];
					perso = World.data.getPersoByName(name);
					if(perso == null)
						perso = _perso;
				}
				int newPDV = perso.getMaxPdv() * count / 100;
				perso.setPdv(newPDV);
				if(perso.isOnline())
					SocketManager.GAME_SEND_STATS_PACKET(perso);
				String mess = "Vous avez fixer le pourcentage de pdv de "+perso.getName()+" a "+count;
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
			}catch(Exception e)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Valeur incorecte");
				return;
			};
		}else
		if(command.equalsIgnoreCase("KAMAS"))
		{
			int count = 0;
			try
			{
				count = Integer.parseInt(infos[1]);
			}catch(Exception e)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Valeur incorecte");
				return;
			};
			if(count == 0)return;
			
			Player perso = _perso;
			if(infos.length == 3)//Si le nom du perso est sp�cifi�
			{
				String name = infos[2];
				perso = World.data.getPersoByName(name);
				if(perso == null)
					perso = _perso;
			}
			long curKamas = perso.getKamas();
			long newKamas = curKamas + count;
			if(newKamas <0) newKamas = 0;
			if(newKamas > 1000000000) newKamas = 1000000000;
			perso.setKamas(newKamas);
			if(perso.isOnline())
				SocketManager.GAME_SEND_STATS_PACKET(perso);
			String mess = "Vous avez ";
			mess += (count<0?"retirer":"ajouter")+" ";
			mess += Math.abs(count)+" kamas a "+perso.getName();
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
		}else
		if(command.equalsIgnoreCase("ITEM") || command.equalsIgnoreCase("!getitem"))
		{
			boolean isOffiCmd = command.equalsIgnoreCase("!getitem");
			if(_compte.getGmLvl() < 2)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Vous n'avez pas le niveau MJ requis");
				return;
			}
			int tID = 0;
			try
			{
				tID = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			if(tID == 0)
			{
				String mess = "Le template "+tID+" n'existe pas ";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			int qua = 1;
			if(infos.length == 3)//Si une quantit� est sp�cifi�e
			{
				try
				{
					qua = Integer.parseInt(infos[2]);
				}catch(Exception e){};
			}
			boolean useMax = false;
			if(infos.length == 4 && !isOffiCmd)//Si un jet est sp�cifi�
			{
				if(infos[3].equalsIgnoreCase("MAX"))useMax = true;
			}
			ObjTemplate t = World.data.getObjTemplate(tID);
			if(t == null)
			{
				String mess = "Le template "+tID+" n'existe pas ";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
				return;
			}
			if(qua <1)qua =1;
			Objet obj = t.createNewItem(qua,useMax);
			if(_perso.addObjet(obj, true))//Si le joueur n'avait pas d'item similaire
				World.data.addObjet(obj,true);
			String str = "Creation de l'item "+tID+" reussie";
			if(useMax) str += " avec des stats maximums";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
			SocketManager.GAME_SEND_Ow_PACKET(_perso);
		}else 
		if (command.equalsIgnoreCase("SPAWN"))
		{			
			String Mob = null;
			try
			{
				Mob = infos[1];
			}catch(Exception e){};
            if(Mob == null) return;
			_perso.getCurMap().spawnGroupOnCommand(_perso.getCurCell().getId(), Mob);
		}else
		if (command.equalsIgnoreCase("TITLE"))
		{
			Player target = null; 
			byte TitleID = 0;
			try
			{
				target = World.data.getPersoByName(infos[1]);
				TitleID = Byte.parseByte(infos[2]);
			}catch(Exception e){};
			
			if(target == null)
			{
				String str = "Le personnage n'a pas ete trouve";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			
			target.setTitle(TitleID);
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Titre mis en place.");
			World.database.getCharacterData().update(target);
			if(target.getFight() == null) SocketManager.GAME_SEND_ALTER_GM_PACKET(target.getCurMap(), target);
		}else
		{
			this.commandGmOne(command, infos, msg);
		}
	}
	
	public void commandGmThree(String command, String[] infos, String msg)
	{
		if(_compte.getGmLvl() < 3)
		{
			_compte.getGameClient().closeSocket();
			return;
		}
		
		if(command.equalsIgnoreCase("EXIT"))
		{
			System.exit(0);
		}else
		if(command.equalsIgnoreCase("SAVE") && !Server.config.isSaving())
		{
			World.data.saveData(_perso.getUUID());
			String mess = "Sauvegarde lancee!";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, mess);
			return;
		}else
		if(command.equalsIgnoreCase("DELFIGHTPOS"))
		{
			int cell = -1;
			try
			{
				cell = Integer.parseInt(infos[2]);
			}catch(Exception e){};
			if(cell < 0 || _perso.getCurMap().getCases().get(cell) == null)
			{
				cell = _perso.getCurCell().getId();
			}
			String places = _perso.getCurMap().getPlaces();
			String[] p = places.split("\\|");
			String newPlaces = "";
			String team0 = "",team1 = "";
			try
			{
				team0 = p[0];
			}catch(Exception e){};
			try
			{
				team1 = p[1];
			}catch(Exception e){};
			
			for(int a = 0;a<=team0.length()-2;a+=2)
			{
				String c = p[0].substring(a,a+2);
				if(cell == CryptManager.cellCode_To_ID(c))continue;
				newPlaces += c;
			}
			newPlaces += "|";
			for(int a = 0;a<=team1.length()-2;a+=2)
			{
				String c = p[1].substring(a,a+2);
				if(cell == CryptManager.cellCode_To_ID(c))continue;
				newPlaces += c;
			}
			_perso.getCurMap().setPlaces(newPlaces);
			if(!World.database.getMapData().update(_perso.getCurMap()))return;
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,"Les places ont ete modifiees ("+newPlaces+")");
			return;
		}else
		if(command.equalsIgnoreCase("BAN"))
		{
			Player P = World.data.getPersoByName(infos[1]);
			if(P == null)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Personnage non trouve");
				return;
			}
			if(P.getAccount() == null)World.database.getAccountData().load(P.getAccount().getUUID());
			if(P.getAccount() == null)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Erreur");
				return;
			}
			P.getAccount().setBanned(true);
			World.database.getAccountData().update(P.getAccount());
			if(P.getAccount().getGameClient() != null)P.getAccount().getGameClient().kick();
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Vous avez banni "+P.getName());
			return;
		}else
		if(command.equalsIgnoreCase("UNBAN"))
		{
			Player P = World.data.getPersoByName(infos[1]);
			if(P == null)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Personnage non trouve");
				return;
			}
			if(P.getAccount() == null)World.database.getAccountData().load(P.getAccount().getUUID());
			if(P.getAccount() == null)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Erreur");
				return;
			}
			P.getAccount().setBanned(false);
			World.database.getAccountData().update(P.getAccount());
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Vous avez debanni "+P.getName());
			return;
		}else
		if(command.equalsIgnoreCase("ADDFIGHTPOS"))
		{
			int team = -1;
			int cell = -1;
			try
			{
				team = Integer.parseInt(infos[1]);
				cell = Integer.parseInt(infos[2]);
			}catch(Exception e){};
			if( team < 0 || team>1)
			{
				String str = "Team ou cellID incorects";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			if(cell <0 || _perso.getCurMap().getCases().get(cell) == null || !_perso.getCurMap().getCases().get(cell).isWalkable(true))
			{
				cell = _perso.getCurCell().getId();
			}
			String places = _perso.getCurMap().getPlaces();
			String[] p = places.split("\\|");
			boolean already = false;
			String team0 = "",team1 = "";
			try
			{
				team0 = p[0];
			}catch(Exception e){};
			try
			{
				team1 = p[1];
			}catch(Exception e){};
			
			//Si case d�j� utilis�e
			Console.instance.println("0 => "+team0+"\n1 =>"+team1+"\nCell: "+CryptManager.cellID_To_Code(cell));
			for(int a = 0; a <= team0.length()-2;a+=2)if(cell == CryptManager.cellCode_To_ID(team0.substring(a,a+2)))already = true;
			for(int a = 0; a <= team1.length()-2;a+=2)if(cell == CryptManager.cellCode_To_ID(team1.substring(a,a+2)))already = true;
			if(already)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,"La case est deja dans la liste");
				return;
			}
			if(team == 0)team0 += CryptManager.cellID_To_Code(cell);
			else if(team == 1)team1 += CryptManager.cellID_To_Code(cell);
			
			String newPlaces = team0+"|"+team1;
			
			_perso.getCurMap().setPlaces(newPlaces);
			if(!World.database.getMapData().update(_perso.getCurMap()))return;
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,"Les places ont ete modifiees ("+newPlaces+")");
			return;
		}else
		if(command.equalsIgnoreCase("SETMAXGROUP"))
		{
			infos = msg.split(" ",4);
			byte id = -1;
			try
			{
				id = Byte.parseByte(infos[1]);
			}catch(Exception e){};
			if(id == -1)
			{
				String str = "Valeur invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			String mess = "Le nombre de groupe a ete fixe";
			_perso.getCurMap().setMaxGroup(id);
			boolean ok = World.database.getMapData().update(_perso.getCurMap());
			if(ok)mess += " et a ete sauvegarder a la BDD";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,mess);
		}else
		if(command.equalsIgnoreCase("SPAWNFIX"))
		{
			String groupData = infos[1];

			_perso.getCurMap().addStaticGroup(_perso.getCurCell().getId(), groupData);
			String str = "Le grouppe a ete fixe";
			//Sauvegarde DB de la modif
			if(World.database.getMonsterData().saveNewFixGroup(_perso.getCurMap().getId(),_perso.getCurCell().getId(), groupData))
				str += " et a ete sauvegarde dans la BDD";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
			return;
		}else
		if(command.equalsIgnoreCase("ADDNPC"))
		{
			int id = 0;
			try
			{
				id = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			if(id == 0 || World.data.getNpcTemplate(id) == null)
			{
				String str = "NpcID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Npc npc = _perso.getCurMap().addNpc(id, _perso.getCurCell().getId(), _perso.getOrientation());
			SocketManager.GAME_SEND_ADD_NPC_TO_MAP(_perso.getCurMap(), npc);
			String str = "Le PNJ a ete ajoute";
			if(_perso.getOrientation() == 0
					|| _perso.getOrientation() == 2
					|| _perso.getOrientation() == 4
					|| _perso.getOrientation() == 6)
						str += " mais est invisible (orientation diagonale invalide).";
			
			if(World.database.getNpcData().create(_perso.getCurMap().getId(), id, _perso.getCurCell().getId(), _perso.getOrientation()))
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
			else
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,"Erreur au moment de sauvegarder la position");
		}else
		if(command.equalsIgnoreCase("DELNPC"))
		{
			int id = 0;
			try
			{
				id = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			Npc npc = _perso.getCurMap().getNpcs().get(id);
			if(id == 0 || npc == null)
			{
				String str = "Npc GUID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			int exC = npc.getCell().getId();
			//on l'efface de la map
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(_perso.getCurMap(), id);
			if(_perso.getCurMap().getNpcs().containsKey(id))
				_perso.getCurMap().getNpcs().remove(id);
			if(_perso.getCurMap().getMobGroups().containsKey(id))
				_perso.getCurMap().getMobGroups().remove(id);
			
			String str = "Le PNJ a ete supprime";
			if(World.database.getNpcData().delete(_perso.getCurMap().getId(),exC))
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
			else
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,"Erreur au moment de sauvegarder la position");
		}else
		if(command.equalsIgnoreCase("DELTRIGGER"))
		{
			int cellID = -1;
			try
			{
				cellID = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			if(cellID == -1 || _perso.getCurMap().getCases().get(cellID) == null)
			{
				String str = "CellID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			
			_perso.getCurMap().getCases().get(cellID).clearOnCellAction();
			boolean success = World.database.getScriptedCellData().delete(_perso.getCurMap().getId(),cellID);
			String str = "";
			if(success)	str = "Le trigger a ete retire";
			else 		str = "Le trigger n'a pas ete retire";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("ADDTRIGGER"))
		{
			int actionID = -1;
			String args = "",cond = "";
			try
			{
				actionID = Integer.parseInt(infos[1]);
				args = infos[2];
				cond = infos[3];
			}catch(Exception e){};
			if(args.equals("") || actionID <= -3)
			{
				String str = "Valeur invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			
			_perso.getCurCell().addOnCellStopAction(actionID,args, cond);
			boolean success = World.database.getScriptedCellData().update(_perso.getCurMap().getId(),_perso.getCurCell().getId(),actionID,1,args,cond);
			String str = "";
			if(success)	str = "Le trigger a ete ajoute";
			else 		str = "Le trigger n'a pas ete ajoute";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("DELNPCITEM"))
		{
			if(_compte.getGmLvl() <3)return;
			int npcGUID = 0;
			int itmID = -1;
			try
			{
				npcGUID = Integer.parseInt(infos[1]);
				itmID = Integer.parseInt(infos[2]);
			}catch(Exception e){};
			NpcTemplate npc =  _perso.getCurMap().getNpcs().get(npcGUID).getTemplate();
			if(npcGUID == 0 || itmID == -1 || npc == null)
			{
				String str = "NpcGUID ou itmID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			
			
			String str = "";
			if(npc.removeObject(itmID))str = "L'objet a ete retire";
			else str = "L'objet n'a pas ete retire";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("ADDNPCITEM"))
		{
			if(_compte.getGmLvl() <3)return;
			int npcGUID = 0;
			int itmID = -1;
			try
			{
				npcGUID = Integer.parseInt(infos[1]);
				itmID = Integer.parseInt(infos[2]);
			}catch(Exception e){};
			NpcTemplate npc =  _perso.getCurMap().getNpcs().get(npcGUID).getTemplate();
			ObjTemplate item =  World.data.getObjTemplate(itmID);
			if(npcGUID == 0 || itmID == -1 || npc == null || item == null)
			{
				String str = "NpcGUID ou itmID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			
			
			String str = "";
			if(npc.addObject(item))str = "L'objet a ete rajoute";
			else str = "L'objet n'a pas ete rajoute";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("ADDMOUNTPARK"))
		{
			int size = -1;
			int owner = -2;
			int price = -1;
			try
			{
				size = Integer.parseInt(infos[1]);
				owner = Integer.parseInt(infos[2]);
				price = Integer.parseInt(infos[3]);
				if(price > 20000000)price = 20000000;
				if(price <0)price = 0;
			}catch(Exception e){};
			if(size == -1 || owner == -2 || price == -1 || _perso.getCurMap().getMountPark() != null)
			{
				String str = "Infos invalides ou map deja config.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			MountPark MP = new MountPark(owner, _perso.getCurMap(), _perso.getCurCell().getId(), size, "", -1, price);
			_perso.getCurMap().setMountPark(MP);
			World.database.getMountparkData().update(MP);
			String str = "L'enclos a ete config. avec succes";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else 
		if (command.equalsIgnoreCase("SEND"))
		{
			infos = msg.split(" ",2);
			SocketManager.send(_perso, infos[1]);
			return;
		}else
		if (command.equalsIgnoreCase("SHUTDOWN"))
		{
			int time = 30, OffOn = 0;
			try
			{
				OffOn = Integer.parseInt(infos[1]);
				time = Integer.parseInt(infos[2]);
			}catch(Exception e){};
			
			if(OffOn == 1 && _TimerStart)// demande de d�marer le reboot
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Un shutdown est deja programmer.");
			}else if(OffOn == 1 && !_TimerStart)
			{
				_timer = createTimer(time);
				_timer.start();
				_TimerStart = true;
				String timeMSG = "minutes";
				if(time <= 1)
				{
					timeMSG = "minute";
				}
				SocketManager.GAME_SEND_Im_PACKET_TO_ALL("115;"+time+" "+timeMSG);
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Shutdown lance.");
			}else if(OffOn == 0 && _TimerStart)
			{
				_timer.stop();
				_TimerStart = false;
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Shutdown arrete.");
			}else if(OffOn == 0 && !_TimerStart)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Aucun shutdown n'est lance.");
			}
		}else
		{
			this.commandGmTwo(command, infos, msg);
		}
	}
	
	public void commandGmFour(String command, String[] infos, String msg)
	{
		if(_compte.getGmLvl() < 4)
		{
			_compte.getGameClient().closeSocket();
			return;
		}
		
		if(command.equalsIgnoreCase("SETADMIN"))
		{
			int gmLvl = -100;
			try
			{
				gmLvl = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			if(gmLvl == -100)
			{
				String str = "Valeur incorrecte";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			Player target = _perso;
			if(infos.length > 2)//Si un nom de perso est sp�cifi�
			{
				target = World.data.getPersoByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
					return;
				}
			}
			target.getAccount().setGmLvl(gmLvl);
			World.database.getAccountData().update(target.getAccount());
			String str = "Le niveau GM du joueur a ete modifie";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
		}else
		if(command.equalsIgnoreCase("LOCK"))
		{
			byte LockValue = 1;//Accessible
			try
			{
				LockValue = Byte.parseByte(infos[1]);
			}catch(Exception e){};
			
			if(LockValue > 2) LockValue = 2;
			if(LockValue < 0) LockValue = 0;
			World.data.set_state(LockValue);
			if(LockValue == 1)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Serveur accessible.");
			}else if(LockValue == 0)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Serveur inaccessible.");
			}else if(LockValue == 2)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Serveur en sauvegarde.");
			}
		}else
		if(command.equalsIgnoreCase("BLOCK"))
		{
			byte GmAccess = 0;
			byte KickPlayer = 0;
			try
			{
				GmAccess = Byte.parseByte(infos[1]);
				KickPlayer = Byte.parseByte(infos[2]);
			}catch(Exception e){};
			
			World.data.setGmAccess(GmAccess);
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Serveur bloque au GmLevel : "+GmAccess);
			if(KickPlayer > 0)
			{
				for(Player z : World.data.getOnlinePersos()) 
				{
					if(z.getAccount().getGmLvl() < GmAccess)
						z.getAccount().getGameClient().closeSocket();
				}
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Les joueurs de GmLevel inferieur a "+GmAccess+" ont ete kicks.");
			}
		}else
		if(command.equalsIgnoreCase("BANIP"))
		{
			Player P = null;
			try
			{
				P = World.data.getPersoByName(infos[1]);
			}catch(Exception e){};
			if(P == null || !P.isOnline())
			{
				String str = "Le personnage n'a pas ete trouve.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
			
			if(!Constants.IPcompareToBanIP(P.getAccount().getCurIp()))
			{
				Constants.BAN_IP += ","+P.getAccount().getCurIp();
				if(World.database.getOtherData().addBannedIp(P.getAccount().getCurIp()))
				{
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "L'IP a ete banni.");
				}
				if(P.isOnline()){
					P.getAccount().getGameClient().kick();
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out, "Le joueur a ete kick.");
				}
			}else
			{
				String str = "L'IP existe deja.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_out,str);
				return;
			}
		}else
		{
			this.commandGmThree(command, infos, msg);
		}
	}
}