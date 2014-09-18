package org.ancestra.evolutive.fight;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.creature.Creature;
import org.ancestra.evolutive.entity.creature.Fightable;
import org.ancestra.evolutive.entity.creature.Helper;
import org.ancestra.evolutive.entity.creature.collector.Collector;
import org.ancestra.evolutive.entity.creature.monster.Mob;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.fight.spell.LaunchedSpell;
import org.ancestra.evolutive.fight.spell.SpellEffect;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.fight.team.Team;
import org.ancestra.evolutive.map.Case;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Fighter extends Creature{


    public enum FighterType {
        PLAYER(1),
        CREATURE(2),
        COLLECTOR(5),
        CLONE(10);
        int value;
        private FighterType(int value) {
            this.value = value;
        }
    }

    private final Fightable fightable;
    private int PA;
    private int PM;
    private int maxHealthPoint;
    private int currentHealthPoint;


    private boolean _canPlay = false;
	private Case fakeCell; //cell before spell cast (hide mode)
	private CopyOnWriteArrayList<SpellEffect> fightBuffs = new CopyOnWriteArrayList<>();
	private Map<Integer,Integer> _chatiValue = new TreeMap<Integer,Integer>();
    private Fighter _invocator;
	public int _nbInvoc = 0;
	private int _PDVMAX;
	private int pdv;
	private boolean hasLeft;
	private Map<Integer,Integer> _state = new TreeMap<Integer,Integer>();
	private Fighter _isHolding;
	private Fighter _holdedBy;
	private CopyOnWriteArrayList<LaunchedSpell> launchedSpells = new CopyOnWriteArrayList<>();
	private Fighter _oldCible = null;
	private int turnRemaining = -1;
    private final Team team;
	
	public Fighter get_oldCible() {
		return _oldCible;
	}
	public void set_oldCible(Fighter cible) {
		_oldCible = cible;
	}

    public Fighter(Fight f,Fightable fightable,Case cell,Team team){
        super(fightable.getId(), fightable.getName(),cell.getMap(),cell);
        this.fightable = fightable;
        this.fight = f;
        fightable.setFight(f);
        this._PDVMAX = fightable.getMaxPdv();
        this.pdv = fightable.getPdv();
        fightable.removeOnMap();
        setFightCell(cell);
        fightable.setFighter(this);
        this.team = team;
        this.PA = getDefaultPA();
        this.PM = getDefautPM();
    }

    public Fighter(Fight f,Fightable fightable,Team team){
        this(f, fightable, fightable.getCell(),team);
    }


    //region Refactored
    //region Getter and Setters
    @Override
    public void setPosition(Case cell){
        this.cell.removeCreature(this);
        this.cell.removeFighter(this);
        this.cell = cell;
        cell.addCreature(this);
        cell.addFighter(this);
        if(!this.isHide())
            this.fakeCell = cell;
    }

    @Override
    public void send(String message){
        fightable.send(message);
    }

    @Override
    public Helper getHelper(){
        return fightable.getHelper();
    }

    public int getInitiative(){
        return fightable.getInitiative();
    }

    public Map<Integer, SpellStats> getSpells(){
        return fightable.getSpells();
    }

    public boolean isReady(){
        return fightable.isReady();
    }

    public FighterType getFighterType(){
        return fightable.getFighterType();
    }

    public int getIa(){
        return fightable.getIa();
    }

    public void removePM(int toRemove){
        removePM(toRemove,this);
    }

    public void removePM(int toRemove,Fighter caster){
        final String message = "GA;129;" + caster.getId() + ";" + this.getId() + ",-" + toRemove;
        this.PM -= toRemove;
        if(!this.isHide())
            this.fight.send(message);
        else
            this.send(message);
    }

    public void addPM(int toAdd){
        addPM(toAdd,this);
    }

    public void addPM(int toAdd,Fighter caster){
        final String message = "GA;129;" + caster.getId() + ";" + this.getId() + ",+" + toAdd;
        this.PM += toAdd;
        if(!this.isHide())
            this.fight.send(message);
        else
            this.send(message);
    }

    public void removePA(int toRemove){
        removePA(toRemove,this);
    }

    public void removePA(int toRemove,Fighter caster){
        final String message = "GA;102;" + caster.getId() + ";" + this.getId() + ",-" + toRemove;
        this.PA -= toRemove;
        if(!this.isHide())
            this.fight.send(message);
        else
            this.send(message);
    }

    public void addPA(int toAdd){
        addPA(toAdd,this);
    }

    public void addPA(int toAdd,Fighter caster){
        final String message = "GA;102;" + caster.getId() + ";" + this.getId() + ",+" + toAdd;
        this.PA += toAdd;
        if(!this.isHide())
            this.fight.send(message);
        else
            this.send(message);
    }

    public void addPDV(int toAdd){
        addPDV(toAdd,this);
    }

    public void addPDV(int toAdd,Fighter caster){
        final String message = "GA;100;" + caster.getId() + ";" + this.getId() + ",+" + toAdd;
        this.pdv = this.pdv+toAdd>_PDVMAX?_PDVMAX:pdv+toAdd;
        if(!this.isHide())
            this.fight.send(message);
        else
            this.send(message);
    }

    public void removePDV(int withdraw){
        removePDV(withdraw,this);
    }

    public void removePDV(int withdraw,Fighter caster){
        this.pdv -= withdraw;
        final String message = "GA;108;" + this.getId() + ";" + caster.getId()+ ",-" + withdraw;
        if(!this.isHide())
            this.getFight().send(message);
        else
            this.send(message);
        if(this.pdv <= 0){
            this.pdv = 0;
            this.cell.removeCreature(this);
            this.cell.removeFighter(this);
            this.fight.onFighterDie(this);
        }

    }

    public int getPA(){
        return this.PA;
    }

    public int getPM(){
        return this.PM;
    }

    public int getGfx() {
        return fightable.getGFX();
    }

    public Fightable getFightable(){
        return fightable;
    }

    public Case getVisibleCell(){
        return fakeCell;
    }

	public CopyOnWriteArrayList<LaunchedSpell> getLaunchedSorts(){
		return launchedSpells;
	}

    public Team getTeam() {
        return team;
    }
	
	public void actualiseLaunchedSort(){
		for(LaunchedSpell S : launchedSpells){
			S.refreshCooldown();
			if(S.getCooldown() <= 0){
				launchedSpells.remove(S);
			}
		}
	}

    public void onStartTurn(Fighter fighter1){
        fightable.onStartTurn(fighter1);
        if(fighter1 == this){
            this._canPlay = true;
        }
        if(this.hasLeft){
            if(this.turnRemaining == 0){
            }
            this.turnRemaining--;
        }
    }

    public void onEndTurn(){
        this.PA = getDefaultPA();
        this.PM = getDefautPM();
    }
	
	public void addLaunchedSort(Fighter target,SpellStats sort){
		launchedSpells.add(new LaunchedSpell(target,sort));
	}

	public Fighter get_isHolding() {
		return _isHolding;
	}

	public void set_isHolding(Fighter isHolding) {
		_isHolding = isHolding;
	}

	public Fighter get_holdedBy() {
		return _holdedBy;
	}

	public void set_holdedBy(Fighter holdedBy) {
		_holdedBy = holdedBy;
	}

	public CopyOnWriteArrayList<SpellEffect> getFightBuff(){
		return fightBuffs;
	}

    public void setFightCell(Case cell)
	{
		setPosition(cell);
	}

    public Case get_fightCell(boolean beforeLaunchedSpell) {
        if(isHide() && beforeLaunchedSpell && fakeCell != null)
            return this.fakeCell;
        return this.cell;
    }

    public boolean isHide(){
		return hasBuff(150);
	}

	public boolean isDead() {
		return this.pdv <= 0 || hasLeft;
	}

	public boolean hasLeft() {
		return hasLeft;
	}

	public void setLeft(boolean hasLeft) {
		this.hasLeft = hasLeft;
	}

	public Player getPersonnage(){
		if(fightable.getFighterType() == FighterType.PLAYER)
			return (Player) fightable;
		return null;
	}
	
	public Collector getPerco()
	{
		if(fightable.getFighterType() == FighterType.COLLECTOR)
			return (Collector) fightable;
		return null;
	}
	public boolean testIfCC(int tauxCC)
	{
		if(tauxCC < 2)return false;
		int agi = getTotalStats().getEffect(Constants.STATS_ADD_AGIL);
		if(agi <0)agi =0;
		tauxCC -= getTotalStats().getEffect(Constants.STATS_ADD_CC);
		tauxCC = (int)((tauxCC * 2.9901) / Math.log(agi +12));//Influence de l'agi
		if(tauxCC<2)tauxCC = 2;
		int jet = Formulas.getRandomValue(1, tauxCC);
		return (jet == tauxCC);
	}
	
	public Stats getTotalStats(){
		Stats stats = fightable.getStats();
		stats = Stats.cumulStat(stats,getFightBuffStats());
		return stats;
	}
	
	
	public void initBuffStats(){
		if(fightable.getFighterType() == FighterType.PLAYER){
			for(Map.Entry<Integer, SpellEffect> entry : ((Player) fightable).getBuffs().entrySet()){
				fightBuffs.add(entry.getValue());
			}
		}
	}
	
	private Stats getFightBuffStats()
	{
		Stats stats = new Stats();
		for(SpellEffect entry : fightBuffs)
		{
			stats.addOneStat(entry.getEffectID(), entry.getValue());
		}
		return stats;
	}
	
	public String getGmPacket(char c)
	{
		StringBuilder str = new StringBuilder();
		str.append("GM|").append(c);
		str.append(fightable.getHelper().getGmPacket());
		return str.toString();
	}
	
	public void setState(int id, int t)
	{
		_state.remove(id);
		if(t != 0)
		_state.put(id, t);
	}
	
	public boolean isState(int id)
	{
		if(_state.get(id) == null)return false;
		return _state.get(id) != 0;
	}
	
	public Map<Integer, Integer> getStates() {
		return _state;
	}
	
	public void decrementStates()
	{
		//Copie pour �vident les modif concurrentes
		ArrayList<Entry<Integer,Integer>> entries = new ArrayList<Entry<Integer, Integer>>();
		entries.addAll(_state.entrySet());
		for(Entry<Integer,Integer> e : entries)
		{
			//Si la valeur est n�gative, on y touche pas
			if(e.getKey() < 0)continue;
			
			_state.remove(e.getKey());
			int nVal = e.getValue()-1;
			//Si 0 on ne remet pas la valeur dans le tableau
			if(nVal == 0)//ne pas mettre plus petit, -1 = infinie
			{
				//on envoie au client la desactivation de l'�tat
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, getId()+"", getId()+","+e.getKey()+",0");
				continue;
			}
			//Sinon on remet avec la nouvelle valeur
			_state.put(e.getKey(), nVal);
		}
	}
	
	public int getPDV() {
		int pdv = this.pdv + getBuffValue(Constants.STATS_ADD_VITA);
		return pdv;
	}


	
	public void applyBeginningTurnBuff(Fight fight) {
        for(int effectID : Constants.BEGIN_TURN_BUFF){
            ArrayList<SpellEffect> buffs = new ArrayList<>();
            buffs.addAll(fightBuffs);
            for(SpellEffect entry : buffs){
                if(entry.getEffectID() == effectID){
                    logger.trace("Effet de debut de tour : " + effectID);
                    entry.applyBeginingBuff(fight, this);
                }
            }
        }
	}

	public SpellEffect getBuff(int id)
	{
		for(SpellEffect entry : fightBuffs)
		{
			if(entry.getEffectID() == id && entry.getDuration() >0)
			{
				return entry;
			}
		}
		return null;
	}
	
	public boolean hasBuff(int id)
	{
		for(SpellEffect entry : fightBuffs)
		{
			if(entry.getEffectID() == id && entry.getDuration() >0)
			{
				return true;
			}
		}
		return false;
	}
	
	public int getBuffValue(int id)
	{
		int value = 0;
		for(SpellEffect entry : fightBuffs)
		{
			if(entry.getEffectID() == id)
				value += entry.getValue();
		}
		return value;
	}
	
	public int getMaitriseDmg(int id)
	{
		int value = 0;
		for(SpellEffect entry : fightBuffs)
		{
			if(entry.getSpell() == id)
				value += entry.getValue();
		}
		return value;
	}

	
	public boolean getSpellValueBool(int id)
	{
		for(SpellEffect entry : fightBuffs)
		{
			if(entry.getSpell() == id)
				return true;
		}
		return false;
	}

	public void refreshfightBuff()
	{
		//Copie pour contrer les modifications Concurentes
		ArrayList<SpellEffect> b = new ArrayList<SpellEffect>();
		for(SpellEffect entry : fightBuffs)
		{
			if(entry.decrementDuration() != 0)//Si pas fin du buff
			{
				b.add(entry);
			}else
			{
				if(Server.config.isDebug()) Log.addToLog("Suppression du buff "+entry.getEffectID()+" sur le joueur Fighter ID= "+ getId());
				switch(entry.getEffectID())
				{
					case 108:
						if(entry.getSpell() == 441)
						{
							//Baisse des pdvs max
							_PDVMAX = (_PDVMAX-entry.getValue());
							
							removePDV(entry.getValue());
						}
					break;
				
					case 150://Invisibilit�
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 150, entry.getCaster().getId()+"", getId()+",0");
					break;
					
					case 950:
						String args = entry.getArgs();
						int id = -1;
						try
						{
							id = Integer.parseInt(args.split(";")[2]);
						}catch(Exception e){}
						if(id == -1)return;
						setState(id,0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, entry.getCaster().getId() + "", entry.getCaster().getId() + "," + id + ",0");
					break;
				}
			}
		}
		fightBuffs.clear();
		fightBuffs.addAll(b);
	}
	
	public void addBuff(int id,int val,int duration,int turns,boolean debuff,int spellID,String args,Fighter caster)
	{
		if(spellID == 99 || 
		   spellID == 5 || 
		   spellID == 20 || 
		   spellID == 127 ||
		   spellID == 89 ||
		   spellID == 126 ||
		   spellID == 115 ||
		   spellID == 192 ||
		   spellID == 4 ||
		   spellID == 1 ||
		   spellID == 6 ||
		   spellID == 14 ||
		   spellID == 18 ||
		   spellID == 7 ||
		   spellID == 284 ||
		   spellID == 197 ||
		   spellID == 704
		   )
		{
			//Tr�ve
			//Immu
			//Pr�vention
			//Momification
			//D�vouement
			//Mot stimulant
			//Odorat
			//Ronce Apaisante
			//Renvoi de sort
			//Armure Incandescente
			//Armure Terrestre
			//Armure Venteuse
			//Armure Aqueuse
			//Bouclier F�ca
			//Acc�l�ration Poupesque
			//Puissance Sylvestre
			//Pandanlku
			debuff = true;
		}
		//Si c'est le jouer actif qui s'autoBuff, on ajoute 1 a la dur�e
		fightBuffs.add(new SpellEffect(id, val, (_canPlay ? duration + 1 : duration), turns, debuff, caster, args, spellID));
		if(Server.config.isDebug()) Log.addToLog("Ajout du Buff "+id+" sur le personnage Fighter ID = "+this.getId()+" val : "+val+" duration : "+duration+" turns : "+turns+" debuff : "+debuff+" spellid : "+spellID+" args : "+args);
		
			
		switch(id)
		{
			case 6://Renvoie de sort
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(fight, 7, id, getId(), -1, val+"", "10", "", duration, spellID);
			break;
			
			case 79://Chance �ca
				val = Integer.parseInt(args.split(";")[0]);
				String valMax = args.split(";")[1];
				String chance = args.split(";")[2];
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(fight, 7, id, getId(), val, valMax, chance, "", duration, spellID);
			break;
			
			case 788://Fait apparaitre message le temps de buff sacri Chatiment de X sur Y tours
				val = Integer.parseInt(args.split(";")[1]);
				String valMax2 = args.split(";")[2];
				if(Integer.parseInt(args.split(";")[0]) == 108)return;
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(fight, 7, id, getId(), val, ""+val, ""+valMax2, "", duration, spellID);
				
			break;

			case 98://Poison insidieux
			case 107://Mot d'�pine (2�3), Contre(3)
			case 100://Fl�che Empoisonn�e, Tout ou rien
			case 108://Mot de R�g�n�ration, Tout ou rien
			case 165://Ma�trises
				val = Integer.parseInt(args.split(";")[0]);
				String valMax1 = args.split(";")[1];
				if(valMax1.compareTo("-1") == 0 || spellID == 82 || spellID == 94)
				{
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(fight, 7, id, getId(), val, "", "", "", duration, spellID);
				}else if(valMax1.compareTo("-1") != 0)
				{
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(fight, 7, id, getId(), val, valMax1, "", "", duration, spellID);
				}
				break;

			default:
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(fight, 7, id, getId(), val, "", "", "", duration, spellID);
			break;
		}
	}

	public int getPDVMAX()
	{
		return _PDVMAX + getBuffValue(Constants.STATS_ADD_VITA);
	}
	
	public int getLvl() {
		return fightable.getLevel();
	}

	public String xpString(String str){
		if(fightable.getFighterType() == FighterType.PLAYER){
			int max = fightable.getLevel()+1;
			if(max>World.data.getExpLevelSize())max = World.data.getExpLevelSize();
			return World.data.getExpLevel(fightable.getLevel()).player+str+((Player) fightable).getExperience()+str+World.data.getExpLevel(max).player;
		}
		return "0"+str+"0"+str+"0";
	}

	public Mob getMob(){
		if(fightable.getFighterType() == FighterType.CREATURE)
			return (Mob) fightable;
		
		return null;
	}


	public boolean canPlay()
	{
		return _canPlay;
	}

	public void setCanPlay(boolean b)
	{
		_canPlay = b;
	}

	public ArrayList<SpellEffect> getBuffsByEffectID(int effectID){
		ArrayList<SpellEffect> buffs = new ArrayList<>();
		for(SpellEffect buff : fightBuffs)
		{
			if(buff.getEffectID() == effectID)
				buffs.add(buff);
		}
		return buffs;
	}

	public Stats getTotalStatsLessBuff(){
		return fightable.getStats();
	}

	public int getDefaultPA() {
		return fightable.getStats().getEffect(Constants.STATS_ADD_PA) + getBuffValue(Constants.STATS_ADD_PA) - getBuffValue(Constants.STATS_REM_PA);
	}

	public int getDefautPM() {
		return fightable.getStats().getEffect(Constants.STATS_ADD_PM) + getBuffValue(Constants.STATS_ADD_PM) -  getBuffValue(Constants.STATS_REM_PM);
	}

	public int getCurPA(Fight fight)
	{
		return fight.get_curFighterPA();
	}
	
	public int getCurPM(Fight fight)
	{
		return fight.get_curFighterPM();
	}
	
	public void setCurPM(Fight fight, int pm){
		fight.set_curFighterPM(pm);
	}
	
	public void setCurPA(Fight fight, int pa)
	{
		fight.set_curFighterPA(pa);
	}
	
	public void setInvocator(Fighter caster)
	{
		_invocator = caster;
	}
	
	public Fighter getInvocator()
	{
		return _invocator;
	}
	
	public boolean isInvocation()
	{
		return (_invocator!=null);
	}
	
	public boolean isPerco()
	{
		return (fightable.getFighterType()==FighterType.COLLECTOR);
	}

	public void debuff()
	{
		ArrayList<SpellEffect> newBuffs = new ArrayList<SpellEffect>();
		//on v�rifie chaque buff en cours, si pas d�buffable, on l'ajout a la nouvelle liste
		for(SpellEffect SE : fightBuffs)
		{
			if(!SE.isDebuffabe())newBuffs.add(SE);
			//On envoie les Packets si besoin
			switch(SE.getEffectID())
			{
				case Constants.STATS_ADD_PA:
				case Constants.STATS_ADD_PA2:
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 101, getId()+"", getId()+",-"+SE.getValue());
				break;
				
				case Constants.STATS_ADD_PM:
				case Constants.STATS_ADD_PM2:
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 127, getId()+"", getId()+",-"+SE.getValue());
				break;
			}
		}
		fightBuffs.clear();
		fightBuffs.addAll(newBuffs);
		if(fightable.getFighterType() == FighterType.PLAYER && !hasLeft)
			SocketManager.GAME_SEND_STATS_PACKET((Player) fightable);
	}

	public void fullPDV()
	{
		pdv = _PDVMAX;
	}

	public void unHide(int spellid)
	{
		logger.debug("Invisibilty has been retired");
		if(spellid != -1)// -1 : CAC
		{
			switch(spellid) 
			{
                //Attaque ne retirant pas l'invisibilité
			case 66: 
			case 71:
			case 181: 
			case 196: 
			case 200: 
			case 219: 
			return; 
			}
		}
		ArrayList<SpellEffect> buffs = new ArrayList<SpellEffect>();
		buffs.addAll(getFightBuff());
		for(SpellEffect SE : buffs)
		{
			if(SE.getEffectID() == 150)
				getFightBuff().remove(SE);
		}
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 150, getId()+"", getId()+",0");
		//On actualise la position
		SocketManager.GAME_SEND_GIC_PACKET_TO_FIGHT(fight, 7,this);
	}

	public Map<Integer, Integer> get_chatiValue() {
		return _chatiValue;
	}

	public void addPDVMAX(int max) {
		_PDVMAX = (_PDVMAX+max);
		pdv = (pdv +max);
	}
    
	public int getTurnRemainingBeforeExpulsion() {
		return turnRemaining;
	}
	
	public void decreaseTurnRemaining() {
		this.turnRemaining--;
	}
	
	public void setTurnRemaining(int turnRemaining) {
		this.turnRemaining = turnRemaining;
	}



}