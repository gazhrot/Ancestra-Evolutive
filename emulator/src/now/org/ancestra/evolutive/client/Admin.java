package org.ancestra.evolutive.client;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.CryptManager;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.monster.MobGroup;
import org.ancestra.evolutive.entity.npc.Npc;
import org.ancestra.evolutive.entity.npc.NpcQuestion;
import org.ancestra.evolutive.entity.npc.NpcTemplate;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.enums.EmulatorInfos;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.job.JobStat;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.map.MountPark;
import org.ancestra.evolutive.object.ObjectSet;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.object.ObjectTemplate;
import org.ancestra.evolutive.tool.command.Command;
import org.ancestra.evolutive.tool.command.Parameter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Admin {
	private Account account;
	private Player player;
	private boolean _TimerStart = false;
	private Timer _timer;
	
	/*
	 * String msg = StringUtils.join(args, " ");
	 */
	
	public Admin(Player player) {
		this.account = player.getAccount();
		this.player = player;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public int getGmLvl() {
		return account.getGmLvl();
	}
	
	public static Map<String, Command<Admin>> initialize() {
		Map<String, Command<Admin>> commands = new HashMap<>();
		
		/** GM LEVEL N°1 **/
		
		Command<Admin> command = new Command<Admin>("INFOS", "Affiche les informations concernant le server.", null, 1) {

			@Override
			public void action(Admin t, String[] args) {
				String msg =	"===========\n"+EmulatorInfos.SOFT_NAME.toString()
					+			"\nUptime: "+ EmulatorInfos.uptime() +"\n"
					+			"Joueurs en lignes: "+Server.config.getGameServer().getPlayerNumber()+"\n"
					+			"Record de connexion: "+Server.config.getGameServer().getMaxPlayer()+"\n"
					+			"===========";
				t.sendText(msg);
			}
		};
		
		commands.put("INFOS", command);
		
		command = new Command<Admin>("WHO", "Affiche tout les joueurs en ligne.", null, 1) {

			@Override
			public void action(Admin t, String[] args) {
				StringBuilder msg = new StringBuilder("Liste des joueurs en ligne :\n");	
				for(GameClient client: Server.config.getGameServer().getClients().values()) {
					Player player = client.getPlayer();
					
					if(player == null)
						continue;
					
					msg.append(player.getName()).append(" (").append(player.getId()).append(") - ").append(player.getClasse().toString());
					msg.append(player.getSex() == 0 ? " - M - " : " - F - ").append("Lvl ").append(player.getLevel()).append(" - ");
					msg.append(player.getMap().getId() + "," + player.getCell().getId()).append(player.getFight() == null ? "" : " - En combat");
					msg.append("\n");
				}
				t.sendText(msg.toString());
			}
		};
		
		commands.put("WHO", command);
		
		command = new Command<Admin>("REFRESHMOBS", "Raffraichis les monstres sur la carte où vous êtes.", null, 1) {

			@Override
			public void action(Admin t, String[] args) {
				t.getPlayer().getMap().refreshSpawns();
				t.sendText("Les monstres ont été rechargé avec succès !");
			}
		};
		
		commands.put("REFRESHMOBS", command);
		
		command = new Command<Admin>("MAPINFOS", "Affiche les pnjs et monstres présent sur la carte où vous êtes.", null, 1) {

			@Override
			public void action(Admin t, String[] args) {
				t.sendText("Liste des pnjs de la carte :\n");
				Maps map = t.getPlayer().getMap();
				
				for(Entry<Integer, Npc> entry : map.getNpcs().entrySet())
					t.sendText("Id: " + entry.getKey() + " | T:" + entry.getValue().getTemplate().getId() + " | Cell : " + entry.getValue().getCell().getId() + " | Question : " + entry.getValue().getTemplate().getInitQuestion() + "\n");

				t.sendText("Liste des groupes de monstres :\n");
				
				for(Entry<Integer, MobGroup> entry : map.getMobGroups().entrySet())
					t.sendText("Id: " + entry.getKey() + " | Cell : " + entry.getValue().getCell().getId() + " | " + entry.getValue().getAlignement() + " | Taille : " + entry.getValue().getMobs().size());
			}
		};
		
		commands.put("MAPINFOS", command);
		
		command = new Command<Admin>("GUILD", null, "CREATE", 1) {

			@Override
			public void action(Admin t, String[] args) {
				
			}
		};
		
		command.addParameter(new Parameter<Admin>("CREATE", "Ouvre le panneaux de création de guilde.", "PLAYER*") {

			@Override
			public void action(Admin t, String[] args) {
				Player player = t.getPlayer();

				if(args.length > 0)
					player = World.data.getPlayerByName(args[0]);
				
				if(player == null) {
					t.sendText("Le joueur en question n'existe pas.");
					return;
				}
				
				if(!player.isOnline()) {
					t.sendText("Le joueur en question n'est pas en ligne.");
					return;
				}
				
				if(player.getGuild() != null || player.getGuildMember() != null) {
					t.sendText("Le joueur en question appartient déjà à une guilde.");
					return;
				}
				
				SocketManager.GAME_SEND_gn_PACKET(player);
				t.sendText("Vous venez d'ouvrir le panneau de création de guilde au joueur " + player.getName() + " !");
			}
		
		});
		
		commands.put("GUILD", command);
		
		command = new Command<Admin>("TP", "Permet la téléportation d'un joueur à une carte et une cellule indiqué.", "MAP,CELL,PLAYER*", 1) {

			@Override
			public void action(Admin t, String[] args) {
				int map = -1;
				int cell = -1;
				
				try {
					map = Integer.parseInt(args[0]);
					cell = Integer.parseInt(args[1]);
				} catch(Exception e) {}
				
				if(map == -1 || cell == -1 || World.data.getMap(map) == null) {
					t.sendText("Les paramètres indiqués sont invalides !");
					return;
				}
				
				if(World.data.getMap(map).getCases().get(cell) == null)	{
					t.sendText("Les paramètres indiqués sont invalides !");
					return;
				}
				
				Player player = t.getPlayer();
				
				if(args.length > 2) {
					player = World.data.getPlayerByName(args[2]);
					if(player == null  || player.getFight() != null) {
						t.sendText("Le joueur en question n'existe pas ou est actuellement en combat.");
						return;
					}
				}
				player.setPosition(map, cell);
				t.sendText("Le joueur " +player.getName() + " a été téléporter avec succès !");			
			}
		};
		
		commands.put("TP", command);
		
		command = new Command<Admin>("ANNOUNCE", "Permet d'afficher une annonce à tout les joueurs du server.", "MESSAGE", 1) {

			@Override
			public void action(Admin t, String[] args) {
				String msg = StringUtils.join(args, " ");
				
				if(msg.isEmpty() || msg.equals(" "))
					return;
				
				String prefix = "["+t.getPlayer().getName()+"] : ";
				SocketManager.GAME_SEND_MESSAGE_TO_ALL(prefix + msg, Server.config.getMotdColor());
			}
		};
		
		commands.put("ANNOUNCE", command);
		
		command = new Command<Admin>("GONAME", "Permet la téléportation d'un joueur à un autre.", "TARGET,ToPLAYER*", 1) {

			@Override
			public void action(Admin t, String[] args) {
				Player target = World.data.getPlayerByName(args[0]);
				
				if(target == null) {
					t.sendText("Le joueur " + args[0] + " n'existe pas.");
					return;
				}
				if(target.getFight() != null) {
					t.sendText("Le joueur " + target.getName() + " est en combat.");
					return;
				}
				
				Player player = t.getPlayer();
				if(args.length > 1) {
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur " + args[1] + " n'existe pas.");
						return;
					}
				}
				
				if(player.isOnline()) {
					target.setPosition(player.getMap().getId(), player.getCell().getId());
					t.sendText("Le joueur " + target.getName() + " a été téléporter avec succès.");
				} else {
					t.sendText("Le joueur " + player.getName() + " n'est pas en ligne.");
				}
			}
		};
		
		commands.put("GONAME", command);
		
		command = new Command<Admin>("TOOGLEAGGRO", "Active ou désactive la possibilité d'aggresion du joueur venu des autres joueurs.", "PLAYER*", 1) {

			@Override
			public void action(Admin t, String[] args) {
				Player player = t.getPlayer();
				
				if(args.length > 0) {
					player = World.data.getPlayerByName(args[0]);
					if(player == null) {
						t.sendText("Le joueur " + args[1] + " n'existe pas.");
						return;
					}
				}
				
				if(player == null) {
					t.sendText("Le joueur " + args[1] + " n'existe pas.");
					return;
				}
				
				player.setCanAggro(!player.isCanAggro());
	
				if(!player.isOnline()) {
					t.sendText("Le joueur " + player.getName() + " n'est pas en ligne.");
				} else {
					t.sendText("Le joueur " + player.getName() + (player.isCanAggro() ? "ne peut être aggresser." : " peut être aggresser."));
				}
			}
		};
		
		commands.put("TOOGLEAGGRO", command);
		
		/** GM LEVEL N°2 **/
		
		command = new Command<Admin>("FIGHTPOS", null, "SHOW|ADD|DEL|DELALL", 2) {

			@Override
			public void action(Admin t, String[] args) {
				t.sendText("Paramètre non indiqué : SHOW, ADD, DEL, DELALL");
			}
		};
		
		command.addParameter(new Parameter<Admin>("SHOW", "Affiche la totalité des positions de combat de la carte.", null) {

			@Override
			public void action(Admin t, String[] args) {
				StringBuilder msg = new StringBuilder("Liste des cellules de combat :\n");
				String places = t.getPlayer().getMap().getPlaces();
				
				if(places.indexOf('|') == -1 || places.length() < 2) {
					t.sendText("Les places n'ont pas été définie.");
					return;
				}
				
				String team0 = "", team1 = "";
				
				try {
					team0 = places.split("\\|")[0];
				} catch(Exception e) {}
				try {
					team1 = places.split("\\|")[1];
				} catch(Exception e) {}
				
				msg.append("Team 0 :\n");
				for(int i = 0; i <= team0.length() - 2; i += 2)
					msg.append(CryptManager.cellCode_To_ID(team0.substring(i, i + 2))).append(" , ");
				
				msg.append("\nTeam 1 :\n");
				for(int i = 0; i <= team1.length() - 2; i += 2)
					msg.append(CryptManager.cellCode_To_ID(team1.substring(i, i + 2))).append(" , ");
				
				t.sendText(msg.toString());
			}
		});
		
		command.addParameter(new Parameter<Admin>("ADD", "Ajoute une cellule de combat sur la case où vous êtes ou une case indiqué.", "TEAM") {

			@Override
			public void action(Admin t, String[] args) {
				int team = -1;
				int cell = -1;
				try {
					team = Integer.parseInt(args[0]);
					cell = Integer.parseInt(args[1]);
				} catch(Exception e) {}
				
				if(team < 0 || team > 1) {
					t.sendText("La valeur de l'équipe (0 ou 1) est inccorecte.");
					return;
				}
				
				if(cell < 0 || t.getPlayer().getMap().getCases().get(cell) == null)
					cell = t.getPlayer().getCell().getId();
				if(!t.getPlayer().getMap().getCases().get(cell).isWalkable(true))
					cell = t.getPlayer().getCell().getId();
				
				boolean already = false;
				String team0 = "",team1 = "", places = t.getPlayer().getMap().getPlaces();
				
				try	{
					team0 = places.split("\\|")[0];
				} catch(Exception e) {}
				try	{
					team1 = places.split("\\|")[1];
				} catch(Exception e) {}
				
				for(int a = 0; a <= team0.length() - 2; a += 2)
					if(cell == CryptManager.cellCode_To_ID(team0.substring(a, a + 2)))
						already = true;
				for(int a = 0; a <= team1.length() - 2; a += 2)
					if(cell == CryptManager.cellCode_To_ID(team1.substring(a, a + 2)))
						already = true;
				if(already) {
					t.sendText("La case comporte déjà une cellule de combat.");
					return;
				}
				
				if(team == 0)
					team0 += CryptManager.cellID_To_Code(cell);
				else if(team == 1)
					team1 += CryptManager.cellID_To_Code(cell);
								
				t.getPlayer().getMap().setPlaces(team0 + "|" + team1);
				
				if(!World.database.getMapData().update(t.getPlayer().getMap()))
					return;
				
				t.sendText("Une cellule de combat a été ajouter sur la case " + cell + ".");	
			}
			
		});

		command.addParameter(new Parameter<Admin>("DEL", "Supprime une cellule de combat sur la case où vous êtes ou une case indiqué.", "TEAM") {

			@Override
			public void action(Admin t, String[] args) {
				int cell = -1;
				
				try	{
					cell = Integer.parseInt(args[1]);
				} catch(Exception e) {}
				
				if(cell < 0 || t.getPlayer().getMap().getCases().get(cell) == null)
					cell = t.getPlayer().getCell().getId();
		
				String[] places = t.getPlayer().getMap().getPlaces().split("\\|");
				String newPlaces = "", team0 = "", team1 = "";
				
				try	{
					team0 = places[0];
				} catch(Exception e) {}
				try	{
					team1 = places[1];
				} catch(Exception e) {}
				
				for(int a = 0; a <= team0.length() - 2; a += 2)	{
					String c = places[0].substring(a, a + 2);
					if(cell == CryptManager.cellCode_To_ID(c))
						continue;
					newPlaces += c;
				}
				
				newPlaces += "|";
				
				for(int a = 0; a <= team1.length() - 2; a += 2) {
					String c = places[1].substring(a,a+2);
					if(cell == CryptManager.cellCode_To_ID(c))
						continue;
					newPlaces += c;
				}
				
				t.getPlayer().getMap().setPlaces(newPlaces);
				
				if(!World.database.getMapData().update(t.getPlayer().getMap()))
					return;

				t.sendText("Une cellule de combat a été supprimer sur la case " + cell + ".");	
			}
			
		});
		
		command.addParameter(new Parameter<Admin>("DELALL", "Supprime toute les cellules de combat sur la carte où vous êtes.", null) {

			@Override
			public void action(Admin t, String[] args) {
				t.getPlayer().getMap().setPlaces("|");
				
				if(!World.database.getMapData().update(t.getPlayer().getMap()))
					return;

				t.sendText("Toute les cellules de combat ont été supprimer avec succès.");	
			}

		});
		
		commands.put("FIGHTPOS", command);
		
		command = new Command<Admin>("aaaaaaaaaaaaaaa", "", null, 1) {

			@Override
			public void action(Admin t, String[] args) {
				
			}
		};
		
		commands.put("aaaaaaaaaaaaaaaaa", command);
	
		
		return commands;
	}
	
	public void sendText(String msg) {
		this.account.send("BAT2" + msg);
	}
	
	private Timer createTimer(final int times) {
	    ActionListener action = new ActionListener() {
	    	private int time = times;
	        @Override
			public void actionPerformed(ActionEvent event) {
	        	this.time = this.time - 1;
	        	if(this.time == 1)
	        		SocketManager.GAME_SEND_Im_PACKET_TO_ALL("115;" + this.time + " minute");
	        	else
		        	SocketManager.GAME_SEND_Im_PACKET_TO_ALL("115;" + this.time + " minutes");

	        	if(this.time <= 0) {
	        		for(Player perso : World.data.getOnlinePersos())
	        			perso.getAccount().getGameClient().kick();
	    			System.exit(0);
	        	}
	        }
	    };
	    return new Timer(60000, action);//60000
	}
	
	/*public void commandGmOne(String command, String[] infos, String msg)
	{
		if(this.account.getGmLvl() < 1)
		{
			this.account.getGameClient().closeSocket();
			return;
		}
		if(command.equalsIgnoreCase("SHOWFIGHTPOS"))
		{
			String mess = "Liste des StartCell [teamID][cellID]:";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), mess);
			String places = t.getPlayer().getMap().getPlaces();
			if(places.indexOf('|') == -1 || places.length() <2)
			{
				mess = "Les places n'ont pas ete definies";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), mess);
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
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), mess);
			mess = "Team 1:\n";
			for(int a = 0;a <= team1.length()-2; a+=2)
			{
				String code = team1.substring(a,a+2);
				mess += CryptManager.cellCode_To_ID(code)+" , ";
			}
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), mess);
			return;
		}		
	}
	
	public void commandGmTwo(String command, String[] infos, String msg)
	{
		if(this.account.getGmLvl() < 2)
		{
			this.account.getGameClient().closeSocket();
			return;
		}
		
		if(command.equalsIgnoreCase("MUTE"))
		{
			Player perso = t.getPlayer();
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
			
			perso = World.data.getPlayerByName(name);
			if(perso == null || time < 0)
			{
				String mess = "Le personnage n'existe pas ou la duree est invalide.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
				return;
			}
			String mess = "Vous avez mute "+perso.getName()+" pour "+time+" secondes";
			if(perso.getAccount() == null)
			{
				mess = "(Le personnage "+perso.getName()+" n'etait pas connecte)";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
				return;
			}
			perso.getAccount().mute(true,time);
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
			
			if(!perso.isOnline())
			{
				mess = "(Le personnage "+perso.getName()+" n'etait pas connecte)";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
			}else
			{
				SocketManager.GAME_SEND_Im_PACKET(perso, "1124;"+time);
			}
			return;
		}else
		if(command.equalsIgnoreCase("UNMUTE"))
		{
			Player perso = t.getPlayer();
			
			String name = null;
			try
			{
				name = infos[1];
			}catch(Exception e){};
			
			perso = World.data.getPlayerByName(name);
			if(perso == null)
			{
				String mess = "Le personnage n'existe pas.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
				return;
			}
			
			perso.getAccount().mute(false,0);
			String mess = "Vous avez unmute "+perso.getName();
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
			
			if(!perso.isOnline())
			{
				mess = "(Le personnage "+perso.getName()+" n'etait pas connecte)";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
			}
		}else
		if(command.equalsIgnoreCase("KICK"))
		{
			Player perso = t.getPlayer();
			String name = null;
			try
			{
				name = infos[1];
			}catch(Exception e){};
			perso = World.data.getPlayerByName(name);
			if(perso == null)
			{
				String mess = "Le personnage n'existe pas.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
				return;
			}
			if(perso.isOnline())
			{
				perso.getAccount().getGameClient().kick();
				String mess = "Vous avez kick "+perso.getName();
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
			}
			else
			{
				String mess = "Le personnage "+perso.getName()+" n'est pas connecte";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			Player target = t.getPlayer();
			if(infos.length > 2)//Si un nom de perso est spï¿½cifiï¿½
			{
				target = World.data.getPlayerByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
					return;
				}
			}
			target.addSpellPoint(pts);
			SocketManager.GAME_SEND_STATS_PACKET(target);
			String str = "Le nombre de point de sort a ete modifiee";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			Player target = t.getPlayer();
			if(infos.length > 2)//Si un nom de perso est spï¿½cifiï¿½
			{
				target = World.data.getPlayerByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
					return;
				}
			}
			
			target.learnSpell(spell, 1, true, true, true);
			
			String str = "Le sort a ete appris";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
		}else
		if(command.equalsIgnoreCase("SETALIGN"))
		{
			byte align = -1;
			try
			{
				align = Byte.parseByte(infos[1]);
			}catch(Exception e){};
			if(align < Alignement.NEUTRE.getId() || align > Alignement.MERCENAIRE.getId())
			{
				String str = "Valeur invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			Player target = t.getPlayer();
			if(infos.length > 2)//Si un nom de perso est spï¿½cifiï¿½
			{
				target = World.data.getPlayerByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
					return;
				}
			}
			
			target.modifAlignement(align);
			
			String str = "L'alignement du joueur a ete modifie";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			str = "Liste des reponses pour la question "+id+": "+Q.getAnswer();
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
			return;
		}else
		if(command.equalsIgnoreCase("HONOR"))
		{
			int honor = 0;
			try
			{
				honor = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			Player target = t.getPlayer();
			if(infos.length > 2)//Si un nom de perso est spï¿½cifiï¿½
			{
				target = World.data.getPlayerByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
					return;
				}
			}
			String str = "Vous avez ajouter "+honor+" honneur a "+target.getName();
			if(target.getAlign() == Alignement.NEUTRE)
			{
				str = "Le joueur est neutre ...";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			target.addHonor(honor);
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
			
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
				Player target = t.getPlayer();
			if(infos.length > 3)//Si un nom de perso est spï¿½cifiï¿½
			{
				target = World.data.getPlayerByName(infos[3]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
					return;
				}
			}
			JobStat SM = target.getMetierByID(job);
			if(SM== null)
			{
				String str = "Le joueur ne connais pas le metier demande";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
				
			SM.addXp(target, xp);
			
			String str = "Le metier a ete experimenter";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			Player target = t.getPlayer();
			if(infos.length > 2)//Si un nom de perso est spï¿½cifiï¿½
			{
				target = World.data.getPlayerByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
					return;
				}
			}
			
			target.learnJob(World.data.getMetier(job));
			
			String str = "Le metier a ete appris";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			Player target = t.getPlayer();
			if(infos.length > 2)//Si un nom de perso est spï¿½cifiï¿½
			{
				target = World.data.getPlayerByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
					return;
				}
			}
			target.addCapital(pts);
			SocketManager.GAME_SEND_STATS_PACKET(target);
			String str = "Le capital a ete modifiee";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			Player target = t.getPlayer();
			if(infos.length > 2)//Si un nom de perso est spï¿½cifiï¿½
			{
				target = World.data.getPlayerByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
					return;
				}
			}
			target.setSize(size);
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(target.getMap(), target.getId());
			SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(target.getMap(), target);
			String str = "La taille du joueur a ete modifiee";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			Player target = t.getPlayer();
			if(infos.length > 2)//Si un nom de perso est spï¿½cifiï¿½
			{
				target = World.data.getPlayerByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
					return;
				}
			}
			target.setGfx(morphID);
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(target.getMap(), target.getId());
			SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(target.getMap(), target);
			String str = "Le joueur a ete transforme";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
		}if(command.equalsIgnoreCase("MOVENPC"))
		{
			int id = 0;
			try
			{
				id = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			Npc npc = t.getPlayer().getMap().getNpcs().get(id);
			if(id == 0 || npc == null)
			{
				String str = "Npc GUID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			int exC = npc.getCell().getId();
			//on l'efface de la map
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(t.getPlayer().getMap(), id);
			//on change sa position/orientation
			npc.setCell(t.getPlayer().getCell());
			npc.setOrientation((byte)t.getPlayer().getOrientation());
			//on envoie la modif
			SocketManager.GAME_SEND_ADD_NPC_TO_MAP(t.getPlayer().getMap(),npc);
			String str = "Le PNJ a ete deplace";
			if(t.getPlayer().getOrientation() == 0
			|| t.getPlayer().getOrientation() == 2
			|| t.getPlayer().getOrientation() == 4
			|| t.getPlayer().getOrientation() == 6)
				str += " mais est devenu invisible (orientation diagonale invalide).";
			if(World.database.getNpcData().delete(t.getPlayer().getMap().getId(),exC)
			&& World.database.getNpcData().create(t.getPlayer().getMap().getId(),npc.getTemplate().getId(),t.getPlayer().getCell().getId(),t.getPlayer().getOrientation()))
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
			else
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),"Erreur au moment de sauvegarder la position");
		}else	
		if(command.equalsIgnoreCase("ITEMSET"))
		{
			int tID = 0;
			try
			{
				tID = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			ObjectSet IS = World.data.getItemSet(tID);
			if(tID == 0 || IS == null)
			{
				String mess = "La panoplie "+tID+" n'existe pas ";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
				return;
			}
			boolean useMax = false;
			if(infos.length == 3)useMax = infos[2].equals("MAX");//Si un jet est spï¿½cifiï¿½

			
			for(ObjectTemplate t : IS.getItemTemplates())
			{
				Object obj = t.createNewItem(1,useMax);
				if(t.getPlayer().addObject(obj, true))//Si le joueur n'avait pas d'item similaire
					World.data.addObject(obj,true);
			}
			String str = "Creation de la panoplie "+tID+" reussie";
			if(useMax) str += " avec des stats maximums";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
		}else
		if(command.equalsIgnoreCase("LEVEL"))
		{
			int count = 0;
			try
			{
				count = Integer.parseInt(infos[1]);
				if(count < 1)	count = 1;
				if(count > World.data.getExpLevelSize())	count = World.data.getExpLevelSize();
				Player perso = t.getPlayer();
				if(infos.length == 3)//Si le nom du perso est spï¿½cifiï¿½
				{
					String name = infos[2];
					perso = World.data.getPlayerByName(name);
					if(perso == null)
						perso = t.getPlayer();
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
			}catch(Exception e)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Valeur incorecte");
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
				Player perso = t.getPlayer();
				if(infos.length == 3)//Si le nom du perso est spï¿½cifiï¿½
				{
					String name = infos[2];
					perso = World.data.getPlayerByName(name);
					if(perso == null)
						perso = t.getPlayer();
				}
				int newPDV = perso.getMaxPdv() * count / 100;
				perso.setPdv(newPDV);
				if(perso.isOnline())
					SocketManager.GAME_SEND_STATS_PACKET(perso);
				String mess = "Vous avez fixer le pourcentage de pdv de "+perso.getName()+" a "+count;
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
			}catch(Exception e)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Valeur incorecte");
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Valeur incorecte");
				return;
			};
			if(count == 0)return;
			
			Player perso = t.getPlayer();
			if(infos.length == 3)//Si le nom du perso est spï¿½cifiï¿½
			{
				String name = infos[2];
				perso = World.data.getPlayerByName(name);
				if(perso == null)
					perso = t.getPlayer();
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
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
		}else
		if(command.equalsIgnoreCase("ITEM") || command.equalsIgnoreCase("!getitem"))
		{
			boolean isOffiCmd = command.equalsIgnoreCase("!getitem");
			if(this.account.getGmLvl() < 2)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Vous n'avez pas le niveau MJ requis");
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
				return;
			}
			int qua = 1;
			if(infos.length == 3)//Si une quantitï¿½ est spï¿½cifiï¿½e
			{
				try
				{
					qua = Integer.parseInt(infos[2]);
				}catch(Exception e){};
			}
			boolean useMax = false;
			if(infos.length == 4 && !isOffiCmd)//Si un jet est spï¿½cifiï¿½
			{
				if(infos[3].equalsIgnoreCase("MAX"))useMax = true;
			}
			ObjectTemplate t = World.data.getObjectTemplate(tID);
			if(t == null)
			{
				String mess = "Le template "+tID+" n'existe pas ";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
				return;
			}
			if(qua <1)qua =1;
			Object obj = t.createNewItem(qua,useMax);
			if(t.getPlayer().addObject(obj, true))//Si le joueur n'avait pas d'item similaire
				World.data.addObject(obj,true);
			String str = "Creation de l'item "+tID+" reussie";
			if(useMax) str += " avec des stats maximums";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
			SocketManager.GAME_SEND_Ow_PACKET(t.getPlayer());
		}else 
		if (command.equalsIgnoreCase("SPAWN"))
		{			
			String Mob = null;
			try
			{
				Mob = infos[1];
			}catch(Exception e){};
            if(Mob == null) return;
			t.getPlayer().getMap().spawnGroupOnCommand(t.getPlayer().getCell().getId(), Mob);
		}else
		if (command.equalsIgnoreCase("TITLE"))
		{
			Player target = null; 
			byte TitleID = 0;
			try
			{
				target = World.data.getPlayerByName(infos[1]);
				TitleID = Byte.parseByte(infos[2]);
			}catch(Exception e){};
			
			if(target == null)
			{
				String str = "Le personnage n'a pas ete trouve";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			
			target.setTitle(TitleID);
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Titre mis en place.");
			World.database.getCharacterData().update(target);
			if(target.getFight() == null) SocketManager.GAME_SEND_ALTER_GM_PACKET(target.getMap(), target);
		}else
		{
			this.commandGmOne(command, infos, msg);
		}
	}
	
	public void commandGmThree(String command, String[] infos, String msg)
	{
		if(this.account.getGmLvl() < 3)
		{
			this.account.getGameClient().closeSocket();
			return;
		}
		
		if(command.equalsIgnoreCase("EXIT"))
		{
			System.exit(0);
		}else
		if(command.equalsIgnoreCase("SAVE") && !Server.config.isSaving())
		{
			World.data.saveData(t.getPlayer().getId());
			String mess = "Sauvegarde lancee!";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), mess);
			return;
		}else
		if(command.equalsIgnoreCase("BAN"))
		{
			Player P = World.data.getPlayerByName(infos[1]);
			if(P == null)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Personnage non trouve");
				return;
			}
			if(P.getAccount() == null)World.database.getAccountData().load(P.getAccount().getUUID());
			if(P.getAccount() == null)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Erreur");
				return;
			}
			P.getAccount().setBanned(true);
			World.database.getAccountData().update(P.getAccount());
			if(P.getAccount().getGameClient() != null)P.getAccount().getGameClient().kick();
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Vous avez banni "+P.getName());
			return;
		}else
		if(command.equalsIgnoreCase("UNBAN"))
		{
			Player P = World.data.getPlayerByName(infos[1]);
			if(P == null)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Personnage non trouve");
				return;
			}
			if(P.getAccount() == null)World.database.getAccountData().load(P.getAccount().getUUID());
			if(P.getAccount() == null)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Erreur");
				return;
			}
			P.getAccount().setBanned(false);
			World.database.getAccountData().update(P.getAccount());
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Vous avez debanni "+P.getName());
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			String mess = "Le nombre de groupe a ete fixe";
			t.getPlayer().getMap().setMaxGroup(id);
			boolean ok = World.database.getMapData().update(t.getPlayer().getMap());
			if(ok)mess += " et a ete sauvegarder a la BDD";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),mess);
		}else
		if(command.equalsIgnoreCase("SPAWNFIX"))
		{
			String groupData = infos[1];

			t.getPlayer().getMap().addStaticGroup(t.getPlayer().getCell().getId(), groupData);
			String str = "Le grouppe a ete fixe";
			//Sauvegarde DB de la modif
			if(World.database.getMonsterData().saveNewFixGroup(t.getPlayer().getMap().getId(),t.getPlayer().getCell().getId(), groupData))
				str += " et a ete sauvegarde dans la BDD";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			Npc npc = t.getPlayer().getMap().addNpc(id, t.getPlayer().getCell().getId(), t.getPlayer().getOrientation());
			SocketManager.GAME_SEND_ADD_NPC_TO_MAP(t.getPlayer().getMap(), npc);
			String str = "Le PNJ a ete ajoute";
			if(t.getPlayer().getOrientation() == 0
					|| t.getPlayer().getOrientation() == 2
					|| t.getPlayer().getOrientation() == 4
					|| t.getPlayer().getOrientation() == 6)
						str += " mais est invisible (orientation diagonale invalide).";
			
			if(World.database.getNpcData().create(t.getPlayer().getMap().getId(), id, t.getPlayer().getCell().getId(), t.getPlayer().getOrientation()))
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
			else
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),"Erreur au moment de sauvegarder la position");
		}else
		if(command.equalsIgnoreCase("DELNPC"))
		{
			int id = 0;
			try
			{
				id = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			Npc npc = t.getPlayer().getMap().getNpcs().get(id);
			if(id == 0 || npc == null)
			{
				String str = "Npc GUID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			int exC = npc.getCell().getId();
			//on l'efface de la map
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(t.getPlayer().getMap(), id);
			if(t.getPlayer().getMap().getNpcs().containsKey(id))
				t.getPlayer().getMap().getNpcs().remove(id);
			if(t.getPlayer().getMap().getMobGroups().containsKey(id))
				t.getPlayer().getMap().getMobGroups().remove(id);
			
			String str = "Le PNJ a ete supprime";
			if(World.database.getNpcData().delete(t.getPlayer().getMap().getId(),exC))
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
			else
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),"Erreur au moment de sauvegarder la position");
		}else
		if(command.equalsIgnoreCase("DELTRIGGER"))
		{
			int cellID = -1;
			try
			{
				cellID = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			if(cellID == -1 || t.getPlayer().getMap().getCases().get(cellID) == null)
			{
				String str = "CellID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			
			t.getPlayer().getMap().getCases().get(cellID).clearOnCellAction();
			boolean success = World.database.getScriptedCellData().delete(t.getPlayer().getMap().getId(),cellID);
			String str = "";
			if(success)	str = "Le trigger a ete retire";
			else 		str = "Le trigger n'a pas ete retire";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			
			t.getPlayer().getCell().addOnCellStopAction(actionID,args, cond);
			boolean success = World.database.getScriptedCellData().update(t.getPlayer().getMap().getId(),t.getPlayer().getCell().getId(),actionID,1,args,cond);
			String str = "";
			if(success)	str = "Le trigger a ete ajoute";
			else 		str = "Le trigger n'a pas ete ajoute";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
		}else
		if(command.equalsIgnoreCase("DELNPCITEM"))
		{
			if(this.account.getGmLvl() <3)return;
			int npcGUID = 0;
			int itmID = -1;
			try
			{
				npcGUID = Integer.parseInt(infos[1]);
				itmID = Integer.parseInt(infos[2]);
			}catch(Exception e){};
			NpcTemplate npc =  t.getPlayer().getMap().getNpcs().get(npcGUID).getTemplate();
			if(npcGUID == 0 || itmID == -1 || npc == null)
			{
				String str = "NpcGUID ou itmID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			
			
			String str = "";
			if(npc.removeObject(itmID))str = "L'objet a ete retire";
			else str = "L'objet n'a pas ete retire";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
		}else
		if(command.equalsIgnoreCase("ADDNPCITEM"))
		{
			if(this.account.getGmLvl() <3)return;
			int npcGUID = 0;
			int itmID = -1;
			try
			{
				npcGUID = Integer.parseInt(infos[1]);
				itmID = Integer.parseInt(infos[2]);
			}catch(Exception e){};
			NpcTemplate npc =  t.getPlayer().getMap().getNpcs().get(npcGUID).getTemplate();
			ObjectTemplate item =  World.data.getObjectTemplate(itmID);
			if(npcGUID == 0 || itmID == -1 || npc == null || item == null)
			{
				String str = "NpcGUID ou itmID invalide";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			
			
			String str = "";
			if(npc.addObject(item))str = "L'objet a ete rajoute";
			else str = "L'objet n'a pas ete rajoute";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
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
			if(size == -1 || owner == -2 || price == -1 || t.getPlayer().getMap().getMountPark() != null)
			{
				String str = "Infos invalides ou map deja config.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			MountPark MP = new MountPark(owner, t.getPlayer().getMap(), t.getPlayer().getCell().getId(), size, "", -1, price);
			t.getPlayer().getMap().setMountPark(MP);
			World.database.getMountparkData().update(MP);
			String str = "L'enclos a ete config. avec succes";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
		}else 
		if (command.equalsIgnoreCase("SEND"))
		{
			
			SocketManager.GAME_SEND_STATS_PACKET(t.getPlayer());
			infos = msg.split(" ",2);
			SocketManager.send(t.getPlayer(), infos[1]);
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
			
			if(OffOn == 1 && _TimerStart)// demande de dï¿½marer le reboot
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Un shutdown est deja programmer.");
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Shutdown lance.");
			}else if(OffOn == 0 && _TimerStart)
			{
				_timer.stop();
				_TimerStart = false;
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Shutdown arrete.");
			}else if(OffOn == 0 && !_TimerStart)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Aucun shutdown n'est lance.");
			}
		}else
		{
			this.commandGmTwo(command, infos, msg);
		}
	}
	
	public void commandGmFour(String command, String[] infos, String msg)
	{
		if(this.account.getGmLvl() < 4)
		{
			this.account.getGameClient().closeSocket();
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			Player target = t.getPlayer();
			if(infos.length > 2)//Si un nom de perso est spï¿½cifiï¿½
			{
				target = World.data.getPlayerByName(infos[2]);
				if(target == null)
				{
					String str = "Le personnage n'a pas ete trouve";
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
					return;
				}
			}
			target.getAccount().setGmLvl(gmLvl);
			World.database.getAccountData().update(target.getAccount());
			String str = "Le niveau GM du joueur a ete modifie";
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
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
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Serveur accessible.");
			}else if(LockValue == 0)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Serveur inaccessible.");
			}else if(LockValue == 2)
			{
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Serveur en sauvegarde.");
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
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Serveur bloque au GmLevel : "+GmAccess);
			if(KickPlayer > 0)
			{
				for(Player z : World.data.getOnlinePersos()) 
				{
					if(z.getAccount().getGmLvl() < GmAccess)
						z.getAccount().getGameClient().closeSocket();
				}
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Les joueurs de GmLevel inferieur a "+GmAccess+" ont ete kicks.");
			}
		}else
		if(command.equalsIgnoreCase("BANIP"))
		{
			Player P = null;
			try
			{
				P = World.data.getPlayerByName(infos[1]);
			}catch(Exception e){};
			if(P == null || !P.isOnline())
			{
				String str = "Le personnage n'a pas ete trouve.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
			
			if(!Constants.IPcompareToBanIP(P.getAccount().getCurIp()))
			{
				Constants.BAN_IP += ","+P.getAccount().getCurIp();
				if(World.database.getOtherData().addBannedIp(P.getAccount().getCurIp()))
				{
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "L'IP a ete banni.");
				}
				if(P.isOnline()){
					P.getAccount().getGameClient().kick();
					SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(), "Le joueur a ete kick.");
				}
			}else
			{
				String str = "L'IP existe deja.";
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(this.account.getGameClient(),str);
				return;
			}
		}else
		{
			this.commandGmThree(command, infos, msg);
		}
	}*/
}