package org.ancestra.evolutive.map;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.area.SubArea;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.*;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Entity;
import org.ancestra.evolutive.entity.creature.monster.MobGrade;
import org.ancestra.evolutive.entity.creature.monster.MobGroup;
import org.ancestra.evolutive.entity.creature.npc.Npc;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.enums.IdType;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.fight.PVMFight;
import org.ancestra.evolutive.fight.team.Team;
import org.ancestra.evolutive.map.flags.Flag;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.other.Action;
import org.ancestra.evolutive.util.Couple;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class Maps {
    private static Random random = new Random();
    //region Move mobs
    private static class MapTimer extends ScheduledAction{
        private static int i = 0;
        public MapTimer(int timeDelay) {
            super(timeDelay);
        }
        @Override
        public void applyAction() {
            for(Maps map : World.data.getMaps().values()) {
                ArrayList<MobGroup> mobs = new ArrayList<>(map.getMobGroups().values());

                if(mobs.size() == 0)
                    continue;

                MobGroup mob = mobs.get(i%mobs.size());
                mob.setPosition(map, map.getRandomNearFreeCell(mob.getCell(), 5, 25));
                i++;
                i = i%50;
            }
        }
    }
    static {
        GlobalThread.registerAction(new MapTimer(Server.moveMobDelay));
    }
    //endregion

    private final String loadingMapMessage;
    private final String descriptionMapMessage;
    private String GDFPacket = "";

	private final int id;
	private final String date;
    private final byte height;
    private final byte width;
    private final String key;
    private final Logger logger;
    private String places;
    private byte x;
    private byte y;
	private byte maxGroup = 3;
	private byte maxSize;
	private SubArea subArea;
	private MountPark mountPark;
    private ArrayList<Integer> temporaryId = new ArrayList<>();
    private final HashMap<Fight, Couple<Flag,Flag>> flags = new HashMap<>();
    private final java.lang.Object flagLocker = new java.lang.Object();



    private Map<Integer, Entity> entities = new HashMap<>();
	private Map<Integer, Case> cases = new TreeMap<>();
	private Map<Integer, Fight> fights;
	private Map<Integer, MobGroup> fixMobGroups = new TreeMap<>();
	private Map<Integer, ArrayList<Action>> endFightAction = new TreeMap<>();
	private ArrayList<MobGrade> mobPossibles = new ArrayList<>();

    /**
     * Constructeur maximal utilise pour creer une map de deplacement
     * @param id id de la map
     * @param date date de la map (cherchee dans la bdd et utilisée pour decrypter celle-ci)
     * @param width largeur de la map
     * @param height hauteur de la map
     * @param key clé de la map (cherchee dans la bdd et utilisée pour decrypter celle-ci)
     * @param places String representatif des places
     * @param mapData String representatif des cellules sur la map (info non cryptee) prevale sur cells
     * @param cells String representatif des cellules sur la map (info cryptee)
     * @param monsters String representatif des monstres sur la map
     * @param pos String donnant la position de la map dans le monde  : x,y,id de la subArea
     * @param maxGroup  nombre maximum de groupe de monstre sur la case
     * @param maxSize taille maximum des groupes de monstre
     */
	public Maps(int id, String date, byte width, byte height, String key, String places, String mapData, String cells, String monsters, String pos, byte maxGroup, byte maxSize) {
		this(id,date,width,height,key,places);
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
            logger.error("Erreur lors du chargement de la map, position invalide ! (erreur de bdd)");
			System.exit(0);
		}
		
		if(!mapData.isEmpty()) {
			this.cases = decompileMapData(mapData);
		} else {
			this.cases = decompileCells(cells);
		}

        this.mobPossibles = decompileMobPossibility(monsters);

		if(this.cases.isEmpty())
			return;
		refreshSpawns();
	}

    /**
     * Constructeur utilise pour la copie d une map pour creer une map de fight
     * @param id id de la map
     * @param date date de la map (cherchee dans la bdd et utilisée pour decrypter celle-ci)
     * @param width largeur de la map
     * @param height hauteur de la map
     * @param key clé de la map (cherchee dans la bdd et utilisée pour decrypter celle-ci)
     * @param places String representatif des places
     * @param cases Map des cases
     */
    public Maps(int id, String date, byte width, byte height, String key, String places,Map<Integer,Case> cases) {
        this(id,date,width,height,key,places);
        this.cases = cases;
        this.GDFPacket = generateGDFPacket();
    }

    /**
     * Creer une nouvelle map avec le minimum d'information requise
     * @param id id de la map
     * @param date date de la map (cherchee dans la bdd et utilisée pour decrypter celle-ci)
     * @param width largeur de la map
     * @param height hauteur de la map
     * @param key clé de la map (cherchee dans la bdd et utilisée pour decrypter celle-ci)
     * @param places String representatif des places
     */
	public Maps(int id, String date, byte width, byte height, String key, String places) {
		this.id = id;
		this.date = date;
		this.width = width;
		this.height = height;
		this.key = key;
		this.places = places;
		this.cases = new TreeMap<>();
        this.loadingMapMessage =  "GA;2;"+this.getId() +";";
        this.descriptionMapMessage = "GDM|"+id+"|"+date+"|"+key;

        logger = (Logger) LoggerFactory.getLogger("maps." + id);
        logger.trace("Une nouvelle map vient d etre formee son packet GDM est {}",this.descriptionMapMessage);
	}

    /**
     * Retourne une copie de la map initiale vide (pas d entitee dessus)
     * @return copie de la map
     */
    public Maps copy() {
        Map<Integer,Case> cases = new TreeMap<>();
        for(Entry<Integer,Case> entry : this.getCases().entrySet())
            cases.put(entry.getKey(),entry.getValue().copy());
        Maps map = new Maps(this.getId(), this.getDate(), this.getWidth(), this.getHeight(), this.getKey(), this.getPlaces(),cases);
        return map;
    }

    //region Getters and Setters
    public String getDate() {
		return date;
	}

    public int getId() {
        return id;
    }

	public byte getX() {
		return x;
	}

	public byte getY() {
		return y;
	}

	public byte getWidth() {
		return width;
	}

	public byte getHeight() {
		return height;
	}

	public String getKey() {
		return key;
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

	public SubArea getSubArea() {
		return subArea;
	}

	public MountPark getMountPark() {
		return mountPark;
	}

	public void setMountPark(MountPark mountPark) {
		this.mountPark = mountPark;
	}

	public Map<Integer, Npc> getNpcs() {
		return getMap(Npc.class, IdType.PNJ);
	}

	public Map<Integer, Case> getCases() {
		return cases;
	}

	public Map<Integer, Fight> getFights() {
		return fights;
	}

	public Map<Integer, MobGroup> getMobGroups() {
		return getMap(MobGroup.class,IdType.MONSTER_GROUP);
	}

	public Map<Integer, MobGroup> getFixMobGroups() {
		return fixMobGroups;
	}

	public Map<Integer, ArrayList<Action>> getEndFightAction() {
		return endFightAction;
	}

	public ArrayList<MobGrade> getMobPossibles() {
		return mobPossibles;
	}
    //endregion

    /**
     * Ajoute et affiche le personnage sur la map
     * @param entity entitee a charger
     */
	public void addEntity(Entity entity) {
        entity.send(generateLoadingMessage());
        send(loadCharacterMessage(entity));
        registerOnMap(entity);
        entity.send(mapDescriptionMessage());
    }

    public void registerOnMap(Entity entity){
        if(!entities.containsKey(entity.getId())){
            entities.put(entity.getId(), entity);
        }
    }

    /**
     * Retire et efface le personnage de la map
     * @param entity entite a faire disparaitre
     */
    public void removeEntity(Entity entity){
        if(entities.containsKey(entity.getId())){
            entities.remove(entity.getId());
            send(unloadCharacterMessage(entity));
        }
    }

    /**
     * Permet de recuperer la liste des joueurs sur la map n etant pas en combat
     * @return liste des personnages n etant pas en combat sur la map
     */
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<>();
		for(Entry<Integer,Entity> entry : entities.entrySet()){
            if(entry.getKey()>0){
                players.add((Player)entry.getValue());
            }
        }
		return players;
	}

    /**
     * Retourne la liste des entitees du type voulu sur la map
     * @param expectedClasse classe attendue
     * @param idType type d id de l entitee
     * @param <T> classe attendue
     * @return une liste de l ensemble des entitee du type attendu
     */
    public <T extends Entity> ArrayList<T> get(Class<T> expectedClasse,IdType idType){
        ArrayList<T> list = new ArrayList<>();
        for(Entry<Integer,Entity> entry : entities.entrySet()){
            if(entry.getKey()>=idType.MINIMAL_ID && entry.getKey()<=idType.MAXIMAL_ID){
                list.add((T)entry.getValue());
            }
        }
        return list;
    }

    /**
     * Retourne la liste des entitees du type voulu sur la map
     * @param expectedClasse classe attendue
     * @param idType type d id de l entitee
     * @param <T> classe attendue
     * @return une liste de l ensemble des entitee du type attendu
     */
    public <T extends Entity> HashMap<Integer,T> getMap(Class<T> expectedClasse,IdType idType){
        HashMap<Integer,T> list = new HashMap<>();
        for(Entry<Integer,Entity> entry : entities.entrySet()){
            if(entry.getKey()>=idType.MINIMAL_ID && entry.getKey()<=idType.MAXIMAL_ID){
                list.put(entry.getKey(),(T)entry.getValue());
            }
        }
        return list;
    }

    /**
     * Retourne un id du type voulu libre
     * @return id du type libre
     * 0 si aucun id n est libre
     */
    public int getNextFreeId(IdType type){
        int startIndex = type.MAXIMAL_ID-this.id*1000;
        for(int i = startIndex ; i > startIndex-1000 && i >= type.MINIMAL_ID ;i--){
            if(!entities.containsKey(i))
                return i;
        }
        return 0;
    }

    /**
     * Retourne un id temporaire
     * Faire tres attention a bien libere les id apres usage!
     * @return id temporaire
     * 0 si aucun id n est libre
     */
    public int getNextFreeId(){
        int startIndex = IdType.TEMPORARY_OBJECT.MAXIMAL_ID-this.id*1000;
        for(int i = startIndex ; i > startIndex-1000 && i >= IdType.TEMPORARY_OBJECT.MINIMAL_ID ;i--){
            if(!temporaryId.contains((Integer)i)) {
                temporaryId.add((Integer) i);
                return i;
            }
        }
        return 0;
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

		for(Action A : this.getEndFightAction().get(type1))if(A.getId() == type2)
			this.getEndFightAction().get(type1).remove(A);
	}

    public void refreshSpawns() {
        for(int id : this.getMobGroups().keySet())
            send("GM|-" + id);

        this.getMobGroups().clear();
        this.getMobGroups().putAll(this.getFixMobGroups());

        for(MobGroup mg : this.getFixMobGroups().values())
            SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, mg);

        this.spawnGroup(Alignement.BRAKMARIEN,1,-1);
        this.spawnGroup(Alignement.BONTARIEN, 1, -1);
        this.spawnGroup(Alignement.NEUTRE, -1);
    }

    public void spawnGroup(Alignement alignement,int cell){
        spawnGroup(alignement, this.getMaxGroup() - get(MobGroup.class, IdType.MONSTER_GROUP).size() + 1, cell);
    }

	public void spawnGroup(Alignement alignement,int quantity, int cell) {
		for(int i = 0; i < quantity; i++) {
            int nextId = getNextFreeId(IdType.MONSTER_GROUP);
            MobGroup mobGroup = new MobGroup(nextId, alignement, this.getMobPossibles(), this, cases.get(cell), this.getMaxSize());
            if (!mobGroup.getMobsGrade().isEmpty()) {
                addEntity(mobGroup);
            }
        }
	}

	public void spawnNewGroup(boolean timer, Case cell, String data, String condition) {
        int nextFreeId = getNextFreeId(IdType.MONSTER_GROUP);
		MobGroup group = new MobGroup(nextFreeId,this,cell, data,condition,false);
		if(group.getMobsGrade().isEmpty())
			return;
		addEntity(group);
		if(timer)
			group.startTimer();
	}

	public void spawnGroupOnCommand(int cell, String data) {
        int nextFreeId = getNextFreeId(IdType.MONSTER_GROUP);
		MobGroup group = new MobGroup(nextFreeId,this,cases.get(cell),data,false);
		if(group.getMobsGrade().isEmpty())
			return;
		group.startTimer();
        addEntity(group);
	}

	public void addStaticGroup(int cell, String data) {
        int nextFreeId = getNextFreeId(IdType.MONSTER_GROUP);
		MobGroup group = new MobGroup(nextFreeId, this,cases.get(cell), data);
		if(group.getMobsGrade().isEmpty())
			return;
		addEntity(group);
	}

    /**public Fight newFight(Player init1, Player init2, int type, boolean protect) {
     int id = 1;

     if(!this.getFights().isEmpty())
     id = ((Integer) (this.getFights().keySet().toArray()[this.getFights().size() - 1])) + 1;
     ArrayList<Fightable> initiateur1 = new ArrayList<>();
     ArrayList<Fightable> initiateur2 = new ArrayList<>();
     initiateur1.add(init1);
     initiateur2.add(init2);
     Fight fight = new Fight(type, id, this,initiateur1,initiateur2);
     this.getFights().put(id, fight);
     SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
     return fight;
     }*/

	/**public void startFightVersusProtectors(Player player, MobGroup group) {
		if(!player.isCanAggro())
			return;

		int id = 1;

		if(!this.getFights().isEmpty())
			id = ((Integer) (this.getFights().keySet().toArray()[this.getFights().size() - 1])) + 1;

        ArrayList<Fightable> initiateur1 = new ArrayList<>();
        ArrayList<Fightable> initiateur2 = new ArrayList<>();
        initiateur1.add(player);
        int count = IdType.MONSTER.MAXIMAL_ID;
        for(MobGrade mobGrade : group.getMobsGrade().values()){
            initiateur2.add(new Mob(count,cases.get(getRandomFreeCell()),mobGrade));
            count--;
        }

        this.getFights().put(id, new Fight(4,id, this, initiateur1, initiateur2));
		SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
	}*/

	public int getRandomFreeCell() {
        ArrayList<Case> cells = new ArrayList<>(this.getCases().values());
		Case freecell;
        int security=0;
        do {
            freecell = cells.get((int)random.nextInt(cells.size()));
            security++;
            if(security==100)break;
        } while (freecell != null || !freecell.isFree() || freecell.isWalkable(true) );
		return freecell.getId();
	}

	public synchronized void onPlayerArriveOnCell(Player player, int cell) {
		if(this.getCases().get(cell) == null)
			return;
		
		Object obj = this.getCases().get(cell).getObject();
		
		if(obj != null) {
			this.getCases().get(cell).setObject(null);
			if(player.addObject(obj, true))
				World.data.addObject(obj, true);
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
				if((group.getAlignement() == Alignement.NEUTRE || ((player.getAlignement() == Alignement.BONTARIEN || player.getAlignement() == Alignement.BRAKMARIEN) && (player.getAlignement() != group.getAlignement()))) && ConditionParser.validConditions(player, group.getCondition())) {
					Log.addToLog(" > Le joueur " + player.getName() + " rentre en combat contre un groupe de monstre (" + group.getId() + ") !");
					startFigthVersusMonstres(player,group);
					return;
				}
			}
		}
	}
	
	public void startFigthVersusMonstres(Player player, MobGroup group) {
        int id = getNextFreeId();
		if(!group.isFix())
            removeEntity(group);
        removeEntity(player);
        Fight fight = new PVMFight(id, this, player, group);
        Couple<Team,Team> teams = fight.getTeams();
        Couple<Flag,Flag> teamFlags = new Couple<>(teams.getKey().getFlag(),teams.getValue().getFlag());
        synchronized (flagLocker){
            addFight(fight);
            this.flags.put(fight,teamFlags);
        }
        this.send("Gc+" + id + ";" + Fight.FightType.PVM.id + "|"
                + teamFlags.getKey().getGc() + "|" + teamFlags.getValue().getGc());
	}
	
	/*public void startFigthVersusPercepteur(Player player, Collector collector) {
		int id = 1;
		
		if(!this.getFights().isEmpty())
			id = ((Integer)(this.getFights().keySet().toArray()[this.getFights().size() - 1])) + 1;
        ArrayList<Fightable> initiateur1 = new ArrayList<>();
        ArrayList<Fightable> initiateur2 = new ArrayList<>();
        initiateur1.add(player);
        initiateur2.add(collector);
		this.getFights().put(id, new Fight(5,id, this, initiateur1, initiateur2));
		SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
	}*/

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
				SocketManager.GAME_SEND_GDO_PACKET(player, '+', cell.getId(), cell.getObject().getTemplate().getId(), 0);
	}

	public int getStoreCount() {
		return (World.data.getSeller(this) == null ? 0 : World.data.getSeller(this).size());
	}
	
	public String getGMsPackets() {
		StringBuilder packet = new StringBuilder("GM");
		for(Player player : getPlayers()){
            packet.append("|+").append(player.getHelper().getGmPacket());
        }
        packet.append('\u0000');
		return packet.toString();
	}
	
	public String getFightersGMsPackets() {
		StringBuilder packet = new StringBuilder();
		for(Entry<Integer, Case> cell : this.getCases().entrySet()) {
            for (Entry<Integer, Fighter> f : cell.getValue().getFighters().entrySet()) {
                packet.append(f.getValue().getGmPacket('+')).append('\u0000');
            }
        }
            return packet.toString();
	}
	
	public String getMobGroupGMsPackets() {
		if(this.getMobGroups().isEmpty())
			return "";
		
		StringBuilder packet = new StringBuilder().append("GM");

		for(MobGroup entry : this.getMobGroups().values()) {
			String GM = entry.getHelper().getGmPacket();
			if(GM.isEmpty())
				continue;
			packet.append("|+").append(GM);
		}
		return packet.toString();
	}
	
	public String getNpcsGMsPackets() {
		StringBuilder str = new StringBuilder("GM");
        for(Npc npc : get(Npc.class, IdType.PNJ)){
            str.append("|+").append(npc.getHelper().getGmPacket());
        }
        if(str.toString().equals("GM"))
            return "";
        str.append((char)0x00);
        return str.toString();
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

    public void onFightStart(Fight fight){
        removeFlags(fight);
        addFight(fight);
        refreshFightOnMap();
        GlobalThread.registerAction(new ExecuteOnceAction(Server.mobRespawnDelay) {
            @Override
            public void applyAction() {
                spawnGroup(Alignement.NEUTRE,getRandomFreeCell());
            }
        });
    }

    public void onFightEnd(Fight fight){
        removeFight(fight);
        refreshFightOnMap();
    }

    /**
     * Renvoie les cellules libres les plus proches
     * @param cell cellucle centrale
     * @param maxDistance portee maximale
     * @return cellule libre
     */
    public Case getRandomNearFreeCell(Case cell,int maxDistance){
        return getRandomNearFreeCell(cell,0,maxDistance);
    }

    /**
     * Renvoie les cellules libres les plus proches
     * @param cell cellucle centrale
     * @param minDistance portee minimale
     * @param maxDistance portee maximale
     * @return cellule libre
     */
    public Case getRandomNearFreeCell(Case cell,int minDistance,int maxDistance){
        for(int i=0; i <= maxDistance;i++){
            Case cell1 = cases.get(cell.getId()+minDistance + i);
            Case cell2 = cases.get(cell.getId()-minDistance-i);
            if(cell1 != null && cell1.isFree()) return cell1;
            if(cell2 != null && cell2.isFree()) return cell2;
        }
        return null;
    }

    /**
     * Envoie un message a tout les player sur la map n etant pas en combat
     * @param str message a envoyer
     */
    public void send(String str) {
        for(Entity entity : this.entities.values()) {
            entity.send(str);
        }
    }

    private void refreshFightOnMap(){
        this.send(getFightCountPacket());
    }

    private void addFight(Fight f){
        if(this.fights == null){
            this.fights = new HashMap<>();
        }
        this.fights.put(f.getId(),f);
    }

    private void removeFlags(Fight f){
        this.send("Gc-" + f.getId());
        Couple<Flag,Flag> flagPair = this.flags.get(f);
        flagPair.getKey().setWorking(false);
        this.temporaryId.remove((Integer)flagPair.getKey().getId());
        flagPair.getValue().setWorking(false);
        this.temporaryId.remove((Integer)flagPair.getValue().getId());
        synchronized (flagLocker){
            this.flags.remove(f);
        }
    }

    private void removeFight(Fight f){
        if(this.fights != null && this.fights.containsKey(f.getId())){
            this.fights.remove(f.getId());
            if(this.fights.isEmpty())
                this.fights = null;
        }
    }

    //region Initailisation
    private Map<Integer,Case> decompileCells(String cellsData){
        Map<Integer,Case> cells = new TreeMap<>();
        String[] data = cellsData.split("\\|");
        for(String cellData : data) {
            boolean walkable = true, lineOfSight = true;
            int num = -1, obj = -1;
            String[] cellInfos = cellData.split(",");
            try	{
                walkable = cellInfos[2].equals("1");
                lineOfSight = cellInfos[1].equals("1");
                num = Integer.parseInt(cellInfos[0]);
                if(!cellInfos[3].trim().equals(""))
                {
                    obj = Integer.parseInt(cellInfos[3]);
                }
            } catch(Exception ignored) {}
            if(num == -1)
                continue;

            cells.put(num, new Case(this, num, walkable, lineOfSight, obj));
        }
        return cells;
    }

    private Map<Integer, Case> decompileMapData(String dData){
        Map<Integer, Case> cells = new TreeMap<>();
        for (int f = 0; f < dData.length(); f += 10)
        {
            String CellData = dData.substring(f, f+10);
            List<Byte> CellInfo = new ArrayList<>();
            for (char i : CellData.toCharArray())
                CellInfo.add((byte)CryptManager.getIntByHashedValue(i));
            int Type = (CellInfo.get(2) & 56) >> 3;
            boolean IsSightBlocker = (CellInfo.get(0) & 1) != 0;
            int layerObject2 = ((CellInfo.get(0) & 2) << 12) + ((CellInfo.get(7) & 1) << 12) + (CellInfo.get(8) << 6) + CellInfo.get(9);
            boolean layerObject2Interactive = ((CellInfo.get(7) & 2) >> 1) != 0;
            int obj = (layerObject2Interactive?layerObject2:-1);
            cells.put(f/10,new Case(this, f/10, Type!=0, IsSightBlocker, obj));
        }
        return cells;
    }

    private ArrayList<MobGrade> decompileMobPossibility(String monsters){
        ArrayList<MobGrade> mobs = new ArrayList<>();
        int monsterId,lvl;
        for(String mob : monsters.split("\\|")) {
            if(mob.equals(""))
                continue;

            try	{
                monsterId = Integer.parseInt(mob.split(",")[0]);
                lvl = Integer.parseInt(mob.split(",")[1]);
            } catch(NumberFormatException e) {
                continue;
            }

            if(monsterId == 0 || lvl == 0)
                continue;
            if(World.data.getMonstre(monsterId) == null)
                continue;
            if(World.data.getMonstre(monsterId).getGradeByLevel(lvl) == null)
                continue;

            mobs.add(World.data.getMonstre(monsterId).getGradeByLevel(lvl));
        }
        return mobs;
    }
    //endregion

    //region Packets
    /**
     * Retourne le packet GM avec l'ensemble des personnes sur la map
     * @return packet gm
     */
    public String getGmMessage(){
        StringBuilder str = new StringBuilder();
        for(Entity entity : entities.values()){
            String gm = entity.getHelper().getGmPacket();
            if(!gm.isEmpty()) {
                str.append("GM|+").append(gm).append((char)0x00);
            }
        }
        return str.toString();
    }

    public String getFlagsPackets(){
        StringBuilder gc = new StringBuilder();
        StringBuilder gt = new StringBuilder();
        synchronized (flagLocker){
            for(Entry<Fight,Couple<Flag,Flag>> entry : flags.entrySet()){
                gc.append("Gc+").append(entry.getKey().getId())
                        .append(";").append(entry.getKey().getFightType().id).append("|")
                        .append(entry.getValue().getKey().getGc()).append("|")
                        .append(entry.getValue().getValue().getGc()).append("|");
                gt.append(entry.getValue().getKey().getGt()).append((char)0x00)
                        .append(entry.getValue().getValue().getGt()).append((char)0x00);
            }
            return gc.toString() + (char)0x00 + gt.toString();
        }
    }

    public String getFightCountPacket(){
        if(this.fights == null) return "fC0";
        else return "fC" + fights.size();
    }

    public String getGDFPacket(){
        return this.GDFPacket;
    }

    /**
     * permet de creer le message demandant l affichage de la fenetre "chargement"
     * lors d un changement de map (le fait d etre affiche ou non depend de si cette carte a deja ete chargee
     * par le client)
     * @return message de chargement
     */
    private String generateLoadingMessage(){
        return this.loadingMapMessage;
    }

    /**
     * Renvoie la description d une carte pour qu elle puisse etre chargee par le client
     * @return description
     */
    private String mapDescriptionMessage(){
        return this.descriptionMapMessage;
    }

    /**
     * Permet de decharger graphiquement l entitee correspondante par le client
     * @param entity entite a faire disparaitre
     * @return message supprimant le visuel de l entite
     */
    private String unloadCharacterMessage(Entity entity){
        return "GM|-" + entity.getId();
    }

    /**
     * Permet de charger graphiquement l entitee sur la map par le client
     * @param entity entitee a charger
     * @return message pour l afficher sur le client
     */
    private String loadCharacterMessage(Entity entity){
        return "GM|+" + entity.getHelper().getGmPacket();
    }

    private String generateGDFPacket(){
        StringBuilder packet = new StringBuilder("GDF|");
        for(Case cell : this.cases.values()){
            packet.append(cell.getId()).append(";")
                    .append(Constants.IOBJECT_STATE_EMPTY2).append(";")//FIXME
                    .append(1).append("|");//FIXME
        }
        return packet.toString();
    }
    //endregion
}
