package org.ancestra.evolutive.client;

import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.CryptManager;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.monster.MobGroup;
import org.ancestra.evolutive.entity.npc.Npc;
import org.ancestra.evolutive.entity.npc.NpcTemplate;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.enums.EmulatorInfos;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.job.JobStat;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.object.ObjectPosition;
import org.ancestra.evolutive.object.ObjectSet;
import org.ancestra.evolutive.object.ObjectTemplate;
import org.ancestra.evolutive.object.Object;

import org.ancestra.evolutive.tool.command.Command;
import org.ancestra.evolutive.tool.command.Parameter;
import org.apache.commons.lang.StringUtils;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Timer;

public class Admin {
	
	private Account account;
	private Player player;
	private static boolean state = false;
	private static Timer timer;
	
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
		
		/** GM LEVEL N1 **/
			
		Command<Admin> command = new Command<Admin>("HELP", null, null, 1) {

			@Override
			public void action(Admin t, String[] args) {
				t.sendText("Liste des commandes disponibles :\nu<u>* = Argument facultatif.</u>\n");
				
				for(Command<Admin> command: World.data.getAdminCommands().values()) {
					if(command == null || (command.getDescription() == null && command.getParameters().isEmpty()))
						continue;
					String msg = command.getGmLvl() + " -> " + command.getName() + " ";
					if(!command.getParameters().isEmpty()) {
						for(Parameter<Admin> parameter : command.getParameters().values()) {
							t.sendText(msg + parameter.getName() + " " + (parameter.getArguments() != null ? parameter.getArguments().replace(",", " ") + " " : "") + "- " + parameter.getDescription());
						}
					} else {
						t.sendText(msg + (command.getArguments() != null ? command.getArguments().replace(",", " ") + " " : "") + "- " + command.getDescription());
					}
				}
			}
			
		};

		commands.put("HELP", command);
		
