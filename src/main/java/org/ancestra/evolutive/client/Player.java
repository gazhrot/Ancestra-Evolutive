package org.ancestra.evolutive.client;

import org.ancestra.evolutive.client.other.Group;
import org.ancestra.evolutive.client.other.Stalk;
import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.*;
import org.ancestra.evolutive.entity.Creature;
import org.ancestra.evolutive.entity.Mount;
import org.ancestra.evolutive.enums.Classe;
import org.ancestra.evolutive.event.player.PlayerJoinEvent;
import org.ancestra.evolutive.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.spell.SpellEffect;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.game.GameAction;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.guild.GuildMember;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.house.Trunk;
import org.ancestra.evolutive.job.Job;
import org.ancestra.evolutive.job.JobAction;
import org.ancestra.evolutive.job.JobConstant;
import org.ancestra.evolutive.job.JobStat;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.InteractiveObject;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.map.MountPark;
import org.ancestra.evolutive.object.ObjectSet;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.other.Exchange;
import org.ancestra.evolutive.tool.time.waiter.Waiter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class Player extends Creature {

	private int sex;
	private Classe classe;
	private int color1;
	private int color2;
	private int color3;
	private long kamas;
	private int spellPoints;
	private int capital;
	private int energy;
	private int level;
	private long experience;
	private int size;
	private int gfx;
	
	private Account account;
	private Maps curMap;
	private Case curCell;
	private Stats stats;
	private Fight fight;
	private Group group;
	private Stalk stalk;
	private GuildMember guildMember;
	private Mount mount;
	private MountPark curMountPark;
	private Trunk curTrunk;
	private House curHouse;
	private JobAction curJobAction;
	private Exchange curExchange;
	
	/** Alignement **/
	private byte align = 0;
	private int deshonor = 0;
	private int honor = 0;
	private boolean showWings = false;
	private int aLvl = 0;
	
	/** Spell **/
	private boolean seeSpell = false;
	private boolean isForgetingSpell = false;
	private Map<Integer, SpellStats> spells = new TreeMap<>();
	private Map<Integer, Character> spellsPlace = new TreeMap<>();
	
	/** Life **/
	private int pdv;
	private int maxPdv;
	private boolean sitted;
	
	/** is... **/
    private int regenRate = 2000;
    private long regenTime = -1;//-1 veut dire que la personne ne c'est jamais connecte
	private boolean isOnline  = false;
	private boolean isInBank;
	private boolean isInAction;//DoAction job
	private boolean isAway;
	private boolean isAbsent = false;
	private boolean isInvisible = false;
	private boolean isZaaping = false;
	private boolean isClone = false;
	private boolean isGhosts = false;
	private boolean isReady = false;
	private boolean isOnMount = false;
	
	private int isTradingWith = 0;
	private int isTalkingWith = 0;
	private int isOnCollector = 0;
	private int isOK = 0;
	
	
	/** Other **/
	private Waiter waiter = new Waiter();
	private Player follow = null;
	private boolean needEndFightAction;
	private boolean showFriendConnection;
	private boolean canAggro = true;
	private boolean seeSeller = false;
	private String emotes = "7667711";
	private String canaux;
	private String savePos;
	private int emoteActive = 0;
	private int inviting = 0;
	private int mountXp = 0;
	private int speed = 0;
	private int wife = 0;	
	private int duel = -1;
	private byte title = 0;
	
	protected long lastPacketTime;
	
	public Map<Integer,Player> followers = new TreeMap<>();
	private ArrayList<Integer> zaaps = new ArrayList<>();
	private Map<Integer, SpellEffect> buffs = new TreeMap<>(); 
	private Map<Integer, Object> objects = new TreeMap<>();
	private Map<Integer, JobStat> jobs = new TreeMap<>();
	private Map<Integer , Integer> stores = new TreeMap<>();//<ObjID, Prix>	
	
	public Player(int UUID, String name, int sex, int classe, int color1, int color2, int color3,long kamas, int spellPoints,
		int capital, int energy, int level, long experience, int size, int gfx, byte align, int account, Map<Integer,Integer> stats,
		byte showFriendConnection, byte showWings, byte seeSeller, String canaux, short curMap, int curCell, String stuff, String store,
		int pdvPer, String spells, String savePos, String jobs, int mountXp, int mount, int honor, int deshonor, int aLvl, String zaaps, byte title, int wife)
	{
        super(UUID,name,curMap,curCell);
		this.sex = sex;
		this.classe = Classe.values()[classe-1];
		this.color1 = color1;
		this.color2 = color2;
		this.color3 = color3;
		this.kamas = kamas;
		this.spellPoints = spellPoints;
		this.capital = capital;
		this.energy = energy;
		this.level = level;
		this.experience = experience;
		this.size = size;
		this.gfx = gfx;
		this.align = align;
		this.account = World.data.getCompte(account);
		this.stats = new Stats(stats, true, this);
		this.showFriendConnection = showFriendConnection==1;
		this.showWings = (this.getAlign() != 0 ? showWings==1 : false);
		/** FIXME: SeeSeller **/
		this.canaux = canaux;
		this.setMapAndCell(curMap, curCell);
		this.setStuff(stuff);
		this.setStore(store);
		this.maxPdv = (this.level - 1) * 5 + Constants.getBasePdv(this.classe.getId()) + getTotalStats().getEffect(Constants.STATS_ADD_VITA);
		this.pdv = (this.maxPdv * pdvPer) / 100;
        if(!Server.regenLifeWhenOffline){
            regenTime = System.currentTimeMillis();
        } else {
            if(regenTime == -1) {
                regenTime = Server.startTime;
            }
        }
		
		this.parseSpells(spells);
		this.savePos = savePos;
		this.setJob(jobs);
		this.mountXp = mountXp;
		this.mount = (mount != 1 ? World.data.getDragoByID(mount) : null);
		this.honor = honor;
		this.deshonor = deshonor;
		this.aLvl = aLvl;
		for(String id: zaaps.split(",")) {
			try	{
				this.zaaps.add(Integer.parseInt(id));
			} catch(Exception e) {}
		}
		this.title = title;
		this.wife = wife; 
			if(this.energy == 0) 
			this.setGhosts();

        helper = new PlayerHelper(this);

	}
	
	public Player(Player player,int id){
        super(id,player.getName(),player.getMap(),player.getCell());
		this.sex = player.getSex();
		this.classe = player.getClasse();
		this.color1 = player.getColor1();
		this.color2 = player.getColor2();
		this.color3 = player.getColor3();
		this.level = player.getLevel();
		
		this.size = player.getSize();
		this.gfx = player.getGfx();
		this.stats = player.getStats();
		this.setStuff(player.getStuffStats().parseToItemSetStats());
		this.maxPdv = player.getMaxPdv();
		this.pdv = player.getPdv();
		this.showWings = player.isShowWings();
		this.mount = player.getMount();
		this.aLvl = player.getaLvl();
		this.align = player.getAlign();
        this.account = player.getAccount();
        helper = new PlayerHelper(this);
    }
	
	public static Player create(String name, int sex, int classe, int color1, int color2, int color3, Account compte) {
		String zaaps = "";
		if(Server.config.isAllZaaps()) 
			for(Entry<Integer, Integer> i : Constants.ZAAPS.entrySet())
				zaaps += (zaaps.length() != 0 ? "," : "") + i.getKey();
        Player perso = new Player(
				World.database.getCharacterData().nextId(),
				name,
				sex,
				classe,
				color1,
				color2,
				color3,
				Server.config.getStartKamas(),
				((Server.config.getStartLevel()-1)*1),
				((Server.config.getStartLevel()-1)*5),
				10000,
				Server.config.getStartLevel(),
				World.data.getPersoXpMin(Server.config.getStartLevel()),
				100,
				Integer.parseInt(classe+""+sex),
				(byte)0,
				compte.getUUID(),
				new TreeMap<Integer,Integer>(),
				(byte)1,
				(byte)0,
				(byte)0,
				"*#%!pi$:?",
				Constants.getStartMap(classe),
				Constants.getStartCell(classe),
				"",
				"",
				100,
				"",
				"10298,314",
				"",
				0,
				-1,
				0,
				0,
				0,
				zaaps,
				(byte)0,
				0
				);
		perso.spells = Constants.getStartSorts(classe);
		
		for(int a = 1; a <= perso.getLevel(); a++)
			Constants.onLevelUpSpells(perso, a);
		
		perso.spellsPlace = Constants.getStartSortsPlaces(classe);
        if(!World.database.getCharacterData().create(perso))
			return null;
		
		World.data.addPlayer(perso);
		return perso;
	}

	public int getSex() {
		return sex;
	}
	
	public void setSex(int sex) {
		this.sex = sex;
		World.database.getCharacterData().updateSex(this);
	}

	public Classe getClasse() {
		return classe;
	}

	public int getColor1() {
		return color1;
	}

	public int getColor2() {
		return color2;
	}

	public int getColor3() {
		return color3;
	}

	public long getKamas() {
		return kamas;
	}

	public void setKamas(long kamas) {
		this.kamas = kamas;
	}

	public int getSpellPoints() {
		return spellPoints;
	}

	public void setSpellPoints(int spellPoints) {
		this.spellPoints = spellPoints;
	}

	public int getCapital() {
		return capital;
	}

	public void setCapital(int capital) {
		this.capital = capital;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getExperience() {
		return experience;
	}

	public void setExperience(long experience) {
		this.experience = experience;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getGfx() {
		return gfx;
	}

	public void setGfx(int gfx) {
		this.gfx = gfx;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Stats getStats() {
		return stats;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public Fight getFight() {
		return fight;
	}

	public void setFight(Fight fight) {
        refreshLife();
        this.fight = fight;
    }

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
	public Stalk getStalk() {
		return stalk;
	}
	
	public void setStalk(Stalk stalk) {
		this.stalk = stalk;
	}
	
	public Guild getGuild() {
		return (this.getGuildMember() != null ? this.getGuildMember().getGuild() : null);
	}

	public GuildMember getGuildMember() {
		return guildMember;
	}

	public void setGuildMember(GuildMember guildMember) {
		this.guildMember = guildMember;
	}

	public Mount getMount() {
		return mount;
	}

	public void setMount(Mount mount) {
		this.mount = mount;
	}

	public MountPark getCurMountPark() {
		return curMountPark;
	}

	public void setCurMountPark(MountPark curMountPark) {
		this.curMountPark = curMountPark;
	}

	public Trunk getCurTrunk() {
		return curTrunk;
	}

	public void setCurTrunk(Trunk curTrunk) {
		this.curTrunk = curTrunk;
	}

	public House getCurHouse() {
		return curHouse;
	}

	public void setCurHouse(House curHouse) {
		this.curHouse = curHouse;
	}
	
	public JobAction getCurJobAction() {
		return curJobAction;
	}
	
	public void setCurJobAction(JobAction curJobAction) {
		this.curJobAction = curJobAction;
	}

	public Exchange getCurExchange() {
		return curExchange;
	}

	public void setCurExchange(Exchange curExchange) {
		this.curExchange = curExchange;
	}

	public byte getAlign() {
		return align;
	}

	public void setAlign(byte align) {
		this.align = align;
	}

	public int getDeshonor() {
		return deshonor;
	}

	public void setDeshonor(int deshonor) {
		this.deshonor = deshonor;
	}

	public int getHonor() {
		return honor;
	}

	public void setHonor(int honor) {
		this.honor = honor;
	}

	public boolean isShowWings() {
		return showWings;
	}

	public void setShowWings(boolean showWings) {
		this.showWings = showWings;
	}

	public int getaLvl() {
		return aLvl;
	}

	public void setaLvl(int aLvl) {
		this.aLvl = aLvl;
	}

	public Map<Integer, SpellStats> getSpells() {
		return spells;
	}

	public void setSpells(Map<Integer, SpellStats> spells) {
		this.spells = spells;
	}

	public Map<Integer, Character> getSpellsPlace() {
		return spellsPlace;
	}

	public void setSpellsPlace(Map<Integer, Character> spellsPlace) {
		this.spellsPlace = spellsPlace;
	}

	public boolean isSeeSpell() {
		return seeSpell;
	}

	public void setSeeSpell(boolean seeSpell) {
		this.seeSpell = seeSpell;
	}

	public void setForgetingSpell(boolean isForgetingSpell) {
		this.isForgetingSpell = isForgetingSpell;
	}
	
	public boolean isForgetingSpell() {
		return isForgetingSpell;
	}
	
	public int getPdv() {
        refreshLife();
		return pdv;
	}

	public void setPdv(int pdv) {
		this.pdv = pdv<getMaxPdv()?pdv:maxPdv;
		if(this.getGroup() != null)
			SocketManager.GAME_SEND_PM_MOD_PACKET_TO_GROUP(this.getGroup(), this);
	}

	public int getMaxPdv() {
		return maxPdv;
	}

	public void setMaxPdv(int maxPdv) {
		this.maxPdv = maxPdv;
		if(this.getGroup() != null)
			SocketManager.GAME_SEND_PM_MOD_PACKET_TO_GROUP(this.getGroup(), this);
	}
	
	public void fullPDV() {
		this.pdv = this.maxPdv;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public boolean isInBank() {
		return isInBank;
	}

	public void setInBank(boolean isInBank) {
		this.isInBank = isInBank;
	}

	public boolean isInAction() {
		return isInAction;
	}

	public void setInAction(boolean isInAction) {
		this.isInAction = isInAction;
	}

	public boolean isAway() {
		return isAway;
	}

	public void setAway(boolean isAway) {
		this.isAway = isAway;
	}

	public boolean isAbsent() {
		return isAbsent;
	}

	public void setAbsent(boolean isAbsent) {
		this.isAbsent = isAbsent;
	}

	public boolean isInvisible() {
		return isInvisible;
	}

	public void setInvisible(boolean isInvisible) {
		this.isInvisible = isInvisible;
	}

	public boolean isZaaping() {
		return isZaaping;
	}

	public void setZaaping(boolean isZaaping) {
		this.isZaaping = isZaaping;
	}

	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
	}

	public boolean isGhosts() {
		return isGhosts;
	}

	public void setGhosts(boolean isGhosts) {
		this.isGhosts = isGhosts;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public boolean isOnMount() {
		return isOnMount;
	}

	public void setOnMount(boolean isOnMount) {
		this.isOnMount = isOnMount;
	}

	public int getIsTradingWith() {
		return isTradingWith;
	}

	public void setIsTradingWith(int isTradingWith) {
		this.isTradingWith = isTradingWith;
	}

	public int getIsTalkingWith() {
		return isTalkingWith;
	}

	public void setIsTalkingWith(int isTalkingWith) {
		this.isTalkingWith = isTalkingWith;
	}

	public int getIsOnCollector() {
		return isOnCollector;
	}

	public void setIsOnCollector(int isOnCollector) {
		this.isOnCollector = isOnCollector;
	}

	public int getIsOK() {
		return isOK;
	}

	public void setIsOK(int isOK) {
		this.isOK = isOK;
	}

	public Player getFollow() {
		return follow;
	}

	public void setFollow(Player follow) {
		this.follow = follow;
	}

	public boolean isNeedEndFightAction() {
		return needEndFightAction;
	}

	public void setNeedEndFightAction(boolean needEndFightAction) {
		this.needEndFightAction = needEndFightAction;
	}

	public boolean isShowFriendConnection() {
		return showFriendConnection;
	}

	public void setShowFriendConnection(boolean showFriendConnection) {
		this.showFriendConnection = showFriendConnection;
	}

	public boolean isCanAggro() {
		return canAggro;
	}

	public void setCanAggro(boolean canAggro) {
		this.canAggro = canAggro;
	}

	public boolean isSeeSeller() {
		return seeSeller;
	}

	public void setSeeSeller(boolean seeSeller) {
		this.seeSeller = seeSeller;
	}

	public String getEmotes() {
		return emotes;
	}

	public void setEmotes(String emotes) {
		this.emotes = emotes;
	}

	public String getCanaux() {
		return canaux;
	}

	public void setCanaux(String canaux) {
		this.canaux = canaux;
	}

	public String getSavePos() {
		return savePos;
	}

	public void setSavePos(String savePos) {
		this.savePos = savePos;
	}

    /**
     * Creer l emote pour le joueur
     * 0 signifie un reset des emotes
     * Se charge de changer les timers lors d un sit
     * @param emote
     * @return true si l'action vient d etre activer
     * false sinon
     */
	public boolean setEmoteActive(int emote) {
        setSitted(false);
        if(this.emoteActive == emote){
            emoteActive = 0;
            return false;
        }
        else {
            emoteActive = emote;
            if(emote ==1 || emote == 19){
                setSitted(true);
            }
            return true;
        }
    }

	public int getInviting() {
		return inviting;
	}

	public void setInviting(int inviting) {
		this.inviting = inviting;
	}

	public int getMountXp() {
		return mountXp;
	}

	public void setMountXp(int mountXp) {
		this.mountXp = mountXp;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getWife() {
		return wife;
	}

	public void setWife(int wife) {
		this.wife = wife;
	}

	public int getDuel() {
		return duel;
	}

	public void setDuel(int duel) {
		this.duel = duel;
	}

	public byte getTitle() {
		return title;
	}

	public void setTitle(byte title) {
		this.title = title;
	}

	public long getLastPacketTime() {
		return lastPacketTime;
	}

	public void setLastPacketTime(long lastPacketTime) {
		this.lastPacketTime = lastPacketTime;
	}

	public Waiter getWaiter() {
		return waiter;
	}

	public Map<Integer, Player> getFollowers() {
		return followers;
	}

	public ArrayList<Integer> getZaaps() {
		return zaaps;
	}
	
	public void addZaap(int mapId) {
		if(!this.zaaps.contains(Integer.valueOf(mapId)))	{
			this.zaaps.add(mapId);
			SocketManager.GAME_SEND_Im_PACKET(this, "024");
			World.database.getCharacterData().update(this);
		}
	}

	public Map<Integer, SpellEffect> getBuffs() {
		return buffs;
	}

	public Map<Integer, Object> getObjects() {
		return objects;
	}

	public Map<Integer, JobStat> getJobs() {
		return jobs;
	}

	public Map<Integer, Integer> getStores() {
		return stores;
	}
	
	public void setMapAndCell(short curMap, int curCell) {
		this.curMap = World.data.getMap(curMap);
		if(this.curMap == null && World.data.getMap(Server.config.getStartMap()) != null)	{
			this.curMap = World.data.getMap(Server.config.getStartMap());
			this.curCell = this.curMap.getCases().get(Server.config.getStartCell());
		}else 
		if (this.curMap == null && World.data.getMap(Server.config.getStartMap()) == null) {
			Console.instance.writeln(" > Le personnage " + this.getName() + " se trouve sur une map incorrecte.");
			Main.closeServers();
		}else 
		if(this.curMap != null)	{
			this.curCell = this.curMap.getCases().get(curCell);
			if(this.curCell == null) {
				this.curMap = World.data.getMap(Server.config.getStartMap());
				this.curCell = this.curMap.getCases().get(Server.config.getStartCell());
			}
		}
		
		if(this.curMap == null || this.curCell == null)	{
			Console.instance.writeln(" > Le personnage " + this.getName() + " se trouve sur une map ou une cellule incorrecte.");
			System.exit(1);
			return;
		}
	}
	
	public void setStuff(String stuff) {
		if(!stuff.equals("")) {
			if(stuff.charAt(stuff.length() - 1) == '|')
				stuff = stuff.substring(0, stuff.length() - 1);
			World.database.getItemData().load(stuff.replace("|", ","));
		}
		for(String data: stuff.split("\\|")) {
			if(data.equals(""))
				continue;
			
			String[] infos = data.split(":");
			int id = 0;
			
			try	{
				id = Integer.parseInt(infos[0]);
			} catch(Exception e) {
				continue;
			}
			
			Object object = World.data.getObject(id);
			
			if(object == null)
				continue;
			
			this.objects.put(object.getId(), object);
		}
	}
	
	public void setStore(String store) {
		if(!store.equals(""))	{
			for(String data: store.split("\\|")) {
				String[] infos = data.split(",");
				int id = 0, price = 0;
				
				try	{
					id = Integer.parseInt(infos[0]);
					price = Integer.parseInt(infos[1]);
				} catch(Exception e) {
					continue;
				}
				
				Object object = World.data.getObject(id);
				
				if(object == null)
					continue;
				
				this.stores.put(object.getId(), price);
			}
		}
	}
	
	public void setJob(String jobs) {
		if(!jobs.equals("")) {
			for(String data : jobs.split(";")) {
				String[] infos = data.split("\\,");
				try	{
					int id = Integer.parseInt(infos[0]);
					long exp = Long.parseLong(infos[1]);
					Job job = World.data.getMetier(id);
					this.jobs.get(learnJob(job)).addXp(this, exp);
				} catch(Exception e) {
					e.getStackTrace();
				}
			}
		}
	}
		
	public String parseSpellsToDb()
	{
		StringBuilder sorts = new StringBuilder();
		if(this.spells.isEmpty())
			return "";
		for(int key : this.spells.keySet())	{
			SpellStats SS = this.spells.get(key);
			sorts.append(SS.getSpellID()).append(";").append(SS.getLevel()).append(";");
			if(this.spellsPlace.get(key)!=null)
				sorts.append(this.spellsPlace.get(key));
			else
				sorts.append("_");
			sorts.append(",");
		}
		return sorts.substring(0, sorts.length()-1).toString();
	}
	
	private void parseSpells(String str) {
		String[] spells = str.split(",");
		for(String e : spells) {
			try	{
				int id = Integer.parseInt(e.split(";")[0]);
				int lvl = Integer.parseInt(e.split(";")[1]);
				char place = e.split(";")[2].charAt(0);
				learnSpell(id, lvl, false, false, false);
				this.spellsPlace.put(id, place);
			} catch(NumberFormatException e1) {
				continue;
			}
		}
	}
		
	public boolean learnSpell(int id, int level, boolean save, boolean send, boolean learn) {
		if(World.data.getSort(id).getStatsByLevel(level) == null) {
			Log.addToLog("> Erreur spell : "+id+" | level : "+level+" non trouver !");
			return false;
		}
		
		if(id == 366 && this.spells.containsKey(Integer.valueOf(id))) 
	    	return false;
	    
		if(this.spells.containsKey(Integer.valueOf(id)) && learn) {
			SocketManager.GAME_SEND_MESSAGE(this, "Tu poss�de d�j� ce sort.", Server.config.getMotdColor());
			return false;
		} else {
			this.spells.put(id, World.data.getSort(id).getStatsByLevel(level));
			
			if(send) {
				SocketManager.GAME_SEND_SPELL_LIST(this);
				SocketManager.GAME_SEND_Im_PACKET(this, "03;" + id);
			}
			
			if(save)
				save();
			
			return true;
		}
	}
	
	public boolean unlearnSpell(int id, int level, int ancLevel, boolean save, boolean send) {
		int spellPoint = 1;
		
		switch(ancLevel) {
		case 3:
			spellPoint = 2+1;
			break;
		case 4:
			spellPoint = 3+3;
			break;
		case 5:
			spellPoint = 4+6;
			break;
		case 6:
			spellPoint = 5+10;
			break;
		}
		
	    if(World.data.getSort(id).getStatsByLevel(level) == null) {
	    	Log.addToLog("> Erreur spell : "+id+" | level : "+level+" non trouver !");
			return false;
	    }

	    if(id == 366 && this.spells.containsKey(Integer.valueOf(id))) 
	    	return false;
	    
	    this.spells.put(Integer.valueOf(id), World.data.getSort(id).getStatsByLevel(level));
	   
	    if(send) {
	    	SocketManager.GAME_SEND_SPELL_LIST(this);
	    	SocketManager.GAME_SEND_Im_PACKET(this, "0154;" +"<b>"+ ancLevel +"</b>" +"~"+"<b>"+ spellPoint +"</b>");
	    	addSpellPoint(spellPoint);
	    	SocketManager.GAME_SEND_STATS_PACKET(this);
	    }
	    
	    if(save) 
	    	World.database.getCharacterData().update(this);
	    return true;
	}
	
	public boolean boostSpell(int id) {
		if(this.getSortStatBySortIfHas(id)== null) {
			Log.addToLog(this.getName()+" ne poss�de pas le sort d'id : " + id + " !");
			return false;
		}
		
		int oldLevel = this.getSortStatBySortIfHas(id).getLevel();
		
		if(oldLevel == 6)
			return false;
		
		if(this.spellPoints >= oldLevel && World.data.getSort(id).getStatsByLevel(oldLevel + 1).getReqLevel() <= this.getLevel())
		{
			if(learnSpell(id, oldLevel + 1, true, false, false)) {
				this.spellPoints -= oldLevel;
				save();
				return true;
			} else {
				Log.addToLog(this.getName()+" echec lors du boost spell d'id : " + id + " !");
				return false;
			}
		} else {//Pas le niveau ou pas les Points
			return false;
		}
	}
	
	public boolean forgetSpell(int id) {
		if(getSortStatBySortIfHas(id)== null) {
			if(Server.config.isDebug()) Log.addToLog(this.getName()+" ne poss�de pas le sort d'id " + id + " !");
			return false;
		}
		
		int oldLevel = getSortStatBySortIfHas(id).getLevel();
		
		if(oldLevel <= 1)
			return false;
		
		if(learnSpell(id, 1, true, false, false)) {
			this.spellPoints += Formulas.spellCost(oldLevel);	
			save();
			return true;
		} else {
			return false;
		}
	}
	
	public String parseSpellsList() {
		StringBuilder packet = new StringBuilder().append("SL");
		
		for(Iterator<SpellStats> i = this.spells.values().iterator(); i.hasNext();) {
		    SpellStats SS = i.next();
		    packet.append(SS.getSpellID()).append("~").append(SS.getLevel()).append("~").append(this.spellsPlace.get(SS.getSpellID())).append(";");
		}
		return packet.toString();
	}

	public void setSpellPlace(int id, char place) {
		replaceSpellInBook(place);
		this.spellsPlace.remove(id);	
		this.spellsPlace.put(id, place);
		save();
	}

	private void replaceSpellInBook(char Place) {
		for(int key : this.spells.keySet())
			if(this.spellsPlace.get(key)!=null)
				if(this.spellsPlace.get(key).equals(Place))
					this.spellsPlace.remove(key);
	}
	
	public SpellStats getSortStatBySortIfHas(int id) {
		return this.spells.get(id);
	}
	
	public String parseALK() {
		StringBuilder perso = new StringBuilder();
		perso.append("|");
		perso.append(this.getId()).append(";");
		perso.append(this.getName()).append(";");
		perso.append(this.level).append(";");
		perso.append(this.gfx).append(";");
		perso.append((this.color1!= -1?Integer.toHexString(this.color1):"-1")).append(";");
		perso.append((this.color2!= -1?Integer.toHexString(this.color2):"-1")).append(";");
		perso.append((this.color3!= -1?Integer.toHexString(this.color3):"-1")).append(";");
		perso.append(getGMStuffString()).append(";");
		perso.append((this.seeSeller?1:0)).append(";");
		perso.append("1;");
		perso.append(";");//DeathCount	this.deathCount;
		perso.append(";");//LevelMax
		return perso.toString();
	}
	
	public void remove() {
		World.database.getCharacterData().delete(this);
	}
	
	public void onJoinGame() {
		if(this.getAccount().getGameClient() == null)
			return; 
		
		if(World.events.call(new PlayerJoinEvent(this)))
			return;
		
		GameClient out = this.getAccount().getGameClient();
		this.getAccount().setCurPlayer(this);
		this.isOnline = true;
		
		if(this.mount != null)
			SocketManager.GAME_SEND_Re_PACKET(this, "+", this.mount);
		
		SocketManager.GAME_SEND_Rx_PACKET(this);
		SocketManager.GAME_SEND_ASK(out, this);
		
		//Envoie des bonus pano si besoin
		for(int a = 1; a < World.data.getItemSetNumber(); a++) {
			int num = getNumbEquipedItemOfPanoplie(a);
			if(num != 0)
				SocketManager.GAME_SEND_OS_PACKET(this, a);
		}
		
		//envoie des donn�es de m�tier
		if(this.jobs.size() > 0) {
			ArrayList<JobStat> list = new ArrayList<JobStat>();
			list.addAll(this.jobs.values());
			SocketManager.GAME_SEND_JS_PACKET(this, list);
			SocketManager.GAME_SEND_JX_PACKET(this, list);
			SocketManager.GAME_SEND_JO_PACKET(this, list);
			Object obj = getObjectByPos(Constants.ITEM_POS_ARME);
			
			if(obj != null)
				for(JobStat sm : list)
					if(sm.getTemplate().isValidTool(obj.getTemplate().getId()))
						SocketManager.GAME_SEND_OT_PACKET(this.getAccount().getGameClient(),sm.getTemplate().getId());
		}
		//Fin m�tier
		SocketManager.GAME_SEND_ALIGNEMENT(out, this.align);
		SocketManager.GAME_SEND_ADD_CANAL(out, this.canaux + "^" + (this.getAccount().getGmLvl() > 0 ? "@�" : ""));
		
		Date actDate = new Date();
		DateFormat dateFormat = new SimpleDateFormat("dd");
		String jour = dateFormat.format(actDate);
		dateFormat = new SimpleDateFormat("MM");
		String mois = dateFormat.format(actDate);	
		dateFormat = new SimpleDateFormat("yyyy");
		String annee = dateFormat.format(actDate);
		dateFormat = new SimpleDateFormat("HH");
		String heure = dateFormat.format(actDate);
		dateFormat = new SimpleDateFormat("mm");
		String min = dateFormat.format(actDate);
		this.getAccount().setLastConnection(annee+"~"+mois+"~"+jour+"~"+heure+"~"+min);
		
		if(this.getGuildMember() != null)
			SocketManager.GAME_SEND_gS_PACKET(this, this.getGuildMember());
		
		SocketManager.GAME_SEND_ZONE_ALLIGN_STATUT(out);
		SocketManager.GAME_SEND_SPELL_LIST(this);
		SocketManager.GAME_SEND_EMOTE_LIST(this, this.emotes, "0");
		SocketManager.GAME_SEND_RESTRICTIONS(out);
		SocketManager.GAME_SEND_Ow_PACKET(this);
		SocketManager.GAME_SEND_SEE_FRIEND_CONNEXION(out, this.isShowFriendConnection());
		SocketManager.GAME_SEND_Im_PACKET(this, "189");
		this.getAccount().sendOnline();
				
		if(!this.getAccount().getLastConnection().equals("") && !this.getAccount().getLastIp().equals(""))
			SocketManager.GAME_SEND_Im_PACKET(this, "0152;"+this.getAccount().getLastConnection()+"~"+this.getAccount().getLastIp());
		
		SocketManager.GAME_SEND_Im_PACKET(this, "0153;"+this.getAccount().getCurIp());
		this.getAccount().setLastIp(this.getAccount().getCurIp());

		World.database.getAccountData().update(this.getAccount());
		
		if(!Server.config.getMotd().equals(""))	{
			String color = Server.config.getMotdColor();
			if(color.equals(""))
				color = "000000";//Noir
			SocketManager.GAME_SEND_MESSAGE(this, Server.config.getMotd(), color);
		}

		SocketManager.GAME_SEND_ILS_PACKET(this, 2000);
	}
		
	public void sendGameCreate() {
		if(this.getAccount().getGameClient() == null) 
			return;
		
		GameClient client = this.getAccount().getGameClient();
		
		if(this.seeSeller == true && World.data.getSeller(this.getMap()) != null && World.data.getSeller(this.getMap()).contains(this.getId())) {
			World.data.removeSeller(this.getId(), this.getMap().getId());
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this.getMap(), this.getId());
			this.seeSeller = false;
		}
		
		SocketManager.GAME_SEND_GAME_CREATE(client, this.getName());
		SocketManager.GAME_SEND_STATS_PACKET(this);
		SocketManager.GAME_SEND_MAP_FIGHT_COUNT(client, this.getMap());
		this.getMap().addPlayer(this);
	}
	
	public String parseToOa() {
		return "Oa" + this.getId() + "|" + this.getGMStuffString();
	}

	
    public String parseToMerchant() {
    	StringBuilder str = new StringBuilder();
    	str.append(this.getCell().getId()).append(";");
    	str.append(this.getOrientation()).append(";");
    	str.append("0").append(";");
    	str.append(this.getId()).append(";");
    	str.append(this.getName()).append(";");
    	str.append("-5").append(";");//Merchant identifier
    	str.append(this.getGfx()).append("^").append(this.getSize()).append(";");
		str.append((this.getColor1()==-1?"-1":Integer.toHexString(this.getColor1()))).append(";");
		str.append((this.getColor2()==-1?"-1":Integer.toHexString(this.getColor2()))).append(";");
		str.append((this.getColor3()==-1?"-1":Integer.toHexString(this.getColor3()))).append(";");
    	str.append(getGMStuffString()).append(";");//acessories
    	str.append((this.getGuildMember() != null ? this.getGuildMember().getGuild().getName() : "")).append(";");//guildName
    	str.append((this.getGuildMember() != null ? this.getGuildMember().getGuild().getEmblem() : "")).append(";");//emblem
    	str.append("0;");//offlineType
        return str.toString();
    }
	
    public	String getGMStuffString() {
		StringBuilder str = new StringBuilder();
		if(getObjectByPos(Constants.ITEM_POS_ARME) != null)
		 	str.append(Integer.toHexString(getObjectByPos(Constants.ITEM_POS_ARME).getTemplate().getId()));	
		str.append(",");
		if(getObjectByPos(Constants.ITEM_POS_COIFFE) != null)
			str.append(Integer.toHexString(getObjectByPos(Constants.ITEM_POS_COIFFE).getTemplate().getId()));	
		str.append(",");
		if(getObjectByPos(Constants.ITEM_POS_CAPE) != null)
			str.append(Integer.toHexString(getObjectByPos(Constants.ITEM_POS_CAPE).getTemplate().getId()));	
		str.append(",");
		if(getObjectByPos(Constants.ITEM_POS_FAMILIER) != null)
			str.append(Integer.toHexString(getObjectByPos(Constants.ITEM_POS_FAMILIER).getTemplate().getId()));	
		str.append(",");
		if(getObjectByPos(Constants.ITEM_POS_BOUCLIER) != null)
			str.append(Integer.toHexString(getObjectByPos(Constants.ITEM_POS_BOUCLIER).getTemplate().getId()));	
		return str.toString();
	}

	public String getAsPacket()
	{
		refreshStats();
        refreshLife();

		StringBuilder ASData = new StringBuilder();
		ASData.append("As").append(this.getXpToString(",")).append("|");
		ASData.append(this.getKamas()).append("|").append(this.getCapital()).append("|").append(this.getSpellPoints()).append("|");
		ASData.append(this.getAlign()).append("~").append(this.getAlign()).append(",").append(this.getaLvl()).append(",").append(this.getGrade()).append(",").append(this.getHonor()).append(",").append(this.getDeshonor()+",").append((this.isShowWings() ? "1" : "0")).append("|");


        int pdv = getPdv(),pdvMax = getMaxPdv();
        if(this.getFight() != null) {
            Fighter f = this.getFight().getFighterByPerso(this);
            if(f != null) {
                pdv = f.getPDV();
                pdvMax = f.getPDVMAX();
            }
        }
		ASData.append(pdv).append(",").append(pdvMax).append("|");
		ASData.append(this.getEnergy()).append(",10000|");
		
		ASData.append(getInitiative()).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_PROS)+getStuffStats().getEffect(Constants.STATS_ADD_PROS)+((int)Math.ceil(this.getStats().getEffect(Constants.STATS_ADD_CHAN)/10))+getBuffsStats().getEffect(Constants.STATS_ADD_PROS)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_PA)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_PA)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_PA)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_PA)).append(",").append(getTotalStats().getEffect(Constants.STATS_ADD_PA)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_PM)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_PM)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_PM)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_PM)).append(",").append(getTotalStats().getEffect(Constants.STATS_ADD_PM)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_FORC)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_FORC)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_FORC)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_FORC)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_VITA)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_VITA)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_VITA)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_VITA)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_SAGE)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_SAGE)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_SAGE)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_SAGE)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_CHAN)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_CHAN)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_CHAN)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_CHAN)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_AGIL)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_AGIL)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_AGIL)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_AGIL)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_INTE)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_INTE)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_INTE)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_INTE)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_PO)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_PO)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_PO)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_PO)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_CREATURE)).append(",").append(getStuffStats().getEffect(Constants.STATS_CREATURE)).append(",").append(getDonsStats().getEffect(Constants.STATS_CREATURE)).append(",").append(getBuffsStats().getEffect(Constants.STATS_CREATURE)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_DOMA)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_DOMA)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_DOMA)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_DOMA)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_PDOM)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_PDOM)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_PDOM)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_PDOM)).append("|");
		ASData.append("0,0,0,0|");//Maitrise ?
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_PERDOM)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_PERDOM)).append(","+getDonsStats().getEffect(Constants.STATS_ADD_PERDOM)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_PERDOM)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_SOIN)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_SOIN)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_SOIN)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_SOIN)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_TRAPDOM)).append(",").append(getStuffStats().getEffect(Constants.STATS_TRAPDOM)).append(",").append(getDonsStats().getEffect(Constants.STATS_TRAPDOM)).append(",").append(getBuffsStats().getEffect(Constants.STATS_TRAPDOM)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_TRAPPER)).append(",").append(getStuffStats().getEffect(Constants.STATS_TRAPPER)).append(",").append(getDonsStats().getEffect(Constants.STATS_TRAPPER)).append(",").append(getBuffsStats().getEffect(Constants.STATS_TRAPPER)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_RETDOM)).append(",").append(getStuffStats().getEffect(Constants.STATS_RETDOM)).append(",").append(getDonsStats().getEffect(Constants.STATS_RETDOM)).append(",").append(getBuffsStats().getEffect(Constants.STATS_RETDOM)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_CC)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_CC)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_CC)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_CC)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_EC)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_EC)).append(",").append(getDonsStats().getEffect(Constants.STATS_ADD_EC)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_EC)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_AFLEE)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_AFLEE)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_AFLEE)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_AFLEE)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_MFLEE)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_MFLEE)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_MFLEE)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_MFLEE)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_R_NEU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_R_NEU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_NEU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_NEU)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_RP_NEU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_RP_NEU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_NEU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_NEU)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_R_PVP_NEU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_R_PVP_NEU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_PVP_NEU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_PVP_NEU)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_RP_PVP_NEU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_RP_PVP_NEU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_PVP_NEU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_PVP_NEU)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_R_TER)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_R_TER)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_TER)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_TER)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_RP_TER)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_RP_TER)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_TER)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_TER)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_R_PVP_TER)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_R_PVP_TER)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_PVP_TER)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_PVP_TER)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_RP_PVP_TER)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_RP_PVP_TER)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_PVP_TER)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_PVP_TER)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_R_EAU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_R_EAU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_EAU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_EAU)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_RP_EAU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_RP_EAU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_EAU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_EAU)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_R_PVP_EAU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_R_PVP_EAU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_PVP_EAU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_PVP_EAU)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_RP_PVP_EAU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_RP_PVP_EAU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_PVP_EAU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_PVP_EAU)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_R_AIR)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_R_AIR)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_AIR)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_AIR)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_RP_AIR)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_RP_AIR)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_AIR)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_AIR)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_R_PVP_AIR)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_R_PVP_AIR)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_PVP_AIR)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_PVP_AIR)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_RP_PVP_AIR)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_RP_PVP_AIR)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_PVP_AIR)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_PVP_AIR)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_R_FEU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_R_FEU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_FEU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_FEU)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_RP_FEU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_RP_FEU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_FEU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_FEU)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_R_PVP_FEU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_R_PVP_FEU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_PVP_FEU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_R_PVP_FEU)).append("|");
		ASData.append(this.getStats().getEffect(Constants.STATS_ADD_RP_PVP_FEU)).append(",").append(getStuffStats().getEffect(Constants.STATS_ADD_RP_PVP_FEU)).append(",").append(0).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_PVP_FEU)).append(",").append(getBuffsStats().getEffect(Constants.STATS_ADD_RP_PVP_FEU)).append("|");
		
		return ASData.toString();
	}
	
	public int getGrade() {
		if(this.getAlign() == Constants.ALIGNEMENT_NEUTRE)
			return 0;
		if(this.getHonor() >= 17500)
			return 10;
		
		for(int n = 1; n <= 10; n++)
			if(this.getHonor() < World.data.getExpLevel(n).pvp)
				return n-1;
		return 0;
	}
	
	public String getXpToString(String c) {
		return this.getExperience() + c + World.data.getPersoXpMin(this.getLevel()) + c + World.data.getPersoXpMax(this.getLevel());
	}
	
	private Stats getStuffStats() {
		Stats stats = new Stats(false,null);
		ArrayList<Integer> itemSetApplied = new ArrayList<Integer>();
		
		for(Entry<Integer, Object> entry : this.objects.entrySet()) {
			if(entry.getValue().getPosition() != Constants.ITEM_POS_NO_EQUIPED) {
				stats = Stats.cumulStat(stats,entry.getValue().getStats());
				int panID = entry.getValue().getTemplate().getSet();
				//Si panoplie, et si l'effet de pano n'a pas encore �t� ajout�
				if(panID > 0 && !itemSetApplied.contains(panID)) {
					itemSetApplied.add(panID);
					ObjectSet IS = World.data.getItemSet(panID);
					//Si la pano existe
					if(IS != null)
						stats = Stats.cumulStat(stats, IS.getBonusStatByItemNumb(this.getNumbEquipedItemOfPanoplie(panID)));
					
				}
			}
		}
		
		if(this.isOnMount() && this.getMount() != null)
			stats = Stats.cumulStat(stats, this.getMount().getStats());
		
		return stats;
	}

	private Stats getBuffsStats() {
		Stats stats = new Stats(false, null);
		for(Map.Entry<Integer, SpellEffect> entry : this.buffs.entrySet())
			stats.addOneStat(entry.getValue().getEffectID(), entry.getValue().getValue());
		return stats;
	}
	
	private Stats getDonsStats() {
		/* TODO*/
		Stats stats = new Stats(false,null);
		return stats;
	}
	
	public Stats getTotalStats() {
		Stats total = new Stats(false,null);
		total = Stats.cumulStat(total, this.getStats());
		total = Stats.cumulStat(total, this.getStuffStats());
		total = Stats.cumulStat(total, this.getDonsStats());
		
		if(this.getFight() == null)
			total = Stats.cumulStat(total, this.getBuffsStats());
		
		return total;
	}

	public int getInitiative() {
		int fact = 4;
		int pvmax = this.getMaxPdv() - Constants.getBasePdv(this.getClasse().getId());
		int pv = this.pdv - Constants.getBasePdv(this.getClasse().getId());
		
		if(this.getClasse() == Classe.SACRIEUR)
			fact = 8;
		
		double coef = pvmax/fact;
		
		coef += getStuffStats().getEffect(Constants.STATS_ADD_INIT);
		coef += getTotalStats().getEffect(Constants.STATS_ADD_AGIL);
		coef += getTotalStats().getEffect(Constants.STATS_ADD_CHAN);
		coef += getTotalStats().getEffect(Constants.STATS_ADD_INTE);
		coef += getTotalStats().getEffect(Constants.STATS_ADD_FORC);
		
		int init = 1;
		if(pvmax != 0)
		 init = (int) (coef * ((double) pv / (double) pvmax));
		if(init < 0)
			init = 0;
		
		return init;
	}

	public int getPodsInStore() {
        if(this.stores.isEmpty())
        	return 0;
        
        int total = 0;
		for(Entry<Integer,Integer> obj : this.stores.entrySet()) {
        	Object object = World.data.getObject(obj.getKey());
        	if(object != null)
        		total += object.getTemplate().getPod() * object.getQuantity();
        }
		
		return total;
    }
	
	public int getPodUsed() {
		int pod = 0;
		for(Entry<Integer,Object> entry : this.objects.entrySet())
			pod += entry.getValue().getTemplate().getPod() * entry.getValue().getQuantity();
		pod += this.getPodsInStore();
		return pod;
	}

	public int getMaxPod() {
		int pods = this.getTotalStats().getEffect(Constants.STATS_ADD_PODS) + 
				   this.getTotalStats().getEffect(Constants.STATS_ADD_FORC) * 5;
		for(JobStat SM : this.jobs.values()) {
			pods += SM.get_lvl() * 5;
			if(SM.get_lvl() == 100) 
				pods += 1000;
		}
		return pods;
	}


	
	public int getPdvPer() {
        return (100* this.getPdv())/ this.getMaxPdv();
	}

    /**
     * Affiche l emote du joueur a l ensemble des joueurs pouvant le voir
     * @param emoteId
     */
	public void emoticone(int emoteId) {
        String activeEmote = "cS" + this.getId() + "|" + emoteId;
        if(this.getFight() == null){
            this.getMap().send(activeEmote);
        } else {
            this.getFight().send(activeEmote);
        }
	}

	public void refreshMapAfterFight() {
		this.getMap().addPlayer(this);
        SocketManager.GAME_SEND_STATS_PACKET(this);
        SocketManager.GAME_SEND_ILS_PACKET(this, 2000);
        this.regenRate=2000;
		this.setFight(null);
		this.isAway = false;
	}

	public void boostStat(int stat, boolean capital) {
		int value = 0;
		switch(stat)
		{
			case 10://Force
				value = this.getStats().getEffect(Constants.STATS_ADD_FORC);
			break;
			case 13://Chance
				value = this.getStats().getEffect(Constants.STATS_ADD_CHAN);
			break;
			case 14://Agilit�
				value = this.getStats().getEffect(Constants.STATS_ADD_AGIL);
			break;
			case 15://Intelligence
				value = this.getStats().getEffect(Constants.STATS_ADD_INTE);
			break;
		}
		int cout = Constants.getReqPtsToBoostStatsByClass(this.getClasse().getId(), stat, value);
		if(!capital)
			cout = 0;
		if(cout <= this.capital)
		{
			switch(stat)
			{
				case 11://Vita
					if(this.getClasse().getId() != Constants.CLASS_SACRIEUR)
						this.getStats().addOneStat(Constants.STATS_ADD_VITA, 1);
					else
						this.getStats().addOneStat(Constants.STATS_ADD_VITA, 2);
				break;
				case 12://Sage
					this.getStats().addOneStat(Constants.STATS_ADD_SAGE, 1);
				break;
				case 10://Force
					this.getStats().addOneStat(Constants.STATS_ADD_FORC, 1);
				break;
				case 13://Chance
					this.getStats().addOneStat(Constants.STATS_ADD_CHAN, 1);
				break;
				case 14://Agilit�
					this.getStats().addOneStat(Constants.STATS_ADD_AGIL, 1);
				break;
				case 15://Intelligence
					this.getStats().addOneStat(Constants.STATS_ADD_INTE, 1);
				break;
				default:
					return;
			}
			this.capital -= cout;
			SocketManager.GAME_SEND_STATS_PACKET(this);
			World.database.getCharacterData().update(this);
		}
	}
	
	public boolean isMuted() {
		return this.getAccount().isMuted();
	}

	public String parseObjectsToDb()
	{
		StringBuilder str = new StringBuilder();
		if(this.objects.isEmpty())
			return "";
		
		for(Entry<Integer,Object> entry : this.objects.entrySet()) 
			str.append(entry.getValue().getId()).append("|");

		return str.toString();
	}
	
	public boolean addObject(Object newObj, boolean stackIfSimilar) {
		for(Entry<Integer,Object> entry : this.objects.entrySet()) {
			Object obj = entry.getValue();
			if(obj.getTemplate().getId() == newObj.getTemplate().getId() && obj.getStats().isSameStats(newObj.getStats())
					&& stackIfSimilar && newObj.getTemplate().getType() != 85 && obj.getPosition() == Constants.ITEM_POS_NO_EQUIPED) {
				obj.setQuantity(obj.getQuantity()+newObj.getQuantity());//On ajoute QUA item a la quantit� de l'objet existant
				World.database.getItemData().update(obj);
				if(this.isOnline)
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this,obj);
				return false;
			}
		}
		this.objects.put(newObj.getId(), newObj);
		SocketManager.GAME_SEND_OAKO_PACKET(this,newObj);
		return true;
	}
	
	public void addObject(Object newObj)
	{
		this.objects.put(newObj.getId(), newObj);
	}
	
	public Map<Integer,Object> getItems()
	{
		return this.objects;
	}
	
	public String parseItemToASK()
	{
		StringBuilder str = new StringBuilder();
		if(this.objects.isEmpty())return "";
		for(Object  obj : this.objects.values())
		{ 
			str.append(obj.parseItem());
		}
		return str.toString();
	}

	public String getBankItemsIDSplitByChar(String splitter)
	{
		StringBuilder str = new StringBuilder();
		if(this.getAccount().getBank().isEmpty())return "";
		for(int entry : this.getAccount().getBank().keySet())
		{
			str.append(entry).append(splitter);
		}
		return str.toString();
	}
	
	public String getItemsIDSplitByChar(String splitter)
	{
		StringBuilder str = new StringBuilder();
		if(this.objects.isEmpty())return "";
		for(int entry : this.objects.keySet())
		{
			if(str.length() != 0) str.append(splitter);
			str.append(entry);
		}
		return str.toString();
	}
	
	public String getStoreItemsIDSplitByChar(String splitter)
	{
		StringBuilder str = new StringBuilder();
		if(this.stores.isEmpty())return "";
		for(int entry : this.stores.keySet())
		{
			if(str.length() != 0) str.append(splitter);
			str.append(entry);
		}
		return str.toString();
	}

	public boolean hasItemGuid(int guid)
	{
		return this.objects.get(guid) != null?this.objects.get(guid).getQuantity()>0:false;
	}
	
	public int storeAllBuy() {
		int total = 0;
		for(java.util.Map.Entry<Integer, Integer> value : this.stores.entrySet()) {
			Object O = World.data.getObject(value.getKey());
			int multiple = O.getQuantity();
			int add = value.getValue() * multiple;
			total += add;
		}
		return total;
	}
	
	public void sellItem(int guid,int qua)
	{
		if(qua <= 0)
			return;
		if(this.objects.get(guid).getQuantity() < qua)//Si il a moins d'item que ce qu'on veut Del
			qua = this.objects.get(guid).getQuantity();
		
		int prix = qua * (this.objects.get(guid).getTemplate().getPrice()/10);//Calcul du prix de vente (prix d'achat/10)
		int newQua =  this.objects.get(guid).getQuantity() - qua;
		if(newQua <= 0)//Ne devrait pas etre <0, S'il n'y a plus d'item apres la vente 
		{
			Object o = this.objects.get(guid);
			this.objects.remove(guid);
			World.data.removeObject(guid);
			World.database.getItemData().delete(o);
			SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this,guid);
		}else//S'il reste des items apres la vente
		{
			this.objects.get(guid).setQuantity(newQua);
			SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this, this.objects.get(guid));
		}

		this.kamas += prix;
		SocketManager.GAME_SEND_STATS_PACKET(this);
		SocketManager.GAME_SEND_Ow_PACKET(this);
		SocketManager.GAME_SEND_ESK_PACKEt(this);
	}

	public void removeItem(int guid)
	{
		this.objects.remove(guid);
	}
	public void removeItem(int guid, int nombre,boolean send,boolean deleteFromWorld)
	{
		Object obj = this.objects.get(guid);
		
		if(nombre > obj.getQuantity())
			nombre = obj.getQuantity();
		
		if(obj.getQuantity() >= nombre)
		{
			int newQua = obj.getQuantity() - nombre;
			if(newQua >0)
			{
				obj.setQuantity(newQua);
				if(send && this.isOnline)
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this, obj);
			}else
			{
				//on supprime de l'inventaire et du Monde
				this.objects.remove(obj.getId());
				if(deleteFromWorld)
					World.data.removeObject(obj.getId());
				//on envoie le packet si connect�
				if(send && this.isOnline)
					SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this, obj.getId());
			}
		}
	}
	public void deleteItem(int guid)
	{
		this.objects.remove(guid);
		World.data.removeObject(guid);
	}
	public Object getObjectByPos(int pos)
	{
		if(pos == Constants.ITEM_POS_NO_EQUIPED)return null;
		
		for(Entry<Integer,Object> entry : this.objects.entrySet())
		{
			Object obj = entry.getValue();
			if(obj.getPosition() == pos)
				return obj;
		}
		return null;
	}

	public void refreshStats()
	{
		int before = this.maxPdv;
		double actPdvPer = (100*(double)this.pdv)/this.maxPdv;
		this.maxPdv = (this.getLevel() - 1) * 5 + Constants.getBasePdv(this.getClasse().getId()) + getTotalStats().getEffect(Constants.STATS_ADD_VITA);
		if(before == this.maxPdv)
			this.pdv = (int) Math.round(this.maxPdv*actPdvPer/100);
	}

	public void levelUp(boolean send,boolean addXp)
	{
		if(this.getLevel() == World.data.getExpLevelSize())
			return;
		
		this.level++;
		this.capital+=5;
		this.spellPoints++;
		this.maxPdv += 5;
		this.pdv = this.maxPdv;
		
		if(this.getLevel() == 100)
			this.getStats().addOneStat(Constants.STATS_ADD_PA, 1);
		Constants.onLevelUpSpells(this,this.getLevel());
		
		if(addXp)
			this.experience = World.data.getExpLevel(this.getLevel()).perso;
		
		if(this.getGuildMember() != null)
			World.database.getGuildMemberData().update(getGuildMember());
		
		if(send && this.isOnline) {
			SocketManager.GAME_SEND_NEW_LVL_PACKET(this.getAccount().getGameClient(),this.getLevel());
			SocketManager.GAME_SEND_STATS_PACKET(this);
			SocketManager.GAME_SEND_SPELL_LIST(this);
		}
	}
	
	public void addXp(long winxp) {
		this.experience += winxp;
		final int exLevel = this.getLevel();
		while(this.experience >= World.data.getPersoXpMax(this.getLevel()) && this.getLevel() < World.data.getExpLevelSize())
			levelUp(false,false);
		if(this.isOnline) {
			if(exLevel < this.getLevel()) 
				SocketManager.GAME_SEND_NEW_LVL_PACKET(this.getAccount().getGameClient(),this.getLevel());
			
			SocketManager.GAME_SEND_STATS_PACKET(Player.this);
			SocketManager.GAME_SEND_SPELL_LIST(Player.this);
		}
	}
	
	public void addKamas (long l)
	{
		this.kamas += l;
	}

	public Object getSimilarItem(Object exObj)
	{
		for(Entry<Integer,Object> entry : this.objects.entrySet())
		{
			Object obj = entry.getValue();
			
			if(obj.getTemplate().getId() == exObj.getTemplate().getId()
				&& obj.getStats().isSameStats(exObj.getStats())
				&& obj.getId() != exObj.getId()
				&& obj.getPosition() == Constants.ITEM_POS_NO_EQUIPED)
			return obj;
		}
		return null;
	}

	public int learnJob(Job m)
	{
		for(Entry<Integer, JobStat> entry : this.jobs.entrySet())
			if(entry.getValue().getTemplate().getId() == m.getId())//Si le joueur a d�j� le m�tier
				return -1;
		int Msize = this.jobs.size();
		if(Msize == 6)//Si le joueur a d�j� 6 m�tiers
			return -1;
		int pos = 0;
		if(JobConstant.isMageJob(m.getId()))
		{
			if(this.jobs.get(5) == null) pos = 5;
			if(this.jobs.get(4) == null) pos = 4;
			if(this.jobs.get(3) == null) pos = 3;
		}else
		{
			if(this.jobs.get(2) == null) pos = 2;
			if(this.jobs.get(1) == null) pos = 1;
			if(this.jobs.get(0) == null) pos = 0;
		}
		
		JobStat sm = new JobStat(pos,m,1,0);
		this.jobs.put(pos, sm);//On apprend le m�tier lvl 1 avec 0 xp
		if(this.isOnline)
		{
			//on cr�er la listes des statsMetier a envoyer (Seulement celle ci)
			ArrayList<JobStat> list = new ArrayList<JobStat>();
			list.add(sm);
			
			SocketManager.GAME_SEND_Im_PACKET(this, "02;"+m.getId());
			//packet JS
			SocketManager.GAME_SEND_JS_PACKET(this, list);
			//packet JX
			SocketManager.GAME_SEND_JX_PACKET(this, list);
			//Packet JO (Job Option)
			SocketManager.GAME_SEND_JO_PACKET(this,list);
			
			Object obj = getObjectByPos(Constants.ITEM_POS_ARME);
			if(obj != null)
				if(sm.getTemplate().isValidTool(obj.getTemplate().getId()))
					SocketManager.GAME_SEND_OT_PACKET(this.getAccount().getGameClient(),m.getId());
		}
		return pos;
	}
	
	public void unlearnJob(int m)
	{
		this.jobs.remove(m);
	}

	public boolean hasEquiped(int id)
	{
		for(Entry<Integer,Object> entry : this.objects.entrySet())
			if(entry.getValue().getTemplate().getId() == id && entry.getValue().getPosition() != Constants.ITEM_POS_NO_EQUIPED)
				return true;
		return false;
	}
	
	public String parseToPM()
	{
		StringBuilder str = new StringBuilder();
		str.append(this.getId()).append(";");
		str.append(this.getName()).append(";");
		str.append(this.getGfx()).append(";");
		str.append(this.getColor1()).append(";");
		str.append(this.getColor2()).append(";");
		str.append(this.getColor3()).append(";");
		str.append(this.getGMStuffString()).append(";");
		str.append(this.pdv).append(",").append(this.maxPdv).append(";");
		str.append(this.getLevel()).append(";");
		str.append(this.getInitiative()).append(";");
		str.append(this.getTotalStats().getEffect(Constants.STATS_ADD_PROS)).append(";");
		str.append("0");//Side = ?
		return str.toString();
	}
	
	public int getNumbEquipedItemOfPanoplie(int panID)
	{
		int nb = 0;
		for(Entry<Integer, Object> i : this.objects.entrySet()) {
			if(i.getValue().getPosition() == Constants.ITEM_POS_NO_EQUIPED)
				continue;
			if(i.getValue().getTemplate().getSet() == panID)
				nb++;
		}
		return nb;
	}

	public void startActionOnCell(GameAction GA)
	{ 
		int cellID = -1;
		int action = -1;
		try	{
			cellID = Integer.parseInt(GA.getArgs().split(";")[0]);
			action = Integer.parseInt(GA.getArgs().split(";")[1]);
		} catch(Exception e) {}
		if(cellID == -1 || action == -1)return;
		//Si case invalide
		if(!this.getMap().getCases().get(cellID).canDoAction(action))return;
		this.getMap().getCases().get(cellID).startAction(this,GA);
	}

	public void finishActionOnCell(GameAction GA)
	{
		int cellID = -1;
		try
		{
			cellID = Integer.parseInt(GA.getArgs().split(";")[0]);
		}catch(Exception e){};
		if(cellID == -1)return;
		this.getMap().getCases().get(cellID).finishAction(this,GA);
		this.getAccount().getGameClient().removeAction(GA);
	}
	
	/**public void setPosition(int newMapID, int newCellID){
		if(!this.followers.isEmpty())//On met a jour la carte des personnages qui nous suivent
			for(Player player : this.followers.values())
				if(player.isOnline())
					SocketManager.GAME_SEND_FLAG_PACKET(player, this);
				else
					this.followers.remove(player.getId());
	}*/
	
	public int getBankCost()
	{
		return this.getAccount().getBank().size();
	}
	
	public String getStringVar(String str)
	{
		if(str.equals("name"))
			return this.getName();
		if(str.equals("bankCost"))
			return getBankCost()+"";
		return "";
	}

	public void setBankKamas(long i)
	{
		this.getAccount().setBankKamas(i);
		World.database.getAccountData().update(this.getAccount());
	}
	
	public long getBankKamas()
	{
		return this.getAccount().getBankKamas();
	}


	public String parseBankPacket()
	{
		StringBuilder packet = new StringBuilder();
		for(Entry<Integer, Object> entry : this.getAccount().getBank().entrySet())
			packet.append("O").append(entry.getValue().parseItem()).append(";");
		if(getBankKamas() != 0)
			packet.append("G").append(getBankKamas());
		return packet.toString();
	}

	public void addCapital(int pts)
	{
		this.capital += pts;
	}

	public void addSpellPoint(int pts)
	{
		this.spellPoints += pts;
	}

	public void addInBank(int guid, int qua)
	{
		Object PersoObj = World.data.getObject(guid);
		//Si le joueur n'a pas l'item dans son sac ...
		if(this.objects.get(guid) == null)
		{
			Log.addToLog("Le joueur "+ this.getName() +" a tenter d'ajouter un objet en banque qu'il n'avait pas.");
			return;
		}
		//Si c'est un item �quip� ...
		if(PersoObj.getPosition() != Constants.ITEM_POS_NO_EQUIPED)return;
		
		Object BankObj = getSimilarBankItem(PersoObj);
		int newQua = PersoObj.getQuantity() - qua;
		if(BankObj == null)//S'il n'y pas d'item du meme Template
		{
			//S'il ne reste pas d'item dans le sac
			if(newQua <= 0)
			{
				//On enleve l'objet du sac du joueur
				removeItem(PersoObj.getId());
				//On met l'objet du sac dans la banque, avec la meme quantit�
				this.getAccount().getBank().put(PersoObj.getId(), PersoObj);
				String str = "O+"+PersoObj.getId()+"|"+PersoObj.getQuantity()+"|"+PersoObj.getTemplate().getId()+"|"+PersoObj.parseStatsString();
				SocketManager.GAME_SEND_EsK_PACKET(this, str);
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this, guid);
				
			}
			else//S'il reste des objets au joueur
			{
				//on modifie la quantit� d'item du sac
				PersoObj.setQuantity(newQua);
				//On ajoute l'objet a la banque et au monde
				BankObj = Object.getClone(PersoObj, qua);
				World.data.addObject(BankObj, true);
				this.getAccount().getBank().put(BankObj.getId(), BankObj);
				
				//Envoie des packets
				String str = "O+"+BankObj.getId()+"|"+BankObj.getQuantity()+"|"+BankObj.getTemplate().getId()+"|"+BankObj.parseStatsString();
				SocketManager.GAME_SEND_EsK_PACKET(this, str);
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this, PersoObj);
				
			}
		}else // S'il y avait un item du meme template
		{
			//S'il ne reste pas d'item dans le sac
			if(newQua <= 0)
			{
				//On enleve l'objet du sac du joueur
				removeItem(PersoObj.getId());
				//On enleve l'objet du monde
				World.data.removeObject(PersoObj.getId());
				//On ajoute la quantit� a l'objet en banque
				BankObj.setQuantity(BankObj.getQuantity() + PersoObj.getQuantity());
				//on envoie l'ajout a la banque de l'objet
				String str = "O+"+BankObj.getId()+"|"+BankObj.getQuantity()+"|"+BankObj.getTemplate().getId()+"|"+BankObj.parseStatsString();
				SocketManager.GAME_SEND_EsK_PACKET(this, str);
				//on envoie la supression de l'objet du sac au joueur
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this, guid);
				
			}else //S'il restait des objets
			{
				//on modifie la quantit� d'item du sac
				PersoObj.setQuantity(newQua);
				BankObj.setQuantity(BankObj.getQuantity() + qua);
				String str = "O+"+BankObj.getId()+"|"+BankObj.getQuantity()+"|"+BankObj.getTemplate().getId()+"|"+BankObj.parseStatsString();
				SocketManager.GAME_SEND_EsK_PACKET(this, str);
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this, PersoObj);
				
			}
		}
		SocketManager.GAME_SEND_Ow_PACKET(this);
		World.database.getAccountData().update(this.getAccount());
	}

	private Object getSimilarBankItem(Object obj)
	{
		for(Object value : this.getAccount().getBank().values())
		{
			if(value.getTemplate().getType() == 85)
				continue;
			if(value.getTemplate().getId() == obj.getTemplate().getId() && value.getStats().isSameStats(obj.getStats()))
				return value;
		}
		return null;
	}

	public void removeFromBank(int guid, int qua)
	{
		Object BankObj = World.data.getObject(guid);
		//Si le joueur n'a pas l'item dans sa banque ...
		if(this.getAccount().getBank().get(guid) == null)
		{
			Log.addToLog("Le joueur "+ this.getName() +" a tenter de retirer un objet en banque qu'il n'avait pas.");
			return;
		}
		
		Object PersoObj = getSimilarItem(BankObj);
		
		int newQua = BankObj.getQuantity() - qua;
		
		if(PersoObj == null)//Si le joueur n'avait aucun item similaire
		{
			//S'il ne reste rien en banque
			if(newQua <= 0)
			{
				//On retire l'item de la banque
				this.getAccount().getBank().remove(guid);
				//On l'ajoute au joueur
				this.objects.put(guid, BankObj);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OAKO_PACKET(this,BankObj);
				String str = "O-"+guid;
				SocketManager.GAME_SEND_EsK_PACKET(this, str);
				
			}else //S'il reste des objets en banque
			{
				//On cr�e une copy de l'item en banque
				PersoObj = Object.getClone(BankObj, qua);
				//On l'ajoute au monde
				World.data.addObject(PersoObj, true);
				//On retire X objet de la banque
				BankObj.setQuantity(newQua);
				//On l'ajoute au joueur
				this.objects.put(PersoObj.getId(), PersoObj);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OAKO_PACKET(this,PersoObj);
				String str = "O+"+BankObj.getId()+"|"+BankObj.getQuantity()+"|"+BankObj.getTemplate().getId()+"|"+BankObj.parseStatsString();
				SocketManager.GAME_SEND_EsK_PACKET(this, str);
				
			}
		}
		else
		{
			//S'il ne reste rien en banque
			if(newQua <= 0)
			{
				//On retire l'item de la banque
				this.getAccount().getBank().remove(BankObj.getId());
				World.data.removeObject(BankObj.getId());
				//On Modifie la quantit� de l'item du sac du joueur
				PersoObj.setQuantity(PersoObj.getQuantity() + BankObj.getQuantity());
				
				//On envoie les packets
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this, PersoObj);
				String str = "O-"+guid;
				SocketManager.GAME_SEND_EsK_PACKET(this, str);
				
			}
			else//S'il reste des objets en banque
			{
				//On retire X objet de la banque
				BankObj.setQuantity(newQua);
				//On ajoute X objets au joueurs
				PersoObj.setQuantity(PersoObj.getQuantity() + qua);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this,PersoObj);
				String str = "O+"+BankObj.getId()+"|"+BankObj.getQuantity()+"|"+BankObj.getTemplate().getId()+"|"+BankObj.parseStatsString();
				SocketManager.GAME_SEND_EsK_PACKET(this, str);
				
			}
		}
		SocketManager.GAME_SEND_Ow_PACKET(this);
		World.database.getAccountData().update(this.getAccount());
	}

	public void openMountPark() {
		if(getDeshonor() >= 5) {
			SocketManager.GAME_SEND_Im_PACKET(this, "183");
			return;
		}
		
		this.setCurMountPark(this.getMap().getMountPark());
		this.setAway(true);
		String str = this.getCurMountPark().parseData(this.getId(), (this.getCurMountPark().getOwner() == -1 ? true : false));
		
		if(this.getCurMountPark().getOwner() == -1 || this.getCurMountPark().getOwner() == this.getId()) {//Public ou le proprio
			SocketManager.GAME_SEND_ECK_PACKET(this, 16, str);
		} else 
		if(this.getGuildMember() != null) {
			if(World.data.getPlayer(this.getCurMountPark().getOwner()).getGuildMember() != null)
				if(World.data.getPlayer(this.getCurMountPark().getOwner()).getGuildMember().getGuild() == this.getGuildMember().getGuild() && getGuildMember().canDo(Constants.G_USEENCLOS))
					SocketManager.GAME_SEND_ECK_PACKET(this, 16, str);
		} else {
			SocketManager.GAME_SEND_Im_PACKET(this, "1101");
			this.setCurMountPark(null);
			this.setAway(false);
		}
	}
	
	public void warpToSavePos() {
		try {
			String[] infos = this.savePos.split(",");
			setPosition(Short.parseShort(infos[0]), Integer.parseInt(infos[1]));
		} catch(Exception e) {}
	}
	
	public void removeByTemplateID(int tID, int count)
	{
		//Copie de la liste pour eviter les modif concurrentes
		ArrayList<Object> list = new ArrayList<Object>();
		list.addAll(this.objects.values());
		
		ArrayList<Object> remove = new ArrayList<Object>();
		int tempCount = count;
		
		//on verifie pour chaque objet
		for(Object obj : list)
		{
			//Si mauvais TemplateID, on passe
			if(obj.getTemplate().getId() != tID)continue;
			
			if(obj.getQuantity() >= count)
			{
				int newQua = obj.getQuantity() - count;
				if(newQua >0)
				{
					obj.setQuantity(newQua);
					if(this.isOnline)
						SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this, obj);
				}else
				{
					//on supprime de l'inventaire et du Monde
					this.objects.remove(obj.getId());
					World.data.removeObject(obj.getId());
					//on envoie le packet si connect�
					if(this.isOnline)
						SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this, obj.getId());
				}
				return;
			}
			else//Si pas assez d'objet
			{
				if(obj.getQuantity() >= tempCount)
				{
					int newQua = obj.getQuantity() - tempCount;
					if(newQua > 0)
					{
						obj.setQuantity(newQua);
						if(this.isOnline)
							SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this, obj);
					}
					else remove.add(obj);
					
					for(Object o : remove)
					{
						//on supprime de l'inventaire et du Monde
						this.objects.remove(o.getId());
						World.data.removeObject(o.getId());
						//on envoie le packet si connect�
						if(this.isOnline)
							SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this, o.getId());
					}
				}else
				{
					// on r�duit le compteur
					tempCount -= obj.getQuantity();
					remove.add(obj);
				}
			}
		}
	}

	public Map<Integer, JobStat> getMetiers()
	{
		return this.jobs;
	}

	public void doJobAction(int actionID, InteractiveObject object, GameAction GA,Case cell)
	{
		JobStat SM = getMetierBySkill(actionID);
		if(SM == null)return;
		SM.startAction(actionID,this, object,GA,cell);
	}
	public void finishJobAction(int actionID, InteractiveObject object, GameAction GA,Case cell)
	{
		JobStat SM = getMetierBySkill(actionID);
		if(SM == null)return;
		SM.endAction(actionID,this, object,GA,cell);
	}

	public String parseJobData()
	{
		StringBuilder str = new StringBuilder();
		if(this.jobs.isEmpty())return "";
		for(JobStat SM : this.jobs.values())
		{
			if(str.length() >0)str.append(";");
			str.append(SM.getTemplate().getId()).append(",").append(SM.getXp());
		}
		return str.toString();
	}
	
	public int totalJobBasic()
	{
		int i=0;

		for(JobStat SM : this.jobs.values())
		{
			// Si c'est un m�tier 'basic' :
			if(SM.getTemplate().getId() == 	2 || SM.getTemplate().getId() == 11 ||
			   SM.getTemplate().getId() == 13 || SM.getTemplate().getId() == 14 ||
			   SM.getTemplate().getId() == 15 || SM.getTemplate().getId() == 16 ||
			   SM.getTemplate().getId() == 17 || SM.getTemplate().getId() == 18 ||
			   SM.getTemplate().getId() == 19 || SM.getTemplate().getId() == 20 ||
			   SM.getTemplate().getId() == 24 || SM.getTemplate().getId() == 25 ||
			   SM.getTemplate().getId() == 26 || SM.getTemplate().getId() == 27 ||
			   SM.getTemplate().getId() == 28 || SM.getTemplate().getId() == 31 ||
			   SM.getTemplate().getId() == 36 || SM.getTemplate().getId() == 41 ||
			   SM.getTemplate().getId() == 56 || SM.getTemplate().getId() == 58 ||
			   SM.getTemplate().getId() == 60 || SM.getTemplate().getId() == 65)
			{
			i++;
			}
		}
		return i;
	}
	
	public int totalJobFM()
	{
		int i=0;

		for(JobStat SM : this.jobs.values())
		{
			// Si c'est une sp�cialisation 'FM' :
			if(SM.getTemplate().getId() == 	43 || SM.getTemplate().getId() == 44 ||
			   SM.getTemplate().getId() == 45 || SM.getTemplate().getId() == 46 ||
			   SM.getTemplate().getId() == 47 || SM.getTemplate().getId() == 48 ||
			   SM.getTemplate().getId() == 49 || SM.getTemplate().getId() == 50 ||
			   SM.getTemplate().getId() == 62 || SM.getTemplate().getId() == 63 ||
			   SM.getTemplate().getId() == 64)
			{
			i++;
			}
		}
		return i;
	}

	public JobStat getMetierBySkill(int skID)
	{
		for(JobStat SM : this.jobs.values())
			if(SM.isValidMapAction(skID))return SM;
		return null;
	}

	public String parseToFriendList(int guid)
	{
		StringBuilder str = new StringBuilder();
		str.append(";");
		str.append("?;");//FIXME
		str.append(this.getName()).append(";");
		if(this.getAccount().isFriendWith(guid))
		{
			str.append(this.getLevel()).append(";");
			str.append(this.getAlign()).append(";");
		}else
		{
			str.append("?;");
			str.append("-1;");
		}
		str.append(this.getClasse().getId()).append(";");
		str.append(this.getSex()).append(";");
		str.append(this.getGfx());
		return str.toString();
	}
	
	public String parseToEnemyList(int guid)
	{
		StringBuilder str = new StringBuilder();
		str.append(";");
		str.append("?;");//FIXME
		str.append(this.getName()).append(";");
		if(this.getAccount().isFriendWith(guid))
		{
			str.append(this.getLevel()).append(";");
			str.append(this.getAlign()).append(";");
		}else
		{
			str.append("?;");
			str.append("-1;");
		}
		str.append(this.getClasse()).append(";");
		str.append(this.getSex()).append(";");
		str.append(this.getGfx());
		return str.toString();
	}

	public JobStat getMetierByID(int job)
	{
		for(JobStat SM : this.jobs.values())
			if(SM.getTemplate().getId() == job)
				return SM;
		return null;
	}

	public void toogleOnMount()
	{
		this.setOnMount(!this.isOnMount());
		Object obj = getObjectByPos(Constants.ITEM_POS_FAMILIER);
		
		if(this.isOnMount() && obj != null)	{
			obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
			SocketManager.GAME_SEND_OBJET_MOVE_PACKET(this, obj);
		}

		if(this.getFight() != null && this.getFight().get_state() == 2) 
			SocketManager.GAME_SEND_ALTER_FIGHTER_MOUNT(this.getFight(), this.getFight().getFighterByPerso(this), this.getId(), this.getFight().getTeamID(this.getId()), this.getFight().getOtherTeamID(this.getId()));
		else
			SocketManager.GAME_SEND_ALTER_GM_PACKET(this.getMap(), this);
		
		SocketManager.GAME_SEND_Re_PACKET(this, "+", this.getMount());
		SocketManager.GAME_SEND_Rr_PACKET(this, this.isOnMount() ? "+" : "-");
		SocketManager.GAME_SEND_STATS_PACKET(this);
	}
	
	public void resetVars()
	{
		this.isTradingWith = 0;
		this.isTalkingWith = 0;
		this.isAway = false;
		setEmoteActive(0);
		this.fight = null;
		this.isReady = false;
		this.curExchange = null;
		this.group = null;
		this.isInBank = false;
		this.inviting = 0;
		this.sitted = false;
		this.curJobAction = null;
		this.isZaaping = false;
		this.curMountPark = null;
		this.isOnMount = false;
		this.isOnCollector = 0;
		this.isClone = false;
		this.isForgetingSpell = false;
		this.isAbsent = false;
		this.isInvisible = false;
		this.followers.clear();
		this.follow = null;
		this.curTrunk = null;
		this.curHouse = null;
		this.isGhosts = false;
	}
	
	public void addChanel(String chan)
	{
		if(this.canaux.indexOf(chan) >= 0)
			return;
		this.canaux += chan;
		SocketManager.GAME_SEND_cC_PACKET(this, '+', chan);
	}
	
	public void removeChanel(String chan)
	{
		this.canaux = this.canaux.replace(chan, "");
		SocketManager.GAME_SEND_cC_PACKET(this, '-', chan);
	}

	public void modifAlignement(byte a)
	{
		this.setHonor(0);
		this.setDeshonor(0);
		this.setAlign(a);
		this.setaLvl(1);
		//envoies des packets
		//Im022;10~42 ?
		SocketManager.GAME_SEND_ZC_PACKET(this, a);
		SocketManager.GAME_SEND_STATS_PACKET(this);
		//Im045;50 ?
	}

	public void toggleWings(char c)
	{
		if(this.getAlign() == Constants.ALIGNEMENT_NEUTRE)
			return;
		
		int hloose = this.getHonor() * 5 / 100;//FIXME: perte de X% honneur
		switch(c) {
			case '*':
				if(this.getDeshonor() > 0) {
					SocketManager.GAME_SEND_MESSAGE(this, "Vous avez " + this.getDeshonor() + " point"+(this.getDeshonor() == 1 ? "" : "s")+ "de d�shonneur. Action impossible.", Server.config.getMotdColor());
					return;
				}
				SocketManager.GAME_SEND_GIP_PACKET(this,hloose);
			return;
			case '+':
				setShowWings(true);
				SocketManager.GAME_SEND_STATS_PACKET(this);
				
				save();
			break;
			case '-':
				if(this.getDeshonor() > 0) {
					SocketManager.GAME_SEND_MESSAGE(this, "Vous avez " + this.getDeshonor() + " point"+(this.getDeshonor() == 1 ? "" : "s")+ "de d�shonneur. Action impossible.", Server.config.getMotdColor());
					return;
				}
				setShowWings(false);
				this.honor -= hloose;
				SocketManager.GAME_SEND_STATS_PACKET(this);
				save();
			break;
		}
		refresh(false);
	}

	public void addHonor(int winH)
	{
		int g = getGrade();
		this.honor += winH;
		 SocketManager.GAME_SEND_Im_PACKET(this, "080;"+winH);
		if(getGrade() != g)
			SocketManager.GAME_SEND_Im_PACKET(this, "082;"+getGrade());
	}

	public String parseZaapList()//Pour le packet WC
	{
		String map = Integer.toString(this.getMap().getId());
		try {
			map = this.savePos.split(",")[0];
		} catch(Exception e) {}
		
		StringBuilder str = new StringBuilder();
		str.append(map);
	        int SubAreaID = this.getMap().getSubArea().getArea().getContinent().getId();
		for(int i : this.zaaps) {
			if(World.data.getMap(i) == null)
				continue;
			if(World.data.getMap(i).getSubArea().getArea().getContinent().getId() != SubAreaID)
				continue;
			int cost = Formulas.calculZaapCost(this.getMap(), World.data.getMap(i));
			if(i == this.getMap().getId())
				cost = 0;
			str.append("|").append(i).append(";").append(cost);
		}
		return str.toString();
	}
	
	public boolean hasZaap(int mapID)
	{
		for(int i : this.zaaps)
			if(i == mapID)
				return true;
		return false;
	}

	public void openZaapMenu()
	{
		if(this.getFight() == null) {//On ouvre si il n'est pas en combat
			if(getDeshonor() >= 3) {
				SocketManager.GAME_SEND_Im_PACKET(this, "183");
				return;
			}
			this.setZaaping(true);
			if(!hasZaap(this.getMap().getId())) {//Si le joueur ne connaissait pas ce zaap
				this.zaaps.add(this.getMap().getId());
				SocketManager.GAME_SEND_Im_PACKET(this, "024");
				save();
			}
			SocketManager.GAME_SEND_WC_PACKET(this);
		}
	}
	public void useZaap(short id)
	{
		if(!this.isZaaping())
			return;//S'il n'a pas ouvert l'interface Zaap(hack?)
		if(this.getFight() != null) 
			return;//Si il combat
		if(!hasZaap(id))
			return;//S'il n'a pas le zaap demand�(ne devrais pas arriver)
		
		int cost = Formulas.calculZaapCost(this.getMap(), World.data.getMap(id));
		if(this.getKamas() < cost)
			return;//S'il n'a pas les kamas (verif cot� client)
		
		short mapID = id;
		int SubAreaID = this.getMap().getSubArea().getArea().getContinent().getId();
		int cellID = World.data.getZaapCellIdByMapId(id);
		if(World.data.getMap(mapID) == null)
		{
			Log.addToLog("La map "+id+" n'est pas implantee, Zaap refuse");
			SocketManager.GAME_SEND_WUE_PACKET(this);
			return;
		}
		if(World.data.getMap(mapID).getCases().get(cellID) == null)
		{
			Log.addToLog("La cellule associee au zaap "+id+" n'est pas implantee, Zaap refuse");
			SocketManager.GAME_SEND_WUE_PACKET(this);
			return;
		}
		if(!World.data.getMap(mapID).getCases().get(cellID).isWalkable(true))
		{
			Log.addToLog("La cellule associee au zaap "+id+" n'est pas 'walkable', Zaap refuse");
			SocketManager.GAME_SEND_WUE_PACKET(this);
			return;
		}
		if(World.data.getMap(mapID).getSubArea().getArea().getContinent().getId() != SubAreaID)
		{
			SocketManager.GAME_SEND_WUE_PACKET(this);
			return;
		}
		this.kamas -= cost;
		setPosition(mapID, cellID);
		SocketManager.GAME_SEND_STATS_PACKET(this);//On envoie la perte de kamas
		SocketManager.GAME_SEND_WV_PACKET(this);//On ferme l'interface Zaap
		this.setZaaping(false);
	}
	public String parseZaaps()
	{
		StringBuilder str = new StringBuilder();
		boolean first = true;
		
		if(this.zaaps.isEmpty())return "";
		for(int i : this.zaaps)
		{
			if(!first) str.append(",");
			first = false;
			str.append(i);
		}
		return str.toString();
	}
	public void stopZaaping()
	{
		if(!this.isZaaping())
			return;
		this.setZaaping(false);
		SocketManager.GAME_SEND_WV_PACKET(this);
	}
	
	public void Zaapi_close()
	{
		if(!this.isZaaping())
			return;
		this.setZaaping(false);
		SocketManager.GAME_SEND_CLOSE_ZAAPI_PACKET(this);
	}
	
	public void Zaapi_use(String packet)
	{
		Maps map = World.data.getMap(Short.valueOf(packet.substring(2)));
	
		short idcelula = 100;
		if (map != null)
		{
			for (Entry<Integer, Case> entry  : map.getCases().entrySet())
			{
			InteractiveObject obj = entry.getValue().getInteractiveObject();
			if (obj != null)
				{
				if (obj.getId() == 7031 || obj.getId() == 7030)
					{
						idcelula = (short) (entry.getValue().getId() + 18);
					}
				}
			}
		}
		if (map.getSubArea().getArea().getId() == 7 || map.getSubArea().getArea().getId() == 11)
		{
		int price = 20;
		if (this.getAlign() == 1 || this.getAlign() == 2)
		price = 10;
		this.kamas -= price;
		SocketManager.GAME_SEND_STATS_PACKET(this);
		this.setPosition(Short.valueOf(packet.substring(2)), idcelula);
		SocketManager.GAME_SEND_CLOSE_ZAAPI_PACKET(this);
		}
	}
		
	public boolean hasItemTemplate(int i, int q)
	{
		for(Object obj : this.objects.values())
		{
			if(obj.getPosition() != Constants.ITEM_POS_NO_EQUIPED)continue;
			if(obj.getTemplate().getId() != i)continue;
			if(obj.getQuantity() >= q)return true;
		}
		return false;
	}


	
	public boolean isDispo(Player sender)
	{
		if(this.isAbsent())
			return false;
		if(this.isInvisible())
			return this.getAccount().isFriendWith(sender.getAccount().getUUID());		
		return true;
	}
	

	
	public static Player ClonePerso(Player P, int id){
		Player Clone = new Player(P,id);
		
		Clone.setClone(true);
		if(P.isOnMount())
			Clone.setOnMount(true);
		return Clone;
	}
	
	public void VerifAndChangeItemPlace()
	{
		boolean isFirstAM = true;
		boolean isFirstAN = true;
		boolean isFirstANb = true;
		boolean isFirstAR = true;
		boolean isFirstBO = true;
		boolean isFirstBOb = true;
		boolean isFirstCA = true;
		boolean isFirstCE = true;
		boolean isFirstCO = true;
		boolean isFirstDa = true;
		boolean isFirstDb = true;
		boolean isFirstDc = true;
		boolean isFirstDd = true;
		boolean isFirstDe = true;
		boolean isFirstDf = true;
		boolean isFirstFA = true;
		for(Object obj : this.objects.values())
		{
			if(obj.getPosition() == Constants.ITEM_POS_NO_EQUIPED)continue;
			if(obj.getPosition() == Constants.ITEM_POS_AMULETTE)
			{
				if(isFirstAM)
				{
					isFirstAM = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_ANNEAU1)
			{
				if(isFirstAN)
				{
					isFirstAN = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_ANNEAU2)
			{
				if(isFirstANb)
				{
					isFirstANb = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_ARME)
			{
				if(isFirstAR)
				{
					isFirstAR = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_BOTTES)
			{
				if(isFirstBO)
				{
					isFirstBO = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_BOUCLIER)
			{
				if(isFirstBOb)
				{
					isFirstBOb = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_CAPE)
			{
				if(isFirstCA)
				{
					isFirstCA = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_CEINTURE)
			{
				if(isFirstCE)
				{
					isFirstCE = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_COIFFE)
			{
				if(isFirstCO)
				{
					isFirstCO = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_DOFUS1)
			{
				if(isFirstDa)
				{
					isFirstDa = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_DOFUS2)
			{
				if(isFirstDb)
				{
					isFirstDb = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_DOFUS3)
			{
				if(isFirstDc)
				{
					isFirstDc = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_DOFUS4)
			{
				if(isFirstDd)
				{
					isFirstDd = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_DOFUS5)
			{
				if(isFirstDe)
				{
					isFirstDe = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_DOFUS6)
			{
				if(isFirstDf)
				{
					isFirstDf = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
			else if(obj.getPosition() == Constants.ITEM_POS_FAMILIER)
			{
				if(isFirstFA)
				{
					isFirstFA = false;
				}else
				{
					obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
				}
				continue;
			}
		}
		
	}
	
	
	//Mariage
	
	public void MarryTo(Player wife)
	{
		this.setWife(wife.getId());
		save();
	}
	
	public String get_wife_friendlist()
	{
		Player wife = World.data.getPlayer(this.getWife());
		StringBuilder str = new StringBuilder();
		if(wife != null)
		{
			str.append(wife.getName()).append("|").append(wife.getClasse().getId()+wife.getSex()).append("|").append(wife.getColor1()).append("|").append(wife.getColor2()).append("|").append(wife.getColor3()).append("|");
			if(!wife.isOnline()){
				str.append("|");
			}else{
			str.append(wife.parse_towife()).append("|");
			}
		}else{
			str.append("|");
		}
		return str.toString();
	}
	
	public String parse_towife()
	{
		int f = 0;
		if(this.getFight() != null)
		{
			f = 1;
		}
		return this.getMap().getId() + "|" + this.getLevel() + "|" + f;
	}
	
	public void meetWife(Player p)// Se teleporter selon les sacro-saintes autorisations du mariage.
	{
		if(p == null)return; // Ne devrait theoriquement jamais se produire.
		
		int dist = (this.getMap().getX() - p.getMap().getX())*(this.getMap().getX() - p.getMap().getX())
					+ (this.getMap().getY() - p.getMap().getY())*(this.getMap().getY() - p.getMap().getY());
		if(dist > 100)// La distance est trop grande...
		{
			if(p.getSex() == 0)
			{
				SocketManager.GAME_SEND_Im_PACKET(this, "178");
			}else
			{
				SocketManager.GAME_SEND_Im_PACKET(this, "179");
			}
			return;
		}
		
		int cellPositiontoadd = Constants.getNearCellidUnused(p);
		if(cellPositiontoadd == -1)
		{
			if(p.getSex() == 0)
			{
				SocketManager.GAME_SEND_Im_PACKET(this, "141");
			}else
			{
				SocketManager.GAME_SEND_Im_PACKET(this, "142");
			}
			return;
		}
		
		setPosition(p.getMap().getId(), (p.getCell().getId() + cellPositiontoadd));
	}
	
	public void Divorce()
	{
		if(isOnline())
			SocketManager.GAME_SEND_Im_PACKET(this, "047;"+World.data.getPlayer(this.getWife()).getName());
		this.setWife(0);
		save();
	}
	
	public void changeOrientation(int toOrientation)
	{
		if(this.getOrientation() == 0 || this.getOrientation() == 2 
		|| this.getOrientation() == 4 || this.getOrientation() == 6)
		{
			this.setOrientation(toOrientation);
			SocketManager.GAME_SEND_eD_PACKET_TO_MAP(this.getMap(), this.getId(), toOrientation);
		}
	}

	public void setGhosts()
	{
		if(isOnMount()) 
			toogleOnMount();
		this.setGhosts(true);
		this.setGfx(8004);
		this.setCanAggro(false);
		this.setAway(true);
		this.setSpeed(-40);
		this.setPosition((short) 8534, 297);
		//Le teleporter aux zone de mort la plus proche
		/*for(Carte map : ) FIXME
		{
			map.
		}*/
	}
	
	public void setAlive()
	{
		if(!this.isGhosts()) 
			return;
		this.setGhosts(false);
		this.setEnergy(1000);
		this.setGfx(Integer.parseInt(getClasse().getId()+""+getSex()));
		this.setCanAggro(true);
		this.setAway(false);
		this.setSpeed(0);
		SocketManager.GAME_SEND_STATS_PACKET(this);
		SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this.getMap(), this.getId());
		SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(this.getMap(), this);
	}
   

	
    public String parseStoreItemsList() 
    {
    	StringBuilder list = new StringBuilder();
        if(this.getStores().isEmpty())return "";
        for(Entry<Integer,Integer> obj : this.getStores().entrySet()) 
        {
        	Object O = World.data.getObject(obj.getKey());
        	if(O == null) continue;
        	list.append(O.getId()).append(";").append(O.getQuantity()).append(";").append(O.getTemplate().getId()).append(";").append(O.parseStatsString()).append(";").append(obj.getValue()).append("|");
        }
        return (list.length()>0?list.toString().substring(0, list.length()-1):list.toString());
    }
    
    public String parseStoreItemstoBD()
    {
    	StringBuilder str = new StringBuilder();
		for(Entry<Integer, Integer> _storeObjects : this.getStores().entrySet())
		{
			str.append(_storeObjects.getKey()).append(",").append(_storeObjects.getValue()).append("|");
		}
		return str.toString();
    }

    public void addinStore(int objectId, int price, int quantity) {
        Object objet = this.objects.get(objectId);
		if(this.objects.get(objectId) == null){
			return;
		}

		Object SimilarObj = getSimilarStoreItem(objet);
		int newQuantity = objet.getQuantity() - quantity;
		if(SimilarObj == null)//S'il n'y pas d'item du meme Template
		{
			//S'il ne reste pas d'item dans le sac
			if(newQuantity <= 0){
				//On enleve l'objet du sac du joueur
				this.objects.remove(objectId);
				//On met l'objet du sac dans le store, avec la meme quantit�
				this.getStores().put(objectId, price);
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this, objectId);
                SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(this, this);
			}
			else//S'il reste des objets au joueur
			{
				//on modifie la quantit� d'item du sac
				objet.setQuantity(newQuantity);
				//On ajoute l'objet a la banque et au monde
				SimilarObj = Object.getClone(objet, quantity);
				World.data.addObject(SimilarObj, true);
				this.getStores().put(SimilarObj.getId(), price);
				
				//Envoie des packets
				SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(this, this);
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this, objet);
				
			}
		}else // S'il y avait un item du meme template
		{
			//S'il ne reste pas d'item dans le sac
			if(newQuantity <= 0)
			{
				//On enleve l'objet du sac du joueur
				removeItem(objet.getId());
				//On enleve l'objet du monde
				World.data.removeObject(objet.getId());
				//On ajoute la quantit� a l'objet en banque
				SimilarObj.setQuantity(SimilarObj.getQuantity() + objet.getQuantity());
				this.getStores().remove(SimilarObj.getId());
				this.getStores().put(SimilarObj.getId(), price);
				//on envoie l'ajout a la banque de l'objet
				SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(this, this);
				//on envoie la supression de l'objet du sac au joueur
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this, objet.getId());
			}else //S'il restait des objets
			{
				//on modifie la quantit� d'item du sac
				objet.setQuantity(newQuantity);
				SimilarObj.setQuantity(SimilarObj.getQuantity() + quantity);
				this.getStores().remove(SimilarObj.getId());
				this.getStores().put(SimilarObj.getId(), price);
				SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(this, this);
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this, objet);
				
			}
		}
		SocketManager.GAME_SEND_Ow_PACKET(this);
		save();
    }

	private Object getSimilarStoreItem(Object obj)
	{
		for(Entry<Integer, Integer> value : this.getStores().entrySet())
		{
			Object obj2 = World.data.getObject(value.getKey());
			if(obj2.getTemplate().getType() == 85)
				continue;
			if(obj2.getTemplate().getId() == obj.getTemplate().getId() && obj2.getStats().isSameStats(obj.getStats()))
				return obj2;
		}
		return null;
	}
	
	public void removeFromStore(int guid, int qua)
	{
		Object SimilarObj = World.data.getObject(guid);
		//Si le joueur n'a pas l'item dans son store ...
		if(this.getStores().get(guid) == null)
		{
			Log.addToLog("Le joueur "+this.getName()+" a tenter de retirer un objet du store qu'il n'avait pas.");
			return;
		}
		
		Object PersoObj = getSimilarItem(SimilarObj);
		
		int newQua = SimilarObj.getQuantity() - qua;
		
		if(PersoObj == null)//Si le joueur n'avait aucun item similaire
		{
			//S'il ne reste rien en store
			if(newQua <= 0)
			{
				//On retire l'item du store
				this.getStores().remove(guid);
				//On l'ajoute au joueur
				this.objects.put(guid, SimilarObj);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OAKO_PACKET(this,SimilarObj);
				SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(this, this);
				
			}
		}
		else
		{
			//S'il ne reste rien en store
			if(newQua <= 0)
			{
				//On retire l'item de la banque
				this.getStores().remove(SimilarObj.getId());
				World.data.removeObject(SimilarObj.getId());
				//On Modifie la quantit� de l'item du sac du joueur
				PersoObj.setQuantity(PersoObj.getQuantity() + SimilarObj.getQuantity());
				
				//On envoie les packets
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this, PersoObj);
				SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(this, this);
				
			}
		}
		SocketManager.GAME_SEND_Ow_PACKET(this);
		save();
	}
	
	public void removeStoreItem(int guid)
	{
		this.getStores().remove(guid);
	}
	
	public void addStoreItem(int guid, int price)
	{
		this.getStores().put(guid, price);
	}

	public void sendText(String text) {
		if(!text.isEmpty())
			SocketManager.GAME_SEND_MESSAGE(this, text, Server.config.getMotdColor());
	}

	public void save() {
		World.database.getCharacterData().update(this);
		World.database.getAccountData().update(this.getAccount());
	}
	
	public void refresh(boolean smoke) {
		if(!smoke)
			SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(this.getMap(), this);
		else
			SocketManager.GAME_SEND_ALTER_GM_PACKET(this.getMap(), this);
	}

    public void setSitted(boolean sitted){
        if(this.sitted == sitted){
            return;
        }
        this.sitted = sitted;
        refreshLife();
        regenRate = (sitted ? 1000 : 2000);
        SocketManager.GAME_SEND_ILS_PACKET(this, regenRate);
    }

    void refreshLife() {
        long time = (System.currentTimeMillis()-regenTime);
        regenTime = System.currentTimeMillis();
        if(fight != null){
            return;
        }
        int diff = (int)time/regenRate;
        if(diff>=10 && this.pdv != this.maxPdv){
            send("ILF" + diff);
        }
        setPdv(pdv+diff);
    }

    /**
     * Envoie un message au player
     * @param message message a faire parvenir
     */
    public void send(String message) {
        this.account.send(message);
    }
}