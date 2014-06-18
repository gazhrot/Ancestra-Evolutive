package org.ancestra.evolutive.map;

import org.ancestra.evolutive.area.SubArea;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.*;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.collector.Collector;
import org.ancestra.evolutive.entity.monster.MobGrade;
import org.ancestra.evolutive.entity.monster.MobGroup;
import org.ancestra.evolutive.entity.npc.Npc;
import org.ancestra.evolutive.entity.npc.NpcTemplate;
import org.ancestra.evolutive.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.object.Objet;
import org.ancestra.evolutive.other.Action;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Maps {
	
	private short id;
	private String date;
	private byte x = 0;
	private byte y = 0;
	private byte width;
	private byte height;
	private String key;
	private String places;
	private byte maxGroup = 3;
	private byte maxSize;
	private int nextObject = -1;
	private SubArea subArea;
	private MountPark mountPark;	
	
	private Map<Integer, Npc> npcs = new TreeMap<>();
	private Map<Integer, Case> cases = new TreeMap<>();
	private Map<Integer, Fight> fights = new TreeMap<>();
	private Map<Integer, MobGroup> mobGroups = new TreeMap<>();
	private Map<Integer, MobGroup> fixMobGroups = new TreeMap<>();
	private Map<Integer, ArrayList<Action>> endFightAction = new TreeMap<>();
	private ArrayList<MobGrade> mobPossibles = new ArrayList<>();

	public Maps(short id, String date, byte width, byte height, String key, String places, String mapData, String cells, String monsters, String pos, byte maxGroup, byte maxSize) {
		this.id = id;
		this.date = date;
		this.width = width;
		this.height = height;
		this.key = key;
		this.places = places;
		this.maxGroup = maxGroup;
		this.maxSize = maxSize;
		try	{
			String[] infos = pos.split(",");
			this.x = Byte.parseByte(infos[0]);
			this.y = Byte.parseByte(infos[1]);
			this.subArea = World.data.getSubArea(Integer.parseInt(infos[2]));
			if(this.subArea != null)
				this.subArea.addMap(this);
		} catch(Exception e) {
			Log.addToLog(" > Erreur lors du chargement de la map "+ this.id + ", position invalide !");
			System.exit(0);
		}
		
		if(!mapData.isEmpty()) {
			this.cases = CryptManager.DecompileMapData(this, mapData);
		} else {
			String[] data = cells.split("\\|");
			
			for(String cell: data) {
				boolean walkable = true, lineOfSight = true;
				int num = -1, obj = -1;
				String[] cellInfos = cell.split(",");
				try	{
					walkable = cellInfos[2].equals("1");
					lineOfSight = cellInfos[1].equals("1");
					num = Integer.parseInt(cellInfos[0]);
					if(!cellInfos[3].trim().equals(""))
					{
						obj = Integer.parseInt(cellInfos[3]);
					}
				} catch(Exception e) {}
				if(num == -1)
					continue;
				
	            this.cases.put(num, new Case(this, num, walkable, lineOfSight, obj));	
			}
		}
		for(String mob : monsters.split("\\|")) {
			if(mob.equals(""))
				continue;
			
			int uid = 0, lvl = 0;
	
			try	{
				uid = Integer.parseInt(mob.split(",")[0]);
				lvl = Integer.parseInt(mob.split(",")[1]);
			} catch(NumberFormatException e) {
				continue;
			}
			
			if(uid == 0 || lvl == 0)
				continue;
			if(World.data.getMonstre(uid) == null)
				continue;
			if(World.data.getMonstre(uid).getGradeByLevel(lvl) == null)
				continue;
			
			this.mobPossibles.add(World.data.getMonstre(uid).getGradeByLevel(lvl));
		}
		
		if(this.cases.isEmpty())
			return;
		
		if(Server.config.isUseMobs()) {
			if(this.maxGroup == 0)
				return;
			this.spawnGroup(Constants.ALIGNEMENT_NEUTRE, this.maxGroup, false, -1);//Spawn des groupes d'alignement neutre 
			this.spawnGroup(Constants.ALIGNEMENT_BONTARIEN, 1, false, -1);//Spawn du groupe de gardes bontarien s'il y a
			this.spawnGroup(Constants.ALIGNEMENT_BRAKMARIEN, 1, false, -1);//Spawn du groupe de gardes brakmarien s'il y a
		}
	}
	
	public Maps(short id, String date, byte width, byte height, String key, String places) {
		this.id = id;
		this.date = date;
		this.width = width;
		this.height = height;
		this.key = key;
		this.places = places;
		this.cases = new TreeMap<Integer, Case>();
	}

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public byte getX() {
		return x;
	}

	public void setX(byte x) {
		this.x = x;
	}

	public byte getY() {
		return y;
	}

	public void setY(byte y) {
		this.y = y;
	}

	public byte getWidth() {
		return width;
	}

	public void setWidth(byte width) {
		this.width = width;
	}

	public byte getHeight() {
		return height;
	}

	public void setHeight(byte height) {
		this.height = height;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPlaces() {
		return places;
	}

	public void setPlaces(String places) {
		this.places = places;
	}

	public byte getMaxGroup() {
		return maxGroup;
	}

	public void setMaxGroup(byte maxGroup) {
		this.maxGroup = maxGroup;
	}

	public byte getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(byte maxSize) {
		this.maxSize = maxSize;
	}

	public int getNextObject() {
		return nextObject;
	}

	public void setNextObject(int nextObject) {
		this.nextObject = nextObject;
	}

	public SubArea getSubArea() {
		return subArea;
	}

	public void setSubArea(SubArea subArea) {
		this.subArea = subArea;
	}

	public MountPark getMountPark() {
		return mountPark;
	}

	public void setMountPark(MountPark mountPark) {
		this.mountPark = mountPark;
	}

	public Map<Integer, Npc> getNpcs() {
		return npcs;
	}

	public void setNpcs(Map<Integer, Npc> npcs) {
		this.npcs = npcs;
	}

	public Map<Integer, Case> getCases() {
		return cases;
	}

	public void setCases(Map<Integer, Case> cases) {
		this.cases = cases;
	}

	public Map<Integer, Fight> getFights() {
		return fights;
	}

	public void setFights(Map<Integer, Fight> fights) {
		this.fights = fights;
	}

	public Map<Integer, MobGroup> getMobGroups() {
		return mobGroups;
	}

	public void setMobGroups(Map<Integer, MobGroup> mobGroups) {
		this.mobGroups = mobGroups;
	}

	public Map<Integer, MobGroup> getFixMobGroups() {
		return fixMobGroups;
	}

	public void setFixMobGroups(Map<Integer, MobGroup> fixMobGroups) {
		this.fixMobGroups = fixMobGroups;
	}

	public Map<Integer, ArrayList<Action>> getEndFightAction() {
		return endFightAction;
	}

	public void setEndFightAction(Map<Integer, ArrayList<Action>> endFightAction) {
		this.endFightAction = endFightAction;
	}

	public ArrayList<MobGrade> getMobPossibles() {
		return mobPossibles;
	}

	public void setMobPossibles(ArrayList<MobGrade> mobPossibles) {
		this.mobPossibles = mobPossibles;
	}
	
	public void addPlayer(Player player) {
		SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(this, player);
		player.getCell().addPlayer(player);
	}
	
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for(Case cell: this.getCases().values())
			for(Player player : cell.getPlayers().values())
				players.add(player);
		return players;
	}

	public void applyEndFightAction(int type, Player player) {
		if(this.getEndFightAction().get(type) == null)
			return;
		
		for(Action action: this.getEndFightAction().get(type))
			action.apply(player, null, -1, -1);
	}
	
	public void addEndFightAction(int type, Action action) {
		if(this.getEndFightAction().get(type) == null)
			this.getEndFightAction().put(type, new ArrayList<Action>());
		
		this.delEndFightAction(type, action.getId());
		this.getEndFightAction().get(type).add(action);
	}
	
	public void delEndFightAction(int type1, int type2) {
		if(this.getEndFightAction().get(type1) == null)
			return;
		
		ArrayList<Action> copy = new ArrayList<Action>();
		copy.addAll(this.getEndFightAction().get(type1));
		
		for(Action A : copy)if(A.getId() == type2)
			this.getEndFightAction().get(type1).remove(A);
	}

	public Npc addNpc(int id,int cell, int dir) {
		NpcTemplate template = World.data.getNpcTemplate(id);
		
		if(template == null || this.getCases().get(cell) == null)
			return null;
		
		Npc npc = new Npc(template, this.getNextObject(),this, cases.get(cell), (byte) dir);
		this.getNpcs().put(this.getNextObject(), npc);
		this.nextObject--;
		return npc;
	}
	
	public void spawnGroup(int align, int nbr, boolean log, int cell) {
		if((nbr < 1) || (this.getMobGroups().size() - this.getFixMobGroups().size() >= this.getMaxGroup()))
			return;
		
		for(int a = 1; a <= nbr; a++) {
			MobGroup group  = new MobGroup(this.getNextObject(), align, this.getMobPossibles(), this, cases.get(cell), this.getMaxSize());
			
			if(group.getMobs().isEmpty())
				continue;
			
			this.getMobGroups().put(this.getNextObject(), group);
			
			if(log)
				SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, group);
			
			this.nextObject--;
		}
	}
	
	public void spawnNewGroup(boolean timer, Case cell, String data, String condition) {
		MobGroup group = new MobGroup(this.getNextObject(),this,cell, data,condition);
		
		if(group.getMobs().isEmpty())
			return;
		
		this.getMobGroups().put(this.getNextObject(), group);
		group.setFix(false);
		SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, group);
		this.nextObject--;
		
		if(timer)
			group.startTimer();
	}
	
	public void spawnGroupOnCommand(int cell, String data) {
		MobGroup group = new MobGroup(this.getNextObject(),this,cases.get(cell),data);
		
		if(group.getMobs().isEmpty())
			return;
		
		this.getMobGroups().put(this.getNextObject(), group);
		group.setFix(false);
		SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, group);
		this.nextObject--;
	}
	
	public void addStaticGroup(int cell, String data) {
		MobGroup group = new MobGroup(this.getNextObject(), this,cases.get(cell), data);
		
		if(group.getMobs().isEmpty())
			return;
		
		this.getMobGroups().put(this.getNextObject(), group);
		this.nextObject--;
		this.getFixMobGroups().put(-1000 + this.getNextObject(), group);
		SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, group);
	}
	
	public Fight newFight(Player init1, Player init2, int type, boolean protect) {
		int id = 1;
		
		if(!this.getFights().isEmpty())
			id = ((Integer) (this.getFights().keySet().toArray()[this.getFights().size() - 1])) + 1;
		
		Fight fight = new Fight(type, id, this, init1, init2, protect);
		this.getFights().put(id, fight);
		SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
		return fight;
	}
	
	public void startFightVersusProtectors(Player player, MobGroup group) {
		if(!player.isCanAggro())
			return;
		
		int id = 1;
		
		if(!this.getFights().isEmpty())
			id = ((Integer) (this.getFights().keySet().toArray()[this.getFights().size() - 1])) + 1;
		
		this.getFights().put(id, new Fight(id, this, player, group, Constants.FIGHT_TYPE_PVM));
		SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
	}
	
	public int getRandomFreeCell() {
		ArrayList<Integer> cases = new ArrayList<>();
		
		for(Entry<Integer, Case> entry : this.getCases().entrySet()) {
			if(!entry.getValue().isWalkable(true))
				continue;

			boolean ok = true;
			
			for(Entry<Integer,MobGroup> mgEntry : this.getMobGroups().entrySet())
				if(mgEntry.getValue().getCell().getId() == entry.getValue().getId())
					ok = false;
		
			if(!ok)
				continue;

			ok = true;
			for(Entry<Integer, Npc> npc : this.getNpcs().entrySet())
				if(npc.getValue().getCell().getId() == entry.getValue().getId())
					ok = false;
			
			if(!ok)
				continue;
			
			if(!entry.getValue().getPlayers().isEmpty())
				continue;

			cases.add(entry.getValue().getId());
		}
		
		if(cases.isEmpty())	{
			Log.addToLog(" > Aucune cellule libre trouv� sur la map " + this.getId() + " !");
			return -1;
		}

		return cases.get(Formulas.getRandomValue(0, cases.size() - 1));
	}
	
	public void refreshSpawns() {
		for(int id : this.getMobGroups().keySet())
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this, id);

		this.getMobGroups().clear();
		this.getMobGroups().putAll(this.getFixMobGroups());
		
		for(MobGroup mg : this.getFixMobGroups().values())
			SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, mg);

		this.spawnGroup(Constants.ALIGNEMENT_NEUTRE, this.getMaxGroup(), true, -1);
		this.spawnGroup(Constants.ALIGNEMENT_BONTARIEN, 1, true, -1);
		this.spawnGroup(Constants.ALIGNEMENT_BRAKMARIEN, 1, true, -1);
	}
	
	public void onPlayerArriveOnCell(Player player, int cell) {
		if(this.getCases().get(cell) == null)
			return;
		
		Objet obj = this.getCases().get(cell).getObject();
		
		if(obj != null) {
			this.getCases().get(cell).setObject(null);
			if(player.addObjet(obj, true))
				World.data.addObjet(obj, true);
			SocketManager.GAME_SEND_GDO_PACKET_TO_MAP(this,'-', cell,0,0);
			SocketManager.GAME_SEND_Ow_PACKET(player);	
		}
		
		this.getCases().get(cell).applyOnCellStopActions(player);
		
		if(this.getPlaces().equalsIgnoreCase("|")) 
			return;

		if(player.getMap().getId() != this.getId() || !player.isCanAggro())
			return;
		
		for(MobGroup group : this.getMobGroups().values()) {
			if(Pathfinding.getDistanceBetween(this, cell,group.getCell().getId()) <= group.getAggroDistance()) {
				if((group.getAlign() == -1 || ((player.getAlign() == 1 || player.getAlign() == 2) && (player.getAlign() != group.getAlign()))) && ConditionParser.validConditions(player, group.getCondition())) {
					Log.addToLog(" > Le joueur " + player.getName() + " rentre en combat contre un groupe de monstre (" + group.getId() + ") !");
					startFigthVersusMonstres(player,group);
					return;
				}
			}
		}
	}
	
	public void startFigthVersusMonstres(Player player, MobGroup group) {
		int id = 1;
		
		if(!this.getFights().isEmpty())
			id = ((Integer) (this.getFights().keySet().toArray()[this.getFights().size() - 1])) + 1;
		
		if(!group.isFix())
			this.getMobGroups().remove(group.getId());
		else 
			SocketManager.GAME_SEND_MAP_MOBS_GMS_PACKETS_TO_MAP(this);
		
		this.getFights().put(id, new Fight(id, this, player, group));
		SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
	}
	
	public void startFigthVersusPercepteur(Player player, Collector collector) {
		int id = 1;
		
		if(!this.getFights().isEmpty())
			id = ((Integer)(this.getFights().keySet().toArray()[this.getFights().size() - 1])) + 1;

		this.getFights().put(id, new Fight(id, this, player, collector));
		SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
	}

	public Maps getMapCopy() {
		Map<Integer,Case> cases = new TreeMap<>();
		Maps map = new Maps(this.getId(), this.getDate(), this.getWidth(), this.getHeight(), this.getKey(), this.getPlaces());
		
		for(Entry<Integer,Case> entry : this.getCases().entrySet())
			cases.put(entry.getKey(), new Case(map, entry.getValue().getId(),	entry.getValue().isWalkable(false),	entry.getValue().isLoS(),
					(entry.getValue().getInteractiveObject()==null?-1:entry.getValue().getInteractiveObject().getId())));
		
		map.setCases(cases);
		return map;
	}

	public InteractiveObject getMountParkDoor() {
		for(Case cell: this.getCases().values()) 
			if(cell.getInteractiveObject() != null)
				if(cell.getInteractiveObject().getId() == 6763 || cell.getInteractiveObject().getId() == 6766 
				|| cell.getInteractiveObject().getId() == 6767 || cell.getInteractiveObject().getId() == 6772)
					return cell.getInteractiveObject();
		return null;
	}

	public void sendFloorItems(Player player) {
		for(Case cell: this.getCases().values())
			if(cell.getObject() != null)
				SocketManager.GAME_SEND_GDO_PACKET(player, '+', cell.getId(), cell.getObject().getTemplate().getID(), 0);
	}

	public int getStoreCount() {
		return (World.data.getSeller(getId()) == null ? 0 : World.data.getSeller(getId()).size());
	}
	
	public String getGMsPackets() {
		StringBuilder packet = new StringBuilder();
		for(Case cell: this.getCases().values())
			for(Player player : cell.getPlayers().values())
				packet.append("GM|+").append(player.getHelper().getGmPacket()).append('\u0000');
		return packet.toString();
	}
	
	public String getFightersGMsPackets() {
		StringBuilder packet = new StringBuilder();
		for(Entry<Integer,Case> cell : this.getCases().entrySet())
			for(Entry<Integer, Fighter> f : cell.getValue().getFighters().entrySet())
				packet.append(f.getValue().getGmPacket('+')).append('\u0000');
		return packet.toString();
	}
	
	public String getMobGroupGMsPackets() {
		if(this.getMobGroups().isEmpty())
			return "";
		
		StringBuilder packet = new StringBuilder().append("GM|+");
		boolean isFirst = true;
		
		for(MobGroup entry : this.getMobGroups().values()) {
			String GM = entry.getHelper().getGmPacket();
			if(GM.equals(""))
				continue;
			if(!isFirst)
				packet.append("|+");
			
			packet.append(GM);
			isFirst = false;
		}
		return packet.toString();
	}
	
	public String getNpcsGMsPackets() {
		if(this.getNpcs().isEmpty())
			return "";
		
		StringBuilder packet = new StringBuilder().append("GM|+");
		boolean isFirst = true;
		
		for(Npc entry : this.getNpcs().values()) {
			String GM = entry.getHelper().getGmPacket();
			if(GM.equals(""))
				continue;
			if(!isFirst)
				packet.append("|+");
			
			packet.append(GM);
			isFirst = false;
		}
		return packet.toString();
	}
	
	public String getObjectsGDsPackets() {
		StringBuilder toreturn = new StringBuilder();
		boolean first = true;
		
		for(Entry<Integer, Case> entry : this.getCases().entrySet())	{
			if(entry.getValue().getObject() != null) {
				if(!first)
					toreturn.append((char)0x00);
				first = false;
				int cellID = entry.getValue().getId();
				InteractiveObject object = entry.getValue().getInteractiveObject();
				toreturn.append("GDF|").append(cellID).append(";").append(object.getState()).append(";").append((object.isInteractive() ? "1" : "0"));
			}
		}
		return toreturn.toString();
	}

    public void send(String str){
        for(Player player : this.getPlayers()){
            player.send(str);
        }
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof Maps){
            if(((Maps)object).getId() == this.getId()){
                return true;
            }
        }
        return false;
    }
}
