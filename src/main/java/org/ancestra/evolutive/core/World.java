package org.ancestra.evolutive.core;

import org.ancestra.evolutive.area.Area;
import org.ancestra.evolutive.area.Continent;
import org.ancestra.evolutive.area.SubArea;
import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Couple;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.database.Database;
import org.ancestra.evolutive.entity.collector.Collector;
import org.ancestra.evolutive.entity.Mount;
import org.ancestra.evolutive.entity.monster.MobTemplate;
import org.ancestra.evolutive.entity.npc.NpcAnswer;
import org.ancestra.evolutive.entity.npc.NpcQuestion;
import org.ancestra.evolutive.entity.npc.NpcTemplate;
import org.ancestra.evolutive.event.Events;
import org.ancestra.evolutive.fight.spell.Animation;
import org.ancestra.evolutive.fight.spell.Spell;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.hdv.HDV;
import org.ancestra.evolutive.hdv.HDV.HdvEntry;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.house.Trunk;
import org.ancestra.evolutive.job.Job;
import org.ancestra.evolutive.map.InteractiveObject.InteractiveObjectTemplate;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.map.MountPark;
import org.ancestra.evolutive.object.ItemSet;
import org.ancestra.evolutive.object.Objet;
import org.ancestra.evolutive.object.Objet.ObjTemplate;
import org.ancestra.evolutive.object.PierreAme;
import org.ancestra.evolutive.other.ExpLevel;
import org.ancestra.evolutive.tool.command.Command;
import org.ancestra.evolutive.tool.plugin.PluginLoader;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class World {

	/**
	 * All data on this fucking case
	 */
	public static World data = new World();
	public static Events events = new Events();
	public static Database database = new Database();

	private Map<Integer, Account> accounts = new HashMap<>();
	private Map<Integer, Player> players = new HashMap<>();
	private Map<Short, Maps> maps = new HashMap<>();
	private Map<Integer, Objet> objects = new HashMap<>();
	private Map<Integer, ExpLevel> expLevels = new HashMap<>();
	private Map<Integer, Spell> spells = new HashMap<>();
	private Map<Integer, ObjTemplate> templateObjects = new HashMap<>();
	private Map<Integer, MobTemplate> templateMobs = new HashMap<>();
	private Map<Integer, NpcTemplate> npcTemplates = new HashMap<>();
	private Map<Integer, NpcQuestion> npcQuestions = new HashMap<>();
	private Map<Integer, NpcAnswer> npcAnswers = new HashMap<>();
	private Map<Integer, InteractiveObjectTemplate> templateIO = new HashMap<>();
	private Map<Integer, Mount> mounts = new HashMap<>();
	private Map<Integer, Continent> Continents = new HashMap<>();
	private Map<Integer, Area> areas = new HashMap<>();
	private Map<Integer, SubArea> subAreas = new HashMap<>();
	private Map<Integer, Job> jobs = new HashMap<>();
	private Map<Integer, ArrayList<Couple<Integer, Integer>>> crafts = new HashMap<>();
	private Map<Integer, ItemSet> setItems = new HashMap<>();
	private Map<Integer, Guild> guilds = new HashMap<>();
	private Map<Integer, HDV> hdvs = new HashMap<>();
	private Map<Integer, Map<Integer, ArrayList<HdvEntry>>> hdvItems = new HashMap<>();
	private Map<Integer, Player> married = new HashMap<>();
	private Map<Integer, Animation> animations = new HashMap<>();
	private Map<Short, MountPark> mountParks = new HashMap<>();
	private Map<Integer, Trunk> trunks = new HashMap<>();
	private Map<Integer, Collector> collectors = new ConcurrentHashMap<>();
	private Map<Integer, House> houses = new HashMap<>();
	private Map<Short, Collection<Integer>> sellers = new HashMap<>();
	private Map<String, Command<Player>> playerCommands = new HashMap<>();
	private Map<String, Command<Console>> consoleCommands = new HashMap<>();
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private Map<String, PacketParser> packetJar = new HashMap<>();
	private Map<String, PacketParser> packetPlugins = new HashMap<>();
	private Map<String, PluginLoader> otherPlugins = new HashMap<>();

	private int nextLigneID; 
	private int saveTries = 1; 
	private short state = 1;
	private byte gmAccess = 0; 
	private int nextObjectID; 
	
	private ExecutorService worker = Executors.newCachedThreadPool();
	
	public int initialize() {
		long startTime = System.currentTimeMillis();
		
		//chargement des donn�es statiques
		database.getOtherData().loadBannedIps();
		database.getOtherData().loadCrafts();
		database.getOtherData().loadZaaps();
		database.getOtherData().loadZaapis();
		database.getExpData().loadAll();
		
		//truc pourri � refaire plus tard
		nextObjectID = database.getItemData().nextId();
		return (int)(System.currentTimeMillis() - startTime);
	}

	public Area getArea(int areaID) {
		Area area = areas.get(areaID);
		if(area == null)
			area = World.database.getAreaData().load(areaID);
		return area;
	}

	public Continent getContinent(int areaID) {
		return Continents.get(areaID);
	}

	public SubArea getSubArea(int areaID) {
		SubArea subArea = subAreas.get(areaID);
		if(subArea == null)
			subArea = World.database.getAreaSubData().load(areaID);
		return subArea;
	}

	public void addArea(Area area) {
		areas.put(area.getId(), area);
	}

	public void addContinent(Continent SA) {
		Continents.put(SA.getId(), SA);
	}

	public void addSubArea(SubArea SA) {
		subAreas.put(SA.getId(), SA);
	}

	public void addNpcAnswer(NpcAnswer rep) {
		npcAnswers.put(rep.getId(), rep);
	}

	public NpcAnswer getNpcAnswer(int guid) {
		NpcAnswer object = npcAnswers.get(guid);
		if(object == null)
			object = World.database.getNpcAnswerData().load(guid);
		return object;
	}

	public int getExpLevelSize() {
		return expLevels.size();
	}

	public void addExpLevel(int lvl, ExpLevel exp) {
		expLevels.put(lvl, exp);
	}

	public Account getCompte(int guid) {
		return accounts.get(guid);
	}

	public void addNpcQuestion(NpcQuestion question) {
		npcQuestions.put(question.getId(), question);
	}

	public NpcQuestion getNpcQuestion(int guid) {
		NpcQuestion object = npcQuestions.get(guid);
		if(object == null)
			object = World.database.getNpcQuestionData().load(guid);
		return object;
	}

	public NpcTemplate getNpcTemplate(int guid) {
		NpcTemplate object = npcTemplates.get(guid);
		if(object == null)
			object = World.database.getNpcTemplateData().load(guid);
		return object;
	}

	public void addNpcTemplate(NpcTemplate temp) {
		npcTemplates.put(temp.getId(), temp);
	}

	public Maps getCarte(short id) {
		Maps map = maps.get(id);
		if(map == null)
			map = World.database.getMapData().load(id);
		return map;
	}

	public void addCarte(Maps map) {
		if (!maps.containsKey(map.getId()))
			maps.put(map.getId(), map);
	}

	public void delCarte(Maps map) {
		if (maps.containsKey(map.getId()))
			maps.remove(map.getId());
	}

	public Account getCompteByName(String name) {
		Account account = null;
		
		for(Account acc: accounts.values())
			if(acc.getName().equalsIgnoreCase(name))
				account = acc;
		
		if(account == null) 
			account = World.database.getAccountData().load(name);
		return account;
	}

	public Player getPersonnage(int guid) {
		return players.get(guid);
	}

	public void addAccount(Account compte) {
		accounts.put(compte.getUUID(), compte);
	}

	public void addPersonnage(Player perso) {
		players.put(perso.getId(), perso);
	}

	public Player getPersoByName(String name) {
		ArrayList<Player> Ps = new ArrayList<Player>();
		Ps.addAll(players.values());
		for (Player P : Ps)
			if (P.getName().equalsIgnoreCase(name))
				return P;
		return null;
	}

	public void deletePerso(Player perso) {
		if (perso.getGuild() != null) {
			if (perso.getGuild().getMembers().size() <= 1) {
				removeGuild(perso.getGuild().getId());
			} else if (perso.getGuildMember().getRank() == 1) {
				int curMaxRight = 0;
				Player Meneur = null;
				for (Player newMeneur : perso.getGuild().getMembers()) {
					if (newMeneur == perso)
						continue;
					if (newMeneur.getGuildMember().getRight() < curMaxRight) {
						Meneur = newMeneur;
					}
				}
				perso.getGuild().removeMember(perso);
				Meneur.getGuildMember().setRank(1);
			} else// Supression simple
			{
				perso.getGuild().removeMember(perso);
			}
		}
		perso.remove();// Supression BDD Perso, items, monture.
		unloadPerso(perso.getId());// UnLoad du perso+item
		players.remove(perso.getId());
	}

	public String getSousZoneStateString() {
		String data = "";
		/* TODO: Sous Zone Alignement */
		return data;
	}

	public long getPersoXpMin(int _lvl) {
		if (_lvl > getExpLevelSize())
			_lvl = getExpLevelSize();
		if (_lvl < 1)
			_lvl = 1;
		return expLevels.get(_lvl).perso;
	}

	public long getPersoXpMax(int _lvl) {
		if (_lvl >= getExpLevelSize())
			_lvl = (getExpLevelSize() - 1);
		if (_lvl <= 1)
			_lvl = 1;
		return expLevels.get(_lvl + 1).perso;
	}

	public void addSort(Spell sort) {
		spells.put(sort.getSpellID(), sort);
	}

	public void addObjTemplate(ObjTemplate obj) {
		templateObjects.put(obj.getID(), obj);
	}

	public Spell getSort(int id) {
		Spell spell = spells.get(id);
		if(spell == null)
			spell = World.database.getSpellData().load(id);
		return spell;
	}

	public ObjTemplate getObjTemplate(int id) {
		ObjTemplate template = templateObjects.get(id);
		if(template == null)
			template = World.database.getItemTemplateData().load(id);
		return template;
	}

	public synchronized int getNewItemGuid() {
		return nextObjectID++;
	}

	public void addMobTemplate(int id, MobTemplate mob) {
		templateMobs.put(id, mob);
	}

	public MobTemplate getMonstre(int id) {
		MobTemplate monster = templateMobs.get(id);
		if(monster == null)
			monster = World.database.getMonsterData().load(id);
		return monster;
	}

	public List<Player> getOnlinePersos() {
		List<Player> online = new ArrayList<Player>();
		for (Entry<Integer, Player> perso : players.entrySet()) {
			if (perso.getValue().isOnline()
					&& perso.getValue().getAccount().getGameClient() != null) {
				if (perso.getValue().getAccount().getGameClient() != null) {
					online.add(perso.getValue());
				}
			}
		}
		return online;
	}

	public void addObjet(Objet item, boolean saveSQL) {
		objects.put(item.getGuid(), item);
		if (saveSQL)
			World.database.getItemData().create(item);
	}

	public Objet getObjet(int guid) {
		Objet item = objects.get(guid);
		if(item == null)
			item = World.database.getItemData().load(guid);
		return item;
	}

	public void removeItem(int guid) {
		Objet o = objects.get(guid);
		objects.remove(guid);
		database.getItemData().delete(o);
	}

	public void addInteractiveObjectTemplate(InteractiveObjectTemplate IOT) {
		templateIO.put(IOT.getId(), IOT);
	}

	public Mount getDragoByID(int id) {
		Mount mount = mounts.get(id);
		if(mount == null)
			mount = World.database.getMountData().load(id);
		return mount;
	}

	public void addDragodinde(Mount DD) {
		mounts.put(DD.getId(), DD);
	}

	public void removeDragodinde(int DID) {
		mounts.remove(DID);
	}

	public void saveData(final int saverID) {
		worker.execute(new Runnable() {
			@Override
			public void run() {
				GameClient _out = null;
				Player saver = saverID != -1 ? getPersonnage(saverID)
						: null;
				if (saver != null)
					_out = saver.getAccount().getGameClient();

				set_state((short) 2);

				try {
					Log.addToLog("Lancement de la sauvegarde du Monde...");
					SocketManager.GAME_SEND_Im_PACKET_TO_ALL("1164");
					Server.config.setSaving(true);

					Log.addToLog("Sauvegarde des personnages...");
					for (Player perso : players.values()) {
						if (!perso.isOnline())
							continue;
						database.getCharacterData().update(perso);
						database.getCharacterData().updateItems(perso);		
					}

					Log.addToLog("Sauvegarde des guildes...");
					for (Guild guilde : guilds.values()) {
						database.getGuildData().update(guilde);
					}

					Log.addToLog("Sauvegarde des percepteurs...");
					for (Collector perco : collectors.values()) {
						if (perco.get_inFight() > 0)
							continue;
						database.getCollectorData().update(perco);
					}

					Log.addToLog("Sauvegarde des maisons...");
					for (House house : houses.values()) {
						if (house.get_owner_id() > 0) {
							database.getHouseData().update(house);
						}
					}

					Log.addToLog("Sauvegarde des coffres...");
					for (Trunk t : trunks.values()) {
						if (t.get_owner_id() > 0) {
							database.getTrunkData().update(t);
						}
					}

					Log.addToLog("Sauvegarde des enclos...");
					for (MountPark mp : mountParks.values()) {
						if (mp.getOwner() > 0 || mp.getOwner() == -1) {
							database.getMountparkData().update(mp);
						}
					}

					Log.addToLog("Sauvegarde des hdvs...");
					for (HDV curHdv : hdvs.values()) {
						database.getHdvData().updateHdvItems(curHdv.getHdvID());
					}

					Log.addToLog("Sauvegarde effectuee !");

					set_state((short) 1);
					// TODO : Rafraichir

				} catch (ConcurrentModificationException e) {
					if (saveTries < 10) {
						Log.addToLog("Nouvelle tentative de sauvegarde");
						if (saver != null && _out != null)
							SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(
									_out,
									"Erreur. Nouvelle tentative de sauvegarde");
						saveTries++;
						saveData(saver.getId());
					} else {
						set_state((short) 1);
						// TODO : Rafraichir
						String mess = "Echec de la sauvegarde apres "
								+ saveTries + " tentatives";
						if (saver != null && _out != null)
							SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(
									_out, mess);
						Log.addToLog(mess);
					}
				} catch (Exception e) {
					Log.addToLog("Erreur lors de la sauvegarde : "
							+ e.getMessage());
					e.printStackTrace();
				} finally {
					Server.config.setSaving(false);
					saveTries = 1;
					SocketManager.GAME_SEND_Im_PACKET_TO_ALL("1165");
				}
			}
		});
	}

	public void RefreshAllMob() {
		SocketManager.GAME_SEND_MESSAGE_TO_ALL(
				"Recharge des Mobs en cours, des latences peuvent survenir.",
				Server.config.getMotdColor());
		for (Maps map : maps.values()) {
			map.refreshSpawns();
		}
		SocketManager
				.GAME_SEND_MESSAGE_TO_ALL(
						"Recharge des Mobs finie. La prochaine recharge aura lieu dans 5heures.",
						Server.config.getMotdColor());
	}

	public ExpLevel getExpLevel(int lvl) {
		ExpLevel level = expLevels.get(lvl);
		if(level == null)
			return World.database.getExpData().load(lvl);
		return expLevels.get(lvl);
	}

	public InteractiveObjectTemplate getInteractiveObjectTemplate(int id) {
		InteractiveObjectTemplate template = templateIO.get(id);
		if(template == null)
			template = World.database.getIoTemplates().load(id);
		return template;
	}

	public Job getMetier(int id) {
		Job job = jobs.get(id);
		if(job == null)
			job = World.database.getJobData().load(id);
		return job;
	}

	public void addJob(Job job) {
		jobs.put(job.getId(), job);
	}

	public void addCraft(int id, ArrayList<Couple<Integer, Integer>> m) {
		crafts.put(id, m);
	}

	public ArrayList<Couple<Integer, Integer>> getCraft(int i) {
		return crafts.get(i);
	}

	public int getObjectByIngredientForJob(ArrayList<Integer> list,
			Map<Integer, Integer> ingredients) {
		if (list == null)
			return -1;
		for (int tID : list) {
			ArrayList<Couple<Integer, Integer>> craft = getCraft(tID);
			if (craft == null) {
				Log.addToLog("/!\\Recette pour l'objet " + tID
						+ " non existante !");
				continue;
			}
			if (craft.size() != ingredients.size())
				continue;
			boolean ok = true;
			for (Couple<Integer, Integer> c : craft) {
				// si ingredient non pr�sent ou mauvaise quantit�
				if (ingredients.get(c.first) != c.second)
					ok = false;
			}
			if (ok)
				return tID;
		}
		return -1;
	}

	public Account getCompteByPseudo(String p) {
		for (Account C : accounts.values())
			if (C.getPseudo().equals(p))
				return C;
		return null;
	}

	public void addItemSet(ItemSet itemSet) {
		setItems.put(itemSet.getId(), itemSet);
	}

	public ItemSet getItemSet(int tID) {
		ItemSet set = setItems.get(tID);
		if(set == null)
			set = World.database.getItemSetData().load(tID);
		return set;
	}

	public int getItemSetNumber() {
		return setItems.size();
	}

	public int getNextIdForMount() {
		int max = 1;
		for (int a : mounts.keySet())
			if (a > max)
				max = a;
		return max + 1;
	}

	public void addGuild(Guild g, boolean save) {
		guilds.put(g.getId(), g);
		if (save)
			database.getGuildData().create(g);
	}

	public int getNextHighestGuildID() {
		if (guilds.isEmpty())
			return 1;
		int n = 0;
		for (int x : guilds.keySet())
			if (n < x)
				n = x;
		return n + 1;
	}

	public boolean guildNameIsUsed(String name) {
		return database.getGuildData().exist(name);
	}

	public boolean guildEmblemIsUsed(String emb) {
		for (Guild g : guilds.values()) {
			if (g.getEmblem().equals(emb))
				return true;
		}
		return false;
	}

	public Guild getGuild(int id) {
		Guild guild = guilds.get(id);
		if(guild == null)
			guild = World.database.getGuildData().load(id);
		return guild;
	}

	public long getGuildXpMax(int _lvl) {
		if (_lvl >= 200)
			_lvl = 199;
		if (_lvl <= 1)
			_lvl = 1;
		return expLevels.get(_lvl + 1).guilde;
	}

	public void ReassignAccountToChar(Account C) {
		C.getPlayers().clear();
		database.getCharacterData().load(C);
		for (Player P : players.values()) {
			if (P.getAccount().getUUID() == C.getUUID()) {
				P.setAccount(C);
			}
		}
	}

	public int getZaapCellIdByMapId(short i) {
		for (Entry<Integer, Integer> zaap : Constants.ZAAPS.entrySet()) {
			if (zaap.getKey() == i)
				return zaap.getValue();
		}
		return -1;
	}

	public int getEncloCellIdByMapId(short i) {
		if (getCarte(i).getMountPark() != null) {
			if (getCarte(i).getMountPark().getCellid() > 0) {
				return getCarte(i).getMountPark().getCellid();
			}
		}

		return -1;
	}

	public void delDragoByID(int getId) {
		mounts.remove(getId);
	}

	public void removeGuild(int id) {
		// Maison de guilde+SQL
		House.removeHouseGuild(id);
		// Enclo+SQL
		MountPark.remove(id);
		// Percepteur+SQL
		Collector.removePercepteur(id);
		// Guilde
		Guild g = guilds.get(id);
		guilds.remove(id);
		
		database.getGuildMemberData().deleteAllByGuild(id);
		database.getGuildData().delete(g);
	}

	public boolean ipIsUsed(String ip) {
		for (Account c : accounts.values())
			if (c.getCurIp().compareTo(ip) == 0)
				return true;
		return false;
	}

	public void unloadPerso(int g) {
		Player toRem = players.get(g);
		if (!toRem.getItems().isEmpty()) {
			for (Entry<Integer, Objet> curObj : toRem.getItems().entrySet()) {
				objects.remove(curObj.getKey());
			}
		}
		toRem = null;
	}

	public boolean isArenaMap(int id) {
		for (int curId : Server.config.getArenaMaps())
			if (curId == id)
				return true;
		return false;
	}
	
	public boolean isMarchandMap(int id) {
		for (int curId : Server.config.getMarchandMaps())
			if (curId == id)
				return true;
		return false;
	}
	
	public boolean isCollectorMap(int id) {
		for (int curId : Server.config.getCollectorMaps())
			if (curId == id)
				return true;
		return false;
	}

	public Objet newObjet(int Guid, int template, int qua, int pos,
			String strStats) {
		if (getObjTemplate(template) == null) {
			Console.instance.println("ItemTemplate " + template
					+ " inexistant, GUID dans la table `items`:" + Guid);
			Main.closeServers();
		}

		if (getObjTemplate(template).getType() == 85)
			return new PierreAme(Guid, qua, template, pos, strStats);
		else
			return new Objet(Guid, template, qua, pos, strStats);
	}

	public short get_state() {
		return state;
	}

	public void set_state(short state) {
		this.state = state;
	}

	public byte getGmAccess() {
		return gmAccess;
	}

	public void setGmAccess(byte GmAccess) {
		gmAccess = GmAccess;
	}

	public HDV getHdv(int mapID) {
		HDV object = hdvs.get(mapID);
		if(object == null)
			object = World.database.getHdvData().load(mapID);
		return object;
	}
	
	/**
	 * @return The next line id (with incrementation).
	 * @deprecated Do not use this function anyhow.
	 */
	@Deprecated
	public synchronized int getNextLigneID() {
		nextLigneID++;
		return nextLigneID;
	}

	public synchronized void setNextLigneID(int ligneID) {
		nextLigneID = ligneID;
	}

	public void addHdvItem(int compteID, int hdvID, HdvEntry toAdd) {
		if (hdvItems.get(compteID) == null) // Si le compte n'est pas dans la
											// memoire
			hdvItems.put(compteID, new HashMap<Integer, ArrayList<HdvEntry>>()); 

		if (hdvItems.get(compteID).get(hdvID) == null)
			hdvItems.get(compteID).put(hdvID, new ArrayList<HdvEntry>());

		hdvItems.get(compteID).get(hdvID).add(toAdd);
	}

	public void removeHdvItem(int compteID, int hdvID, HdvEntry toDel) {
		hdvItems.get(compteID).get(hdvID).remove(toDel);
	}

	public int getHdvNumber() {
		return hdvs.size();
	}

	public int getHdvObjetsNumber() {
		int size = 0;

		for (Map<Integer, ArrayList<HdvEntry>> curCompte : hdvItems.values()) {
			for (ArrayList<HdvEntry> curHdv : curCompte.values()) {
				size += curHdv.size();
			}
		}
		return size;
	}

	public void addHdv(HDV toAdd) {
		hdvs.put(toAdd.getHdvID(), toAdd);
	}

	public Map<Integer, ArrayList<HdvEntry>> getMyItems(int compteID) {
		if (hdvItems.get(compteID) == null)// Si le compte n'est pas dans la memoire
			hdvItems.put(compteID, new HashMap<Integer, ArrayList<HdvEntry>>());
		return hdvItems.get(compteID);
	}

	public Collection<ObjTemplate> getObjTemplates() {
		return templateObjects.values();
	}

	public Player getMarried(int ordre) {
		return married.get(ordre);
	}

	public void AddMarried(int ordre, Player perso) {
		Player Perso = married.get(ordre);
		if (Perso != null) {
			if (perso.getId() == Perso.getId()) // Si c'est le meme
														// joueur...
				return;
			if (Perso.isOnline())// Si perso en ligne...
			{
				married.remove(ordre);
				married.put(ordre, perso);
				return;
			}

			return;
		} else {
			married.put(ordre, perso);
			return;
		}
	}

	public void PriestRequest(Player perso, Maps carte, int IdPretre) {
		Player Homme = married.get(0);
		Player Femme = married.get(1);
		if (Homme.getWife() != 0) {
			SocketManager.GAME_SEND_MESSAGE_TO_MAP(carte, Homme.getName()
					+ " est deja marier!", Server.config.getMotdColor());
			return;
		}
		if (Femme.getWife() != 0) {
			SocketManager.GAME_SEND_MESSAGE_TO_MAP(carte, Femme.getName()
					+ " est deja marier!", Server.config.getMotdColor());
			return;
		}
		SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(perso.getMap(), "", -1,
				"Pr�tre", perso.getName()
						+ " acceptez-vous d'�pouser "
						+ getMarried((perso.getSex() == 1 ? 0 : 1))
								.getName() + " ?");
		SocketManager.GAME_SEND_WEDDING(carte, 617,
				(Homme == perso ? Homme.getId() : Femme.getId()),
				(Homme == perso ? Femme.getId() : Homme.getId()),
				IdPretre);
	}

	public void Wedding(Player Homme, Player Femme, int isOK) {
		if (isOK > 0) {
			SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(Homme.getMap(), "",
					-1, "Pr�tre", "Je d�clare " + Homme.getName() + " et "
							+ Femme.getName()
							+ " unis par les liens sacr�s du mariage.");
			Homme.MarryTo(Femme);
			Femme.MarryTo(Homme);
		} else {
			SocketManager.GAME_SEND_Im_PACKET_TO_MAP(Homme.getMap(),
					"048;" + Homme.getName() + "~" + Femme.getName());
		}
		married.get(0).setIsOK(0);
		married.get(1).setIsOK(0);
		married.clear();
	}

	public Animation getAnimation(int AnimationId) {
		Animation animation = animations.get(AnimationId);
		if(animation == null)
			animation = World.database.getAnimationData().load(AnimationId);
		return animation;
	}

	public void addAnimation(Animation animation) {
		animations.put(animation.getId(), animation);
	}

	public void addHouse(House house) {
        if(house != null) {
            houses.put(house.get_id(), house);
        }
	}

	public Map<Integer, House> getHouses() {
		return houses;
	}

	public House getHouse(int id) {
		return houses.get(id);
	}

	public void addPerco(Collector perco) {
		collectors.put(perco.getId(), perco);
	}

	public Collector getPerco(int percoID) {
		Collector collector = collectors.get(percoID);
		if(collector == null)
			collector = World.database.getCollectorData().load(percoID);
		return collector;
	}

    public Collector getCollector(Maps map){
        for(Collector collector : collectors.values()){
            if(collector.getMap().equals(map)){
                return collector;
            }
        }
        return World.database.getCollectorData().loadByMap(map.getId());
    }

	public Map<Integer, Collector> getPercos() {
		return collectors;
	}

	public void addTrunk(Trunk trunk) {
		trunks.put(trunk.get_id(), trunk);
	}

	public Trunk getTrunk(int id) {
		return trunks.get(id);
	}

	public Map<Integer, Trunk> getTrunks() {
		return trunks;
	}

	public void addMountPark(MountPark mp) {
		mountParks.put(mp.getMap().getId(), mp);
	}

	public Map<Short, MountPark> getMountPark() {
		
		return mountParks;
	}
	
	public MountPark getMountPark(int mapid) {
		MountPark map = mountParks.get(mapid);
		if(map == null)
			map = World.database.getMountparkData().load(mapid);
		return map;
	}

	public String parseMPtoGuild(int GuildID) {
		Guild G = getGuild(GuildID);
		byte enclosMax = (byte) Math.floor(G.getLevel() / 10);
		StringBuilder packet = new StringBuilder();
		packet.append(enclosMax);

		for (Entry<Short, MountPark> mp : mountParks.entrySet()) {
			if (mp.getValue().getGuild() != null
					&& mp.getValue().getGuild().getId() == GuildID) {
				packet.append("|").append(mp.getValue().getMap().getId())
					.append(";").append(mp.getValue().getSize())
					.append(";").append(mp.getValue().getObjectNumb());
			} else {
				continue;
			}
		}
		return packet.toString();
	}

	public int totalMPGuild(int GuildID) {
		int i = 0;
		for (Entry<Short, MountPark> mp : mountParks.entrySet()) {
			if (mp.getValue().getGuild().getId() == GuildID) {
				i++;
			} else {
				continue;
			}
		}
		return i;
	}

	public void addSeller(int id, short map) {
		if (sellers.get(map) == null) {
			ArrayList<Integer> players = new ArrayList<Integer>();
			players.add(id);
			sellers.put(map, players);
		} else {
			ArrayList<Integer> players = new ArrayList<Integer>();
			players.addAll(sellers.get(map));
			players.add(id);
			sellers.remove(map);
			sellers.put(map, players);
		}
	}

	public Collection<Integer> getSeller(short mapID) {
		return sellers.get(mapID);
	}

	public void removeSeller(int pID, short mapID) {
		sellers.get(mapID).remove(pID);
	}

	public Map<String, Command<Player>> getPlayerCommands() {
		return playerCommands;
	}

	public Map<String, Command<Console>> getConsoleCommands() {
		return consoleCommands;
	}

	public ScheduledExecutorService getScheduler() {
		return scheduler;
	}
	
	/**
	 * @return All packets of the jar file compiled.
	 * @deprecated Do not use this function, only for the emulator. Please use : {@link #getPacketPlugins()}
	 */
	@Deprecated
	public Map<String, PacketParser> getPacketJar() {
		return packetJar;
	}
	
	public Map<String, PacketParser> getPacketPlugins() {
		return packetPlugins;
	}
	
	public void addPacketPlugins(String packet, PacketParser packetParser) {
		if(packet == null || packetParser == null) {
			Console.instance.writeln(" > The packet or packet parser was null.");
			return;
		}
		if(this.packetPlugins.containsKey(packet)) {
			Console.instance.writeln(" > The packet " + packet + " already exists and has been replaced.");
			this.packetPlugins.remove(packet);
		}
		this.packetPlugins.put(packet, packetParser);
	}
	
	public Map<String, PluginLoader> getOtherPlugins() {
		return otherPlugins;
	}
	
	public String valueOfPacket(Class<?> zClass) {
		Annotation annotation = zClass.getAnnotation(Packet.class);
		if(annotation == null)
			return null;
		if(annotation instanceof Packet) {
			Packet name = (Packet) annotation;
			return name.value();
		}
		return null;
	}
	
	public Map<Integer, Player> getPlayers() {
		return this.players;
	}
	
	public Map<Integer, Account> getAccounts() {
		return this.accounts;
	}

	public ExecutorService getWorker() {
		return worker;
	}


}
