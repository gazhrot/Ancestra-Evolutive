package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Couple;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.object.Objet;
import org.ancestra.evolutive.object.Objet.ObjTemplate;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.util.ArrayList;

public class OtherData extends AbstractDAO<Object>{

	public OtherData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.Other");
	}
	
	@Override
	public boolean create(Object obj) { return false; }
	@Override
	public boolean delete(Object obj) { return false; }
	@Override
	public boolean update(Object obj) { return false; }
	@Override
	public Object load(int id) { return null; }
	
	public void loadZaaps() {
		try {
			Result result = getData("SELECT * FROM zaaps");
			while (result.resultSet.next()) {
				Constants.ZAAPS.put(result.resultSet.getInt("mapID"), result.resultSet.getInt("cellID"));
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("LoadZaaps: "+e.getMessage());
		}
	}
	
	public void loadZaapis() {
		try {
			String bonta = "", brakmar = "", neutre = "";
			Result result = getData("SELECT * FROM zaapi");
			
			while (result.resultSet.next()) {
				if (result.resultSet.getInt("align") == Constants.ALIGNEMENT_BONTARIEN) {
					bonta += result.resultSet.getString("mapid");
					if (!result.resultSet.isLast())
						bonta += ",";
				} else if (result.resultSet.getInt("align") == Constants.ALIGNEMENT_BRAKMARIEN) {
					brakmar += result.resultSet.getString("mapid");
					if (!result.resultSet.isLast())
						brakmar += ",";
				} else {
					neutre += result.resultSet.getString("mapid");
					if (!result.resultSet.isLast())
						neutre += ",";
				}
			}
			Constants.ZAAPI.put(Constants.ALIGNEMENT_BONTARIEN, bonta);
			Constants.ZAAPI.put(Constants.ALIGNEMENT_BRAKMARIEN, brakmar);
			Constants.ZAAPI.put(Constants.ALIGNEMENT_NEUTRE, neutre);
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(OtherData): "+e.getMessage());
		}
	}
	
	public void loadBannedIps() {
		try {
			Result result = getData("SELECT * FROM banip");
			while (result.resultSet.next()) {
				Constants.BAN_IP += result.resultSet.getString("ip");
				if (!result.resultSet.isLast())
					Constants.BAN_IP += ",";
			}
			close(result);
        } catch (Exception e) {
			Console.instance.writeln("SQL ERROR(OtherData): "+e.getMessage());
		}
	}
	
	public boolean addBannedIp(String ip) {
		try {
			String baseQuery = "INSERT INTO `banip`" + " VALUES (?);";
			PreparedStatement statement = getPreparedStatement(baseQuery);
			statement.setString(1, ip);
			
			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(OtherData): "+e.getMessage());
		}
		return false;
	}
	
	public void loadCrafts() {
		try {
			Result result = getData("SELECT * from crafts;");
			while (result.resultSet.next()) {
				ArrayList<Couple<Integer, Integer>> m = new ArrayList<Couple<Integer, Integer>>();

				boolean cont = true;
				for (String str : result.resultSet.getString("craft").split(";")) {
					try {
						int tID = Integer.parseInt(str.split("\\*")[0]);
						int qua = Integer.parseInt(str.split("\\*")[1]);
						m.add(new Couple<Integer, Integer>(tID, qua));
					} catch (Exception e) {
						e.printStackTrace();
						cont = false;
					}

				}
				// s'il y a eu une erreur de parsing, on ignore cette recette
				if (!cont)
					continue;

				World.data.addCraft(result.resultSet.getInt("id"), m);
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(OtherData): "+e.getMessage());
		}
	}
	
	public void reloadLiveActions() {
		/* Variables repr�sentant les champs de la base */
		Player perso;
		int action, nombre, id;
		String sortie, couleur = "DF0101";
									
		ObjTemplate t;
		Objet obj;
		/* FIN */
		try {
			Result result = getData("SELECT * from live_action;");

			while (result.resultSet.next()) {
				perso = World.data.getPersonnage(result.resultSet.getInt("PlayerID"));
				if (perso == null) {
					Log.addToShopLog("Personnage " + result.resultSet.getInt("PlayerID")
							+ " non trouve, personnage non charge ?");
					continue;
				}
				if (!perso.isOnline()) {
					Log.addToShopLog("Personnage " + result.resultSet.getInt("PlayerID")
							+ " hors ligne");
					continue;
				}
				if (perso.getAccount() == null) {
					Log.addToShopLog("Le Personnage " + result.resultSet.getInt("PlayerID")
							+ " n'est attribue a aucun compte charge");
					continue;
				}
				if (perso.getAccount().getGameClient() == null) {
					Log.addToShopLog("Le Personnage "
							+ result.resultSet.getInt("PlayerID")
							+ " n'a pas thread associe, le personnage est il hors ligne ?");
					continue;
				}
				if (perso.getFight() != null)
					continue; // Perso en combat @ Nami-Doc
				action = result.resultSet.getInt("Action");
				nombre = result.resultSet.getInt("Nombre");
				id = result.resultSet.getInt("ID");
				sortie = "+";

				switch (action) {
				case 1: // Monter d'un level
					if (perso.getLevel() == World.data.getExpLevelSize())
						continue;
					for (int n = nombre; n > 1; n--)
						perso.levelUp(false, true);
					perso.levelUp(true, true);
					sortie += nombre + " Niveau(x)";
					break;
				case 2: // Ajouter X point d'experience
					if (perso.getLevel() == World.data.getExpLevelSize())
						continue;
					perso.addXp(nombre);
					sortie += nombre + " Xp";
					break;
				case 3: // Ajouter X kamas
					perso.addKamas(nombre);
					sortie += nombre + " Kamas";
					break;
				case 4: // Ajouter X point de capital
					perso.addCapital(nombre);
					sortie += nombre + " Point(s) de capital";
					break;
				case 5: // Ajouter X point de sort
					perso.addSpellPoint(nombre);
					sortie += nombre + " Point(s) de sort";
					break;
				case 20: // Ajouter un item avec des jets al�atoire
					t = World.data.getObjTemplate(nombre);
					if (t == null)
						continue;
					obj = t.createNewItem(1, false); // Si mis � "true" l'objet
														// � des jets max. Sinon
														// ce sont des jets
														// al�atoire
					if (obj == null)
						continue;
					if (perso.addObjet(obj, true))// Si le joueur n'avait pas
													// d'item similaire
						World.data.addObjet(obj, true);
					Log.addToSockLog("Objet " + nombre + " ajouter a "
							+ perso.getName() + " avec des stats aleatoire");
					SocketManager
							.GAME_SEND_MESSAGE(
									perso,
									"L'objet \""
											+ t.getName()
											+ "\" viens d'etre ajouter a votre personnage",
									couleur);
					break;
				case 21: // Ajouter un item avec des jets MAX
					t = World.data.getObjTemplate(nombre);
					if (t == null)
						continue;
					obj = t.createNewItem(1, true); // Si mis � "true" l'objet �
													// des jets max. Sinon ce
													// sont des jets al�atoire
					if (obj == null)
						continue;
					if (perso.addObjet(obj, true))// Si le joueur n'avait pas
													// d'item similaire
						World.data.addObjet(obj, true);
					Log.addToSockLog("Objet " + nombre + " ajoute a "
							+ perso.getName() + " avec des stats MAX");
					SocketManager
							.GAME_SEND_MESSAGE(
									perso,
									"L'objet \""
											+ t.getName()
											+ "\" avec des stats maximum, viens d'etre ajoute a votre personnage",
									couleur);
					break;
				case 118:// Force
					perso.getStats().addOneStat(action, nombre);
					SocketManager.GAME_SEND_STATS_PACKET(perso);
					sortie += nombre + " force";
					break;
				case 119:// Agilite
					perso.getStats().addOneStat(action, nombre);
					SocketManager.GAME_SEND_STATS_PACKET(perso);
					sortie += nombre + " agilite";
					break;
				case 123:// Chance
					perso.getStats().addOneStat(action, nombre);
					SocketManager.GAME_SEND_STATS_PACKET(perso);
					sortie += nombre + " chance";
					break;
				case 124:// Sagesse
					perso.getStats().addOneStat(action, nombre);
					SocketManager.GAME_SEND_STATS_PACKET(perso);
					sortie += nombre + " sagesse";
					break;
				case 125:// Vita
					perso.getStats().addOneStat(action, nombre);
					SocketManager.GAME_SEND_STATS_PACKET(perso);
					sortie += nombre + " vita";
					break;
				case 126:// Intelligence
					int statID = action;
					perso.getStats().addOneStat(statID, nombre);
					SocketManager.GAME_SEND_STATS_PACKET(perso);
					sortie += nombre + " intelligence";
					break;
				}
				SocketManager.GAME_SEND_STATS_PACKET(perso);
				if (action < 20 || action > 100)
					SocketManager.GAME_SEND_MESSAGE(perso, sortie
							+ " a votre personnage", couleur); // Si l'action
																// n'est pas un
																// ajout d'objet
																// on envoye un
																// message a
																// l'utilisateur

				Log.addToShopLog("(Commande " + id + ")Action " + action
						+ " Nombre: " + nombre
						+ " appliquee sur le personnage "
						+ result.resultSet.getInt("PlayerID") + "(" + perso.getName() + ")");
				try {
					PreparedStatement statement = getPreparedStatement("DELETE FROM live_action WHERE ID=" + id);
					execute(statement);
					close(statement);
					Log.addToShopLog("Commande " + id + " supprimee.");
				} catch (Exception e) {
					Console.instance.writeln("SQL ERROR(OtherData): "+e.getMessage());
				}
				perso.save();
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(OtherData): "+e.getMessage());
		}
	}
	
	public void loadMarchand() {
		try {
			Result result = getData("SELECT * FROM `personnages` WHERE `seeSeller`='1';");
			while (result.resultSet.next())
				World.data.addSeller(result.resultSet.getInt("guid"), result.resultSet.getShort("map"));
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("LoadZaaps: "+e.getMessage());
		}
	}
	
	public String getNaturalStats(int id) {
		String stats = null;
		try {
			Result result = getData("SELECT statsTemplate FROM `item_template` WHERE `id`='"+id+"';");
			if(result.resultSet.next())
				stats = result.resultSet.getString("statsTemplate");
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(OtherData): "+e.getMessage());
		}
		return stats;
	}
}	