		command = new Command<Admin>("INFOS", "Affiche les informations concernant le server.", null, 1) {

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
		
		command = new Command<Admin>("REFRESHMOBS", "Raffraichis les monstres sur la carte oï¿½ vous ï¿½tes.", null, 1) {

			@Override
			public void action(Admin t, String[] args) {
				t.getPlayer().getMap().refreshSpawns();
				t.sendText("Les monstres ont ï¿½tï¿½ rechargï¿½ avec succï¿½s !");
			}
		};
		
		commands.put("REFRESHMOBS", command);
		
		command = new Command<Admin>("MAPINFOS", "Affiche les pnjs et monstres prï¿½sent sur la carte oï¿½ vous ï¿½tes.", null, 1) {

			@Override
			public void action(Admin t, String[] args) {
				t.sendText("Liste des pnjs de la carte :\n");
				Maps map = t.getPlayer().getMap();
				
				for(Entry<Integer, Npc> entry : map.getNpcs().entrySet())
					t.sendText("Id: " + entry.getKey() + " | T:" + entry.getValue().getTemplate().getId() + " | Cell : " + entry.getValue().getCell().getId() + " | Question : " + entry.getValue().getTemplate().getInitQuestion() + "\n");

				t.sendText("Liste des groupes de monstres :\n");
				
				for(Entry<Integer, MobGroup> entry : map.getMobGroups().entrySet())
					t.sendText("Id: " + entry.getKey() + " | Cell : " + entry.getValue().getCell().getId() + " | " + entry.getValue().getAlignement() + " | Taille : " + entry.getValue().getMobsGrade().size());
			}
		};
		
		commands.put("MAPINFOS", command);
		
		command = new Command<Admin>("GUILD", null, "CREATE", 1) {

			@Override
			public void action(Admin t, String[] args) {
				t.sendText("Paramï¿½tre non indiquï¿½ : CREATE");
			}
		};
		
		command.addParameter(new Parameter<Admin>("CREATE", "Ouvre le panneaux de crï¿½ation de guilde.", "PLAYER*", 1) {

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
					t.sendText("Le joueur en question appartient dï¿½jï¿½ ï¿½ une guilde.");
					return;
				}
				
				SocketManager.GAME_SEND_gn_PACKET(player);
				t.sendText("Vous venez d'ouvrir le panneau de crï¿½ation de guilde au joueur " + player.getName() + " !");
			}
		
		});
		
		commands.put("GUILD", command);
		
		command = new Command<Admin>("TP", "Permet la tï¿½lï¿½portation d'un joueur ï¿½ une carte et une cellule indiquï¿½.", "MAP,CELL,PLAYER*", 1) {

			@Override
			public void action(Admin t, String[] args) {
				int map = -1;
				int cell = -1;
				
				try {
					map = Integer.parseInt(args[0]);
					cell = Integer.parseInt(args[1]);
				} catch(Exception e) {}
				
				if(map == -1 || cell == -1 || World.data.getMap(map) == null) {
					t.sendText("Les paramï¿½tres indiquï¿½s sont invalides !");
					return;
				}
				
				if(World.data.getMap(map).getCases().get(cell) == null)	{
					t.sendText("Les paramï¿½tres indiquï¿½s sont invalides !");
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
				t.sendText("Le joueur " +player.getName() + " a ï¿½tï¿½ tï¿½lï¿½porter avec succï¿½s !");			
			}
		};
		
		commands.put("TP", command);
		
		command = new Command<Admin>("ANNOUNCE", "Permet d'afficher une annonce ï¿½ tout les joueurs du server.", "MESSAGE", 1) {

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
		
		command = new Command<Admin>("GOTO", "Permet la teleportation d'un joueur ï¿½ un autre.", "TARGET,ToPLAYER*", 1) {

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
					t.sendText("Le joueur " + target.getName() + " a ï¿½tï¿½ tï¿½lï¿½porter avec succï¿½s.");
				} else {
					t.sendText("Le joueur " + player.getName() + " n'est pas en ligne.");
				}
			}
		};
		
		commands.put("GOTO", command);
		
		command = new Command<Admin>("TOOGLEAGGRO", "Active ou dï¿½sactive la possibilitï¿½ d'aggresion du joueur venu des autres joueurs.", "PLAYER*", 1) {

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
					t.sendText("Le joueur " + player.getName() + (player.isCanAggro() ? "ne peut ï¿½tre aggresser." : " peut ï¿½tre aggresser."));
				}
			}
		};
		
		commands.put("TOOGLEAGGRO", command);
		
		/** GM LEVEL Nï¿½2 **/
		
		command = new Command<Admin>("FIGHTPOS", null, "SHOW|ADD|DEL|DELALL", 2) {

			@Override
			public void action(Admin t, String[] args) {
				t.sendText("Paramï¿½tre non indiquï¿½ : SHOW, ADD, DEL, DELALL");
			}
		};
		
		command.addParameter(new Parameter<Admin>("SHOW", "Affiche la totalitï¿½ des positions de combat de la carte.", null, 2) {

			@Override
			public void action(Admin t, String[] args) {
				StringBuilder msg = new StringBuilder("Liste des cellules de combat :\n");
				String places = t.getPlayer().getMap().getPlaces();
				
				if(places.indexOf('|') == -1 || places.length() < 2) {
					t.sendText("Les places n'ont pas ï¿½tï¿½ dï¿½finie.");
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
		
		command.addParameter(new Parameter<Admin>("ADD", "Ajoute une cellule de combat sur la case oï¿½ vous ï¿½tes ou une case indiquï¿½.", "TEAM", 2) {

			@Override
			public void action(Admin t, String[] args) {
				int team = -1;
				int cell = -1;
				try {
					team = Integer.parseInt(args[0]);
					cell = Integer.parseInt(args[1]);
				} catch(Exception e) {}
				
				if(team < 0 || team > 1) {
					t.sendText("La valeur de l'ï¿½quipe (0 ou 1) est inccorecte.");
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
					t.sendText("La case comporte dï¿½jï¿½ une cellule de combat.");
					return;
				}
				
				if(team == 0)
					team0 += CryptManager.cellID_To_Code(cell);
				else if(team == 1)
					team1 += CryptManager.cellID_To_Code(cell);
								
				t.getPlayer().getMap().setPlaces(team0 + "|" + team1);
				
				if(!World.database.getMapData().update(t.getPlayer().getMap()))
					return;
				
				t.sendText("Une cellule de combat a ï¿½tï¿½ ajouter sur la case " + cell + ".");	
			}
			
		});

		command.addParameter(new Parameter<Admin>("DEL", "Supprime une cellule de combat sur la case oï¿½ vous ï¿½tes ou une case indiquï¿½.", "TEAM", 2) {

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

				t.sendText("Une cellule de combat a ï¿½tï¿½ supprimer sur la case " + cell + ".");	
			}
			
		});
		
		command.addParameter(new Parameter<Admin>("DELALL", "Supprime toute les cellules de combat sur la carte oï¿½ vous ï¿½tes.", null, 2) {

			@Override
			public void action(Admin t, String[] args) {
				t.getPlayer().getMap().setPlaces("|");
				
				if(!World.database.getMapData().update(t.getPlayer().getMap()))
					return;

				t.sendText("Toute les cellules de combat ont ï¿½tï¿½ supprimer avec succï¿½s.");	
			}

		});
		
		commands.put("FIGHTPOS", command);
		
		command = new Command<Admin>("MUTE", "Interdit la communication du joueur ciblé pendant un temps prédéfinis.", "PLAYER,TIME*", 2) {

			@Override
			public void action(Admin t, String[] args) {
				Player player = t.getPlayer();
				
				String name = null;
				int time = -1;
				
				try	{
					name = args[0];
					time = Integer.parseInt(args[1]);
				} catch(Exception e) {}
				
				if(name != null)
					player = World.data.getPlayerByName(name); 
								
				if(player == null) {
					t.sendText("Le joueur en question n'existe pas.");
					return;
				}
				
				if(player.getAccount() == null) {
					t.sendText("Le compte du joueur " + player.getName() + " est inexistant.");
					return;
				}
				
				if(time == -1) {
					player.getAccount().mute(false, 0);
					t.sendText("Vous avez démute " + player.getName() + ".");
					return;
				}				
				
				player.getAccount().mute(true, time);
				t.sendText("Le joueur " + player.getName() + " a été mute " + time + " secondes.");
				
				if(!player.isOnline()) {
					t.sendText("Le joueur " + player.getName() + " n'est pas connecté.");
				} else {
					SocketManager.GAME_SEND_Im_PACKET(player, "1124;" + time);
				}
			}
		};
		
		commands.put("MUTE", command);
		
		command = new Command<Admin>("KICK", "Exclut le joueur ciblé.", "PLAYER", 2) {
			
			@Override
			public void action(Admin t, String[] args) {
				String name = null;
				
				try {
					name = args[0];
				} catch(Exception e) {}
				
				Player player = World.data.getPlayerByName(name);
				
				if(player == null) {
					t.sendText("Le joueur en question n'existe pas.");
					return;
				}
				
				if(player.isOnline()) {
					player.getAccount().getGameClient().kick();
					t.sendText("Le joueur " + player.getName() + " a été expulser avec succès.");
				} else {
					t.sendText("Le joueur " + player.getName() + " n'est pas connecté.");
				}
			}
		};
		
		commands.put("KICK", command);
		
		command = new Command<Admin>("ADD", "Ajout de différents types.", "EMOTE|HONOR|JOB|JOBXP|CAPITAL|KAMAS|ITEM|SPELL|SPELLPOINT", 2) {
			
			@Override
			public void action(Admin t, String[] args) {
				t.sendText("Paramètre non indiqué : HONOR, JOB, JOBXP, CAPITAL, KAMAS, ITEM, SPELL, SPELLPOINT");
			}
			
		};
		
		command.addParameter(new Parameter <Admin>("EMOTE", "Ajoute l'�motte en question au joueur.", "ID,PLAYER*", 2) {

			@Override
			public void action(Admin t, String[] args) {
				int id = 0;
				try {
					System.out.println(args[0]);
					System.out.println(args[1]);
				} catch(Exception e) {}
				try {
					id = Integer.parseInt(args[0]);
				} catch(Exception e) {}
				
				if(id == 0) {
					t.sendText("Paramètre incorrecte, merci d'y mettre un nombre entier.");
					return;
				}
				
				Player player = t.getPlayer();
				
				if(args.length > 1) {
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				player.getEmote().add(id);
				t.sendText("Le joueur " + player.getName() + " � re�u l'�motte d'id " + id + " avec succ�s.");
			}

		});
		
		command.addParameter(new Parameter <Admin>("HONOR", "Attribut des points d'honneurs au joueur ciblé.", "HONOR,PLAYER*", 2) {

			@Override
			public void action(Admin t, String[] args) {
				int honor = 0;
				
				try {
					honor = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, merci d'y mettre un nombre entier.");
					return;
				}
				
				Player player = t.getPlayer();
				
				if(args.length > 1) {
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				
				if(player.getAlignement() == Alignement.NEUTRE) {
					t.sendText("Le joueur " + player.getName() + " est neutre, impossible de lui ajouté des points d'honneur.");
					return;
				}
				
				player.addHonor(honor);
				
				if(player.getHonor() < 0)
					player.setHonor(0);
				
				t.sendText("Le joueur " + player.getName() + " à reçu " + honor + " points d'honneur.");
			}
		});
		
		command.addParameter(new Parameter <Admin>("JOBXP", "Attribut de l'expérience au métier du joueur ciblé.", "JOB,XP,PLAYER*", 2) {

			@Override
			public void action(Admin t, String[] args) {
				int id = -1, xp = -1;
				
				try	{
					id = Integer.parseInt(args[0]);
					xp = Integer.parseInt(args[1]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer l'id du métier ainsi que l'expèrience à attribué.");
					return;
				}
				
				if(id == -1 || xp < 0)	{
					t.sendText("Paramètre incorrecte, veuillez rentrer l'id du métier ainsi que l'expèrience à attribué.");
					return;
				}
				
				Player player = t.getPlayer();
				
				if(args.length > 2) {
					player = World.data.getPlayerByName(args[2]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				JobStat job = player.getMetierByID(id);
				if(job == null)	{
					t.sendText("Le joueur " + player.getName() + " ne possède pas le métier d'id " + id + ".");
					return;
				}
					
				job.addXp(player, xp);
				
				t.sendText("Le joueur " + player.getName() + " vient de recevoir " + xp + " points d'expèrience dans le métier d'id " + id + ".");
			}
		});
				
		command.addParameter(new Parameter <Admin>("JOB", "Apprend le métier définis au joueur ciblé.", "JOB,PLAYER*", 2) {

			@Override
			public void action(Admin t, String[] args) {
				int id = -1;
				
				try	{
					id = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer l'id d'un métier existant.");
					return;
				}
				
				if(id == -1 || World.data.getMetier(id) == null) {
					t.sendText("Paramètre incorrecte, veuillez rentrer l'id d'un métier existant.");
					return;
				}
				
				Player player = t.getPlayer();
				
				if(args.length > 1)	{
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				player.learnJob(World.data.getMetier(id));
				
				t.sendText("Le joueur " + player.getName() + " vient d'apprendre le métier d'id " + id + " avec succès.");
			}
		});
				
		command.addParameter(new Parameter <Admin>("CAPITAL", "Permet d'ajouter des capitals.", "POINTS,PLAYER*", 2) {

			@Override
			public void action(Admin t, String[] args) {
				int pts = 0;
				
				try {
					pts = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				if(pts == 0) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				Player player = t.getPlayer();
				
				if(args.length > 1) {
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				player.addCapital(pts);
				SocketManager.GAME_SEND_STATS_PACKET(player);
				t.sendText("Le joueur " + player.getName() + " vient de recevoir " + pts + " points de caractéristique.");
			}
		});
		
		command.addParameter(new Parameter <Admin>("KAMAS", "Permet d'ajouter des kamas.", "KAMAS,PLAYER*", 2) {

			@Override
			public void action(Admin t, String[] args) {
				int count = 0;
				
				try	{
					count = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				if(count == 0) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				Player player = t.getPlayer();
				
				if(args.length == 2) {
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}

				long newKamas = player.getKamas() + count;
				
				if(newKamas < 0) 
					newKamas = 0;
				if(newKamas > 1000000000) 
					newKamas = 1000000000;
				
				player.setKamas(newKamas);
				
				if(player.isOnline())
					SocketManager.GAME_SEND_STATS_PACKET(player);
				
				t.sendText("Le joueur " + player.getName() + " vient de " + (count < 0 ? "perdre" : "gagner") + " kamas.");
			}
		});
		
		Parameter<Admin> parameter = new Parameter<Admin>("ITEM", "Permet d'obtenir l'équipement spécifié.", "ITEM,QUANTITY,PLAYER*", 2) {

			@Override
			public void action(Admin t, String[] args) {	
				int id = 0;
				
				try {
					id = Integer.parseInt(args[0]);
				} catch(Exception e) {}
				
				if(id == 0) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				ObjectTemplate template = World.data.getObjectTemplate(id);
				
				if(template == null) {
					t.sendText("Paramètre incorrecte, l'objet n'existe pas.");
					return;
				}
				
				int qua = 1;
				Player player = t.getPlayer();
				
				if(args.length >= 2) {
					try {
						qua = Integer.parseInt(args[1]);
					} catch(Exception e) {}
				}
				
				boolean useMax = false;
				if(args.length == 3) {
					if(args[2].equalsIgnoreCase("MAX")) {
						useMax = true;
					} else {
						player = World.data.getPlayerByName(args[2]);
						if(player == null) {
							t.sendText("Le joueur en question n'existe pas.");
							return;
						}
					}
				} else if(args.length > 3) {
					player = World.data.getPlayerByName(args[3]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				if(qua < 1)
					qua = 1;
				
				Object object = template.createNewItem(qua, useMax);
				
				if(t.getPlayer().addObject(object, true))
					World.data.addObject(object, true);
				
				t.sendText("Le joueur " + player.getName() + " vient de recevoir l'objet '" + template.getName() + "'" + (useMax ? " avec des stats maximum." : "."));
				SocketManager.GAME_SEND_Ow_PACKET(t.getPlayer());
			}
		};
		
		parameter.addParameter(new Parameter<Admin>("SET", "Ajoute tout les objets du set en question sur vous.", "SET,MAX*", 2) {
			
			@Override
			public void action(Admin t, String[] args) {
				int id = 0;
				
				try {
					id = Integer.parseInt(args[0]);
				} catch(Exception e) {}
				
				ObjectSet set = World.data.getItemSet(id);
				
				if(id == 0 || set == null) {
					t.sendText("Le set d'objet en question n'existe pas.");
					return;
				}
				
				boolean useMax = false;
				
				if(args.length == 2)
					useMax = args[1].equals("MAX");

				for(ObjectTemplate template : set.getObjectsTemplate()) {
					Object object = template.createNewItem(1, useMax);
					if(t.getPlayer().addObject(object, true))
						World.data.addObject(object, true);
				}
				
				t.sendText("Le set d'objet d'id " + id + " a été crée avec succès" + (useMax ? " avec des stats maximum" : "")+ ".");
			}
			
		});
		
		command.addParameter(parameter);
		
		command.addParameter(new Parameter<Admin>("SPELLPOINT", "Permet d'ajouter des points de sort.", "POINTS,PLAYER*", 2) {
			
			@Override
			public void action(Admin t, String[] args) {	
				int pts = 0;
			
				try	{
					pts = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				if(pts == -1) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				Player player = t.getPlayer();
				if(args.length > 1) {
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				player.addSpellPoint(pts);
				SocketManager.GAME_SEND_STATS_PACKET(player);
		
				t.sendText("Le joueur " + player.getName() + " vient de recevoir " + pts + " points de sort.");
			}
			
		});
		
		command.addParameter(new Parameter<Admin>("SPELL", "Apprend le sort spécifié au playernnage.", "SPELL,PLAYER*", 2) {

			@Override
			public void action(Admin t, String[] args) {
				int id = -1;
				try	{
					id = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				if(id == -1) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				if(World.data.getSort(id) == null) {
					t.sendText("Le sort d'id " + id + " n'existe pas.");
					return;
				}
				
				Player player = t.getPlayer();
				if(args.length > 1)	{
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				player.learnSpell(id, 1, true, true, true);
				t.sendText("Le joueur " + player.getName() + " vient d'apprendre le sort d'id " + id + ".");
			}
		});
		
		commands.put("ADD", command);
		
		command = new Command<Admin>("!GETITEM", null, null, 3) {

			@Override
			public void action(Admin t, String[] args) {
				int id = 0;
				
				try {
					id = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				if(id == 0) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				ObjectTemplate template = World.data.getObjectTemplate(id);
				
				if(template == null) {
					t.sendText("Paramètre incorrecte, l'objet n'existe pas.");
					return;
				}
				
				int qua = 1;
				Player player = t.getPlayer();
				
				if(args.length == 2) {
					try {
						qua = Integer.parseInt(args[1]);
					} catch(Exception e) {}
				}
				
				boolean useMax = false;
				if(args.length == 3) {
					if(args[2].equalsIgnoreCase("MAX")) {
						useMax = true;
					} else {
						player = World.data.getPlayerByName(args[2]);
						if(player == null) {
							t.sendText("Le joueur en question n'existe pas.");
							return;
						}
					}
				} else if(args.length > 3) {
					player = World.data.getPlayerByName(args[3]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				if(qua < 1)
					qua = 1;
				
				Object object = template.createNewItem(qua, useMax);
				
				if(t.getPlayer().addObject(object, true))
					World.data.addObject(object, true);
				
				t.sendText("Le joueur " + player.getName() + " vient de recevoir l'objet '" + template.getName() + "'" + (useMax ? " avec des stats maximum." : "."));
				SocketManager.GAME_SEND_Ow_PACKET(t.getPlayer());
			}
			
		};
		
		commands.put("!GETITEM", command);
		
		command = new Command<Admin>("SET", null, "ALIGN|SIZE|MORPH|LEVEL|LIFE|TITLE", 2) {
			
			public void action(Admin t, String[] args) {
				t.sendText("Paramètre non indiqué : ALIGN, SIZE, MORPH, LEVEL, LIFE, TITLE");
			}
			
		};
		
		command.addParameter(new Parameter<Admin>("TITLE", "Attribut un titre spécifié.", "TITLE,PERSO*", 2) {

			@Override
			public void action(Admin t, String[] args) {
				byte id = 0;
				
				try {
					id = Byte.parseByte(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				Player player = t.getPlayer();
				
				if(args.length > 1)	{
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				player.setTitle(id);
				t.sendText("Le joueur " + player.getName() + " vient d'acquérir le titre d'id " + id + ".");
				
				if(player.getFight() == null) 
					SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getMap(), player);
			}
		});
		
		command.addParameter(new Parameter<Admin>("ALIGN", "Attribut un alignement.", "ALIGN,PERSO*", 2) {

			@Override
			public void action(Admin t, String[] args) {
				byte align = -1;
				
				try	{
					align = Byte.parseByte(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				if(align < Alignement.NEUTRE.getId() || align > Alignement.MERCENAIRE.getId()) {
					t.sendText("La valeur de l'alignement est incorrecte, elle doit être comprise entre 0 et 3.");
					return;
				}
				
				Player player = t.getPlayer();
				if(args.length > 1) {
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				player.modifAlignement(align);
				t.sendText("Le joueur " + player.getName() + " vient de passer " + player.getAlignement().toString().toLowerCase() + ".");
			}
		});
		
		command.addParameter(new Parameter<Admin>("SIZE", "Définis la taille du personnage.", "SIZE,PLAYER*", 2) {
			
			public void action(Admin t, String[] args) {
				int size = -1;
				
				try	{
					size = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				if(size == -1) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				Player player = t.getPlayer();
				
				if(args.length > 1) {
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				player.setSize(size);
				SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getMap(), player);
				t.sendText("La taille du joueur " + player.getName() + " vient d'être changé à " + size + "%.");
			}
		});
		
		command.addParameter(new Parameter<Admin>("MORPH", "Définis l'apparence du joueur (-1 pour être démorph).", "MORPH,PLAYER*", 2) {
			
			public void action(Admin t, String[] args) {
				int morph = -2;
				
				try {
					morph = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				if(morph == -2) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				Player player = t.getPlayer();
				
				if(args.length > 1) {
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				if(morph == -1) {
					player.setGfx(player.getClasse().getId() * 10 + player.getSex());
				} else {
					player.setGfx(morph);
				}
				
				SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getMap(), player);
				t.sendText("Le joueur " + player.getName() + " a été transformer dans la gfx d'id " + (morph == -1 ? (player.getClasse().getId() * 10 + player.getSex()) : morph) + ".");
			}
		});
		
		command.addParameter(new Parameter<Admin>("LEVEL", "Attribut le niveau spécifié.", "LEVEL,PLAYER*", 2) {
			
			public void action(Admin t, String[] args) {
				int level = 0;
				
				try {
					level = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier positif compris entre 1 et " + World.data.getExpLevelSize() + ".");
					return;
				}
				
				if(level <= 0) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier positif compris entre 1 et " + World.data.getExpLevelSize() + ".");
					return;
				}
					
				if(level > World.data.getExpLevelSize())
					level = World.data.getExpLevelSize();
				
				Player player = t.getPlayer();
				
				if(args.length == 2) {
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}
				
				if(player.getLevel() < level) {
					while(player.getLevel() < level)
						player.levelUp(false,true);
					if(player.isOnline()) {
						SocketManager.GAME_SEND_SPELL_LIST(player);
						SocketManager.GAME_SEND_NEW_LVL_PACKET(player.getAccount().getGameClient(), player.getLevel());
						SocketManager.GAME_SEND_STATS_PACKET(player);
					}
				} else {
					player.setLevel(level);
					player.setExperience(World.data.getExpLevel(level).perso);
					
					for(Object object : player.getObjects().values()) {
						if(object == null)
							continue;
						object.setPosition(ObjectPosition.NO_EQUIPED);
						SocketManager.GAME_SEND_OBJET_MOVE_PACKET(player, object);
					}
					
					player.setCapital((level - 1) * 5);
					player.setSpellPoints(level - 1);
					player.setSpells(Constants.getStartSorts(player.getClasse().getId()));
					
					for(int a = 1; a <= player.getLevel(); a++)
						Constants.onLevelUpSpells(player, a);
					
					player.setSpellsPlace(Constants.getStartSortsPlaces(player.getClasse().getId()));
					player.setStats(new Stats(true, player));
					
					SocketManager.GAME_SEND_STATS_PACKET(player);
					SocketManager.GAME_SEND_Ow_PACKET(player);
					SocketManager.GAME_SEND_SPELL_LIST(player);
					SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getMap(), player);
				}
				
				t.sendText("Le joueur " + player.getName() + " vient de passer niveau " + level +" avec succès.");
			}
		});
		
		command.addParameter(new Parameter<Admin>("LIFE", "Attribut la vie en pourcentage", "POURCENTAGE,PLAYER*", 2) {
			
			public void action(Admin t, String[] args) {
				int life = -1;
				try {
					life = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier positif compris entre 0 et 100.");
					return;
				}
				
				if(life < 0 || life > 100) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier positif compris entre 0 et 100.");
					return;
				}
			
				Player player = t.getPlayer();
				
				if(args.length == 2) {
					player = World.data.getPlayerByName(args[1]);
					if(player == null) {
						t.sendText("Le joueur en question n'existe pas.");
						return;
					}
				}

				player.setPdv(player.getMaxPdv() * life / 100);
				
				if(player.isOnline())
					SocketManager.GAME_SEND_STATS_PACKET(player);

				t.sendText("La vie du joueur " + player.getName() + " est à " + (player.getMaxPdv() * life / 100) + ".");
			}
		});
		
		command.addParameter(new Parameter<Admin>("MAXGROUP", "Change le nombre de groupe de monstre sur la carte actuel.", null, 3) {
			
			public void action(Admin t, String[] args) {
				byte nbr = -1;
				
				try {
					nbr = Byte.parseByte(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier positif compris entre 0 et 10.");
					return;
				}
				
				if(nbr <= -1 || nbr > 10) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier positif compris entre 0 et 10.");
					return;
				}
				
				t.getPlayer().getMap().setMaxGroup(nbr);
				World.database.getMapData().update(t.getPlayer().getMap());
				
				t.sendText("La carte d'id " + t.getPlayer().getMap().getId() + " n'accueil plus que " + nbr + " groupe de monstre.");
			}
		});
		
		commands.put("SET", command);
		
		command = new Command<Admin>("TRIGGER", null, "ADD|DEL", 3) {

			@Override
			public void action(Admin t, String[] args) {
				t.sendText("Argument non défini : ADD, DEL");
			}
		};
		
		command.addParameter(new Parameter<Admin>("ADD", "Ajoute un trigger à votre position.", "ARGS,ACTION,COND", 3) {

			@Override
			public void action(Admin t, String[] args) {
				
				int action = 0; 
				String arg = "", cond = "";
				
				try {
					arg = args[0];
					action = Integer.parseInt(args[1]);
					cond = args[2];
				} catch(Exception e) {}
				
				if(args.equals("")) {
					t.sendText("Paramètre incorrecte.");
					return;
				}
				
				t.getPlayer().getCell().addOnCellStopAction(action, arg, cond);
				World.database.getScriptedCellData().update(t.getPlayer().getMap().getId(), t.getPlayer().getCell().getId(), action, 1, arg, cond);
				
				t.sendText("L'action sur la cellule " + t.getPlayer().getCell().getId() + " a été ajouter avec succès.");
			}

		});
		
		command.addParameter(new Parameter<Admin>("DEL", "Supprime un trigger à la position spécifié.", "CELLID", 3) {

			@Override
			public void action(Admin t, String[] args) {
				int cell = t.getPlayer().getCell().getId();
				
				try	{
					cell = Integer.parseInt(args[0]);
				} catch(Exception e) {}
				
				if(cell == -1 || t.getPlayer().getMap().getCases().get(cell) == null) {
					t.sendText("La cellule du joueur ou indiqué est inexistante.");
					return;
				}
				
				t.getPlayer().getMap().getCases().get(cell).clearOnCellAction();
				 World.database.getScriptedCellData().delete(t.getPlayer().getMap().getId(), cell);

				t.sendText("L'action sur la cellule " + cell + " a été retirer avec succès.");
			}

		});
		
		commands.put("TRIGGER", command);
		
		command = new Command<Admin>("SPAWN", "Ajoute un groupe de monstre sur la carte et la cellule actuel où vous êtes.", "GROUPDATA", 2) {

			@Override
			public void action(Admin t, String[] args) {
				String data = null;
				
				try {
					data = args[0];
				} catch(Exception e) {}
			
	            if(data == null) {
	            	t.sendText("Paramètre incorrecte.");
	            	return;
	            }
	            
				t.getPlayer().getMap().spawnGroupOnCommand(t.getPlayer().getCell().getId(), data);
				t.sendText("Le groupe de monstre a été ajouté sur la cellule " + t.getPlayer().getCell().getId() + ".");
			}
		};
		
		command.addParameter(new Parameter<Admin>("FIX", "Ajoute un groupe de monstre statique sur la carte et la cellule actuel où vous êtes.", "GROUPDATA", 2) {

			@Override
			public void action(Admin t, String[] args) {
				String data = null;
				
				try {
					data = args[0];
				} catch(Exception e) {}
			
	            if(data == null) {
	            	t.sendText("Paramètre incorrecte.");
	            	return;
	            }
	            
				t.getPlayer().getMap().addStaticGroup(t.getPlayer().getCell().getId(), data);
				World.database.getMonsterData().saveNewFixGroup(t.getPlayer().getMap().getId(),t.getPlayer().getCell().getId(), data);
				t.sendText("Le groupe de monstre a été ajouté sur la cellule " + t.getPlayer().getCell().getId() +" de façon fixe.");
			}
			
		});
		
		commands.put("SPAWN", command);
		
		/** GM Level N3 **/ 
		
		command = new Command<Admin>("EXIT", "Ferme le server.", null, 3) {
			
			@Override
			public void action(Admin t, String[] args) {
				World.data.saveData(-1);
				System.exit(0);				
			}
			
		};
		
		commands.put("EXIT", command);
		
		command = new Command<Admin>("SAVE", "Lance une sauvegarde du server.", null, 3) {

			@Override
			public void action(Admin t, String[] args) {
				World.data.saveData(t.getPlayer().getId());
				t.sendText("La sauvegarde du server a été effectuer.");	
			}
		};
		
		commands.put("SAVE", command);
		
		command = new Command<Admin>("BAN", "Banni le joueur en question.", "PLAYER", 3) {

			@Override
			public void action(Admin t, String[] args) {
				String name = null;
				
				try {
					name = args[0];
				} catch(Exception e) {}
				
				if(name == null) {
					t.sendText("Paramètre incorrecte, merci de spécifié le nom du joueur.");
					return;
				}
				
				if(name.isEmpty()) {
					t.sendText("Paramètre incorrecte, merci de spécifié le nom du joueur.");
					return;
				}
				
				Player player = World.data.getPlayerByName(name);
				
				if(player == null) {
					t.sendText("Le joueur en question n'existe pas.");
					return;
				}
				
				if(player.getAccount() == null)
					World.database.getAccountData().load(player.getAccount().getUUID());
				
				if(player.getAccount() == null) {
					t.sendText("Le compte du joueur est inexistant.");
					return;
				}
				
				player.getAccount().setBanned(true);
				World.database.getAccountData().update(player.getAccount());
				
				if(player.getAccount().getGameClient() != null)
					player.getAccount().getGameClient().kick();
				
				t.sendText("Le joueur " + player.getName() + " a été banni.");
			}
		};
		
		commands.put("BAN", command);
		
		command = new Command<Admin>("UNBAN", "Débanni le joueur en question.", "PLAYER", 3) {

			@Override
			public void action(Admin t, String[] args) {
				String name = null;
				
				try {
					name = args[0];
				} catch(Exception e) {}
				
				if(name == null) {
					t.sendText("Paramètre incorrecte, merci de spécifié le nom du joueur.");
					return;
				}
				
				if(name.isEmpty()) {
					t.sendText("Paramètre incorrecte, merci de spécifié le nom du joueur.");
					return;
				}
				
				Player player = World.data.getPlayerByName(name);
				
				if(player == null) {
					t.sendText("Le joueur en question n'existe pas.");
					return;
				}
				
				if(player.getAccount() == null)
					World.database.getAccountData().load(player.getAccount().getUUID());

				if(player.getAccount() == null) {
					t.sendText("Le compte du joueur est inexistant.");
					return;
				}
				
				player.getAccount().setBanned(false);
				World.database.getAccountData().update(player.getAccount());
				
				t.sendText("Le joueur " + player.getName() + " a été débanni.");
			}
		};
		
		commands.put("UNBAN", command);
		
		command = new Command<Admin>("SEND", "Permet d'envoyé un packet au client.", "PACKET", 3) {

			@Override
			public void action(Admin t, String[] args) {
				SocketManager.GAME_SEND_STATS_PACKET(t.getPlayer());
				SocketManager.send(t.getPlayer(), StringUtils.join(args, " "));
			}
		};
	
		commands.put("SEND", command);
		
		command = new Command<Admin>("SHUTDOWN", "Eteint le server selon le temps spécifié.", "OFF/ON,TIME", 3) {

			@Override
			public void action(Admin t, String[] args) {
				int time = 30, state = 0;
				
				try {
					state = Integer.parseInt(args[0]);
					time = Integer.parseInt(args[1]);
				} catch(Exception e) {}
				
				if(state == 1 && Admin.state) {
					t.sendText("Un redémarrage est déjà lancé.");
				} else if(state == 1 && !Admin.state) {
					Admin.timer = t.createTimer(time);
					Admin.timer.start();
					Admin.state = true;
					String timeMSG = "minutes";
					
					if(time <= 1)
						timeMSG = "minute";
		
					SocketManager.GAME_SEND_Im_PACKET_TO_ALL("115;" + time + " " + timeMSG);
					t.sendText("Un redémarrage a été lancer dans " + time + " " + timeMSG + "."); 
				} else if(state == 0 && Admin.state) {
					Admin.timer.stop();
					Admin.state = false;
					t.sendText("Shutdown arrete.");
				} else if(state == 0 && !Admin.state) {
					t.sendText("Aucun redémarrage prévu.");
				}
			}
		};
	
		commands.put("SHUTDOWN", command);
		
		command = new Command<Admin>("NPC", null, "ADD|DEL", 3) {

			@Override
			public void action(Admin t, String[] args) {
				t.sendText("Argument non défini : ADD, DEL");
				return;
			}
		};
		
		command.addParameter(new Parameter<Admin>("ADD", "Ajoute un npc à votre position.", "ID", 3) {

			@Override
			public void action(Admin t, String[] args) {
				int templateId;
				try	{
					templateId = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
                NpcTemplate template = World.data.getNpcTemplate(templateId);
				if(templateId == 0 || template == null) {
					t.sendText("Le pnj en question n'existe pas.");
					return;
				}
                new Npc(template,t.getPlayer().getMap(),t.getPlayer().getCell(),(byte)t.getPlayer().getOrientation());
				
				String str = "Le pnj a été ajouté avec succès.";
				if(t.getPlayer().getOrientation() == 0 || t.getPlayer().getOrientation() == 2 || t.getPlayer().getOrientation() == 4 || t.getPlayer().getOrientation() == 6)
							str = "Le pnj a été ajouté avec succès mais est invisible (orientation diagonale invalide).";
				
				if(World.database.getNpcData().create(t.getPlayer().getMap().getId(), templateId, t.getPlayer().getCell().getId(), t.getPlayer().getOrientation()))
					t.sendText(str);
				else
					t.sendText("Le pnj n'a pas pu être ajouté dû à un problème d'exportation en base de donnée.");
			}

		});
		
		command.addParameter(new Parameter<Admin>("DEL", "Supprime le npc spécifié.", "ID", 3) {

			@Override
			public void action(Admin t, String[] args) {
				int id = 0;
				
				try {
					id = Integer.parseInt(args[0]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				Npc npc = t.getPlayer().getMap().getNpcs().get(id);
				
				if(id == 0 || npc == null) {
					t.sendText("Le pnj en question n'existe pas.");
					return;
				}
				
				SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(t.getPlayer().getMap(), id);
				
				if(t.getPlayer().getMap().getNpcs().containsKey(id))
					t.getPlayer().getMap().getNpcs().remove(id);
				else if(t.getPlayer().getMap().getMobGroups().containsKey(id))
					t.getPlayer().getMap().getMobGroups().remove(id);
				
				if(World.database.getNpcData().delete(t.getPlayer().getMap().getId(), npc.getCell().getId()))
					t.sendText("Le pnj a été supprimer avec succès.");
				else
					t.sendText("Le pnj n'a pas pu être supprimé dû a une erreur d'exportation en base de donnée.");
			}

		});
		
		command.addParameter(new Parameter<Admin>("MOVE", "Permet de déplacer le pnj désigné sur la cellule sur laquelle vous êtes.", "ID", 3) {
			
			public void action(Admin t, String[] args) {
				int id = 0;
				
				try {
					id = Integer.parseInt(args[0]);
				} catch(Exception e) {}
				
				Npc npc = t.getPlayer().getMap().getNpcs().get(id);
				
				if(id == 0 || npc == null) {
					t.sendText("Le pnj en question n'existe pas.");
					return;
				}
				
				int exCell = npc.getCell().getId();

				SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(t.getPlayer().getMap(), id);

				npc.setPosition(t.getPlayer().getCell());
				npc.setOrientation(t.getPlayer().getOrientation());
	
				SocketManager.GAME_SEND_ADD_NPC_TO_MAP(t.getPlayer().getMap(), npc);
				
				String str = "Le PNJ a été déplacé avec succès.";
				if(t.getPlayer().getOrientation() == 0 || t.getPlayer().getOrientation() == 2 || t.getPlayer().getOrientation() == 4 || t.getPlayer().getOrientation() == 6)
					str += "Le PNJ a été déplacé avec succès mais est devenu invisible (orientation diagonale invalide).";
				
				if(World.database.getNpcData().delete(t.getPlayer().getMap().getId(), exCell)
				&& World.database.getNpcData().create(t.getPlayer().getMap().getId(), npc.getTemplate().getId(), t.getPlayer().getCell().getId(), t.getPlayer().getOrientation()))
					t.sendText(str);
				else
					t.sendText("Le PNJ n'a pas pu être déplacé dû a une erruer d'exportation en base de donnée.");
			}
			
		});
		
		parameter = new Parameter<Admin>("ITEM", null, "ADD|DEL", 3) {

			@Override
			public void action(Admin t, String[] args) {
				t.sendText("Argument non défini : ADD, DEL");
			}

		};
		
		parameter.addParameter(new Parameter<Admin>("ADD", "Ajoute l'équipement spécifié dans un pnj de la map défini.", "NPC|ITEM", 3) {

			@Override
			public void action(Admin t, String[] args) {
				int guid = 0;
				int id = -1;
				
				try {
					guid = Integer.parseInt(args[0]);
					id = Integer.parseInt(args[1]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				NpcTemplate npc =  t.getPlayer().getMap().getNpcs().get(guid).getTemplate();
				ObjectTemplate template =  World.data.getObjectTemplate(id);
				
				if(guid == 0 || id == -1 || npc == null || template == null) {
					t.sendText("Le pnj ou le template d'objet est inexistant.");
					return;
				}
				
				String str;
				if(npc.addObject(template))
					str = "L'objet '" + template.getName() + "' a été ajouté avec succès sur le pnj d'id " + guid + ".";
				else 
					str = "L'objet n'a pas pu être rajouté car il existe déjà dans le pnj.";
				
				t.sendText(str);
			}

		});
		
		parameter.addParameter(new Parameter<Admin>("DEL", "Supprime l'équipement spécifié dans un pnj de la map défini.", "NPCGUID,ITEMID", 3) {

			@Override
			public void action(Admin t, String[] args) {
				int guid = 0;
				int id = -1;
				
				try {
					guid = Integer.parseInt(args[0]);
					id = Integer.parseInt(args[1]);
				} catch(Exception e) {
					t.sendText("Paramètre incorrecte, veuillez rentrer un nombre entier.");
					return;
				}
				
				NpcTemplate npc =  t.getPlayer().getMap().getNpcs().get(guid).getTemplate();
				
				if(guid == 0 || id == -1 || npc == null) {
					t.sendText("Le pnj ou le template d'objet est inexistant.");
					return;
				}
				
				String str;
				if(npc.removeObject(id))
					str = "L'objet a été retirer avec succès.";
				else
					str = "L'objet n'a pas été retirer car il n'existe pas dans le pnj concerné.";
				
				t.sendText(str);	
			}

		});
		
		command.addParameter(parameter);
		
		commands.put("NPC", command);
		
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
	        		World.data.saveData(-1);
	        		for(Player perso : World.data.getOnlinePersos())
	        			perso.getAccount().getGameClient().kick();
	    			System.exit(0);
	        	}
	        }
	    };
	    return new Timer(60000, action);//60000
	}
}