package org.ancestra.evolutive.fight;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.collector.Collector;
import org.ancestra.evolutive.entity.monster.MobGrade;
import org.ancestra.evolutive.fight.spell.LaunchedSpell;
import org.ancestra.evolutive.fight.spell.SpellEffect;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.map.Case;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Fighter
{
    protected Logger logger;

	int _id = 0;
	private boolean _canPlay = false;
	private Fight _fight;
	private int _type = 0; // 1 : Personnage, 2 : Mob, 5 : Perco
	private MobGrade _mob = null;
	private Player _perso = null;
	Collector _Perco = null;
	Player _double = null;
	private int _team = -2;
	private Case _cell;
	private Case fakeCell; //cell before spell cast (hide mode)
	private ArrayList<SpellEffect> _fightBuffs = new ArrayList<SpellEffect>();
	private Map<Integer,Integer> _chatiValue = new TreeMap<Integer,Integer>();
	private int _orientation; 
	private Fighter _invocator;
	public int _nbInvoc = 0;
	private int _PDVMAX;
	private int _PDV;
	private boolean _isDead;
	private boolean _hasLeft;
	private int _gfxID;
	private Map<Integer,Integer> _state = new TreeMap<Integer,Integer>();
	private Fighter _isHolding;
	private Fighter _holdedBy;
	private ArrayList<LaunchedSpell> _launchedSort = new ArrayList<LaunchedSpell>();
	private Fighter _oldCible = null;
	
	public Fighter get_oldCible() {
		return _oldCible;
	}
	public void set_oldCible(Fighter cible) {
		_oldCible = cible;
	}

	public Fighter(Fight f, MobGrade mob)
	{
		_fight = f;
		_type = 2;
		_mob = mob;
		_id = mob.getId();
		_PDVMAX = mob.getMaxPdv();
		_PDV = mob.getPdv();
		_gfxID = getDefaultGfx();
        logger = (Logger) LoggerFactory.getLogger("Fight " + f.getMap() + ",Monster ID " + mob.getTemplate().getId());
    }
	
	public Fighter(Fight f, Player perso)
	{
		_fight = f;
		if(perso.isClone())
		{
			_type = 10;
			_double = perso;
		}else
		{
			_type = 1;
			_perso = perso;
		}
		_id = perso.getUUID();
		_PDVMAX = perso.getMaxPdv();
		_PDV = perso.getPdv();
		_gfxID = getDefaultGfx();
        logger = (Logger) LoggerFactory.getLogger("Fight " + f.getMap() + ",Player " + perso.getName());
	}

	public Fighter(Fight f, Collector collector) {
		_fight = f;
		_type = 5;
		_Perco = collector;
		Console.instance.println("Perco:"+collector);
		_id = -1;
		_PDVMAX = (World.data.getGuild(collector.get_guildID()).getLevel()*100);
		_PDV = (World.data.getGuild(collector.get_guildID()).getLevel()*100);
		_gfxID = 6000;
        logger = (Logger) LoggerFactory.getLogger("Fight " + f.getMap() + ",Monster ID " + collector.get_guildID());
	}

	public ArrayList<LaunchedSpell> getLaunchedSorts()
	{
		return _launchedSort;
	}
	
	public void ActualiseLaunchedSort()
	{
		ArrayList<LaunchedSpell> copie = new ArrayList<LaunchedSpell>();
		copie.addAll(_launchedSort);
		int i = 0;
		for(LaunchedSpell S : copie)
		{
			S.refreshCooldown();
			if(S.getCooldown() <= 0)
			{
				_launchedSort.remove(i);
				i--;
			}
			i++;
		}
	}
	
	public void addLaunchedSort(Fighter target,SpellStats sort)
	{
		LaunchedSpell launched = new LaunchedSpell(target,sort);
		_launchedSort.add(launched);
	}
	
	public int getGUID()
	{
		return _id;
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

	public int get_gfxID() {
		return _gfxID;
	}

	public void set_gfxID(int gfxID) {
		_gfxID = gfxID;
	}

	public ArrayList<SpellEffect> get_fightBuff()
	{
		return _fightBuffs;
	}
	public void set_fightCell(Case cell)
	{
		set_cell(cell);
	}
	public boolean isHide()
	{
		return hasBuff(150);
	}
	public Case get_fightCell(boolean beforeLaunchedSpell) {
		if(isHide() && beforeLaunchedSpell && fakeCell != null)
			return this.fakeCell;
		return this._cell;
	}
	public void setTeam(int i)
	{
		_team = i;
	}
	public boolean isDead() {
		return _isDead;
	}

	public void setDead(boolean isDead) {
		_isDead = isDead;
	}

	public boolean hasLeft() {
		return _hasLeft;
	}

	public void setLeft(boolean hasLeft) {
		_hasLeft = hasLeft;
	}

	public Player getPersonnage()
	{
		if(_type == 1)
			return _perso;
		return null;
	}
	
	public Collector getPerco()
	{
		if(_type == 5)
			return _Perco;
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
	
	public Stats getTotalStats()
	{
		Stats stats = new Stats(new TreeMap<Integer,Integer>());
		if(_type == 1)
			stats = _perso.getTotalStats();
		if(_type == 2)
			stats =_mob.getStats();
		if(_type == 5)
			stats = World.data.getGuild(_Perco.get_guildID()).getStatsFight();
		if(_type == 10)
			stats = _double.getTotalStats();
		
		stats = Stats.cumulStat(stats,getFightBuffStats());
		return stats;
	}
	
	
	public void initBuffStats()
	{
		if(_type == 1)
		{
			for(Map.Entry<Integer, SpellEffect> entry : _perso.getBuffs().entrySet())
			{
				_fightBuffs.add(entry.getValue());
			}
		}
	}
	
	private Stats getFightBuffStats()
	{
		Stats stats = new Stats();
		for(SpellEffect entry : _fightBuffs)
		{
			stats.addOneStat(entry.getEffectID(), entry.getValue());
		}
		return stats;
	}
	
	public String getGmPacket(char c)
	{
		StringBuilder str = new StringBuilder();
		str.append("GM|").append(c);
		str.append(get_cell().getId()).append(";");
		_orientation = 1;
		str.append(_orientation).append(";");
		str.append("0;");
		str.append(getGUID()).append(";");
		str.append(getPacketsName()).append(";");

		switch(_type)
		{
			case 1://Perso
				str.append(_perso.getClasse().getId()).append(";");
				str.append(_perso.getGfx()).append("^").append(_perso.getSize()).append(";");
				str.append(_perso.getSex()).append(";");
				str.append(_perso.getLevel()).append(";");
				str.append(_perso.getAlign()).append(",");
				str.append("0").append(",");//TODO
				str.append((_perso.isShowWings()?_perso.getGrade():"0")).append(",");
				str.append(_perso.getLevel()+_perso.getUUID());
				if(_perso.isShowWings() && _perso.getDeshonor()>0)
					str.append(",").append(_perso.getDeshonor()>0?1:0).append(';');
				else
					str.append(";");
				str.append((_perso.getColor1()==-1?"-1":Integer.toHexString(_perso.getColor1()))).append(";");
				str.append((_perso.getColor2()==-1?"-1":Integer.toHexString(_perso.getColor2()))).append(";");
				str.append((_perso.getColor3()==-1?"-1":Integer.toHexString(_perso.getColor3()))).append(";");
				str.append(_perso.getGMStuffString()).append(";");
				str.append(getPDV()).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_PA)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_PM)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_RP_NEU)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_RP_TER)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_RP_FEU)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_RP_EAU)).append(";");	
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_RP_AIR)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_AFLEE)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_MFLEE)).append(";");
				str.append(_team).append(";");
				if(_perso.isOnMount() && _perso.getMount() != null)str.append(_perso.getMount().getColor());
				str.append(";");
			break;
			case 2://Mob
				str.append("-2;");
				str.append(_mob.getTemplate().getGfx()).append("^100;");
				str.append(_mob.getGrade()).append(";");
				str.append(_mob.getTemplate().getColors().replace(",", ";")).append(";");
				str.append("0,0,0,0;");
				str.append(this.getPDVMAX()).append(";");
				str.append(_mob.getPa()).append(";");
				str.append(_mob.getPm()).append(";");
				str.append(_team);
			break;
			case 5://Perco
				str.append("-6;");//Perco
				str.append("6000^100;");//GFXID^Size
				Guild G = World.data.getCollector(_fight.getOldMap()).getGuild();
				str.append(G.getLevel()).append(";");
				str.append("1;");//FIXME
				str.append("2;4;");//FIXME
				str.append((int)Math.floor(G.getLevel()/2)).append(";").append((int)Math.floor(G.getLevel()/2)).append(";").append((int)Math.floor(G.getLevel()/2)).append(";").append((int)Math.floor(G.getLevel()/2)).append(";").append((int)Math.floor(G.getLevel()/2)).append(";").append((int)Math.floor(G.getLevel()/2)).append(";").append((int)Math.floor(G.getLevel()/2)).append(";");//R�sistances
				str.append(_team);
			break;
			case 10://Double
				str.append(_double.getClasse().getId()).append(";");
				str.append(_double.getGfx()).append("^").append(_double.getSize()).append(";");
				str.append(_double.getSex()).append(";");
				str.append(_double.getLevel()).append(";");
				str.append(_double.getAlign()).append(",");
				str.append("0,");//TODO
				str.append((_double.isShowWings()?_double.getGrade():"0")).append(",");
				str.append(_double.getUUID()).append(";");
				str.append((_double.getColor1()==-1?"-1":Integer.toHexString(_double.getColor1()))).append(";");
				str.append((_double.getColor2()==-1?"-1":Integer.toHexString(_double.getColor2()))).append(";");
				str.append((_double.getColor3()==-1?"-1":Integer.toHexString(_double.getColor3()))).append(";");
				str.append(_double.getGMStuffString()).append(";");
				str.append(getPDV()).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_PA)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_PM)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_RP_NEU)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_RP_TER)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_RP_FEU)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_RP_EAU)).append(";");	
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_RP_AIR)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_AFLEE)).append(";");
				str.append(getTotalStats().getEffect(Constants.STATS_ADD_MFLEE)).append(";");
				str.append(_team).append(";");
				if(_double.isOnMount() && _double.getMount() != null)str.append(_double.getMount().getColor());
				str.append(";");
			break;
		}
		
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
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, 7, 950, getGUID()+"", getGUID()+","+e.getKey()+",0");
				continue;
			}
			//Sinon on remet avec la nouvelle valeur
			_state.put(e.getKey(), nVal);
		}
	}
	
	public int getPDV()
	{
		int pdv = _PDV + getBuffValue(Constants.STATS_ADD_VITA);
		return pdv;
	}
	
	public void removePDV(int pdv)
	{
		_PDV -= pdv;
	}
	
	public void applyBeginningTurnBuff(Fight fight)
	{
		synchronized(_fightBuffs)
		{
			for(int effectID : Constants.BEGIN_TURN_BUFF)
			{
				//On �vite les modifications concurrentes
				ArrayList<SpellEffect> buffs = new ArrayList<SpellEffect>();
				buffs.addAll(_fightBuffs);
				for(SpellEffect entry : buffs)
				{
					if(entry.getEffectID() == effectID)
					{
						if(Server.config.isDebug()) Log.addToLog("Effet de debut de tour : "+ effectID);
						entry.applyBeginingBuff(fight, this);
					}
				}
			}
		}
	}

	public SpellEffect getBuff(int id)
	{
		for(SpellEffect entry : _fightBuffs)
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
		for(SpellEffect entry : _fightBuffs)
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
		for(SpellEffect entry : _fightBuffs)
		{
			if(entry.getEffectID() == id)
				value += entry.getValue();
		}
		return value;
	}
	
	public int getMaitriseDmg(int id)
	{
		int value = 0;
		for(SpellEffect entry : _fightBuffs)
		{
			if(entry.getSpell() == id)
				value += entry.getValue();
		}
		return value;
	}

	
	public boolean getSpellValueBool(int id)
	{
		for(SpellEffect entry : _fightBuffs)
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
		for(SpellEffect entry : _fightBuffs)
		{
			if(entry.decrementDuration() != 0)//Si pas fin du buff
			{
				b.add(entry);
			}else
			{
				if(Server.config.isDebug()) Log.addToLog("Suppression du buff "+entry.getEffectID()+" sur le joueur Fighter ID= "+getGUID());
				switch(entry.getEffectID())
				{
					case 108:
						if(entry.getSpell() == 441)
						{
							//Baisse des pdvs max
							_PDVMAX = (_PDVMAX-entry.getValue());
							
							//Baisse des pdvs actuel
							int pdv = 0;
							if(_PDV-entry.getValue() <= 0){
								pdv = 0;
								_fight.onFighterDie(this);
								_fight.verifIfTeamAllDead();
							}
							else pdv = (_PDV-entry.getValue());
							_PDV = pdv;
						}
					break;
				
					case 150://Invisibilit�
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, 7, 150, entry.getCaster().getGUID()+"",getGUID()+",0");
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
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, 7, 950, entry.getCaster().getGUID()+"", entry.getCaster().getGUID()+","+id+",0");
					break;
				}
			}
		}
		_fightBuffs.clear();
		_fightBuffs.addAll(b);
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
		_fightBuffs.add(new SpellEffect(id,val,(_canPlay?duration+1:duration),turns,debuff,caster,args,spellID));
		if(Server.config.isDebug()) Log.addToLog("Ajout du Buff "+id+" sur le personnage Fighter ID = "+this.getGUID()+" val : "+val+" duration : "+duration+" turns : "+turns+" debuff : "+debuff+" spellid : "+spellID+" args : "+args);
		
			
		switch(id)
		{
			case 6://Renvoie de sort
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(_fight, 7, id, getGUID(), -1, val+"", "10", "", duration, spellID);
			break;
			
			case 79://Chance �ca
				val = Integer.parseInt(args.split(";")[0]);
				String valMax = args.split(";")[1];
				String chance = args.split(";")[2];
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(_fight, 7, id, getGUID(), val, valMax, chance, "", duration, spellID);
			break;
			
			case 788://Fait apparaitre message le temps de buff sacri Chatiment de X sur Y tours
				val = Integer.parseInt(args.split(";")[1]);
				String valMax2 = args.split(";")[2];
				if(Integer.parseInt(args.split(";")[0]) == 108)return;
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(_fight, 7, id, getGUID(), val, ""+val, ""+valMax2, "", duration, spellID);		
				
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
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(_fight, 7, id, getGUID(), val, "", "", "", duration, spellID);		
				}else if(valMax1.compareTo("-1") != 0)
				{
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(_fight, 7, id, getGUID(), val, valMax1, "", "", duration, spellID);
				}
				break;

			default:
				SocketManager.GAME_SEND_FIGHT_GIE_TO_FIGHT(_fight, 7, id, getGUID(), val, "", "", "", duration, spellID);
			break;
		}
	}
	
	public int getInitiative()
	{
		if(_type == 1)
			return _perso.getInitiative();
		if(_type == 2)
			return _mob.getInitiative();
		if(_type == 5)
			return World.data.getGuild(_Perco.get_guildID()).getLevel();
		if(_type == 10)
			return _double.getInitiative();
		
		return 0;
	}
	public int getPDVMAX()
	{
		return _PDVMAX + getBuffValue(Constants.STATS_ADD_VITA);
	}
	
	public int get_lvl() {
		if(_type == 1)
			return _perso.getLevel();
		if(_type == 2)
			return _mob.getLevel();
		if(_type == 5)
			return World.data.getGuild(_Perco.get_guildID()).getLevel();
		if(_type == 10)
			return _double.getLevel();

		return 0;
	}
	public String xpString(String str)
	{
		if(_perso != null)
		{
			int max = _perso.getLevel()+1;
			if(max>World.data.getExpLevelSize())max = World.data.getExpLevelSize();
			return World.data.getExpLevel(_perso.getLevel()).perso+str+_perso.getExperience()+str+World.data.getExpLevel(max).perso;		
		}
		return "0"+str+"0"+str+"0";
	}
	public String getPacketsName()
	{
		if(_type == 1)
			return _perso.getName();
		if(_type == 2)
			return _mob.getTemplate().getId()+"";
		if(_type == 5)
			return (_Perco.getFirstNameId()+","+_Perco.getLastNameId());
		if(_type == 10)
			return _double.getName();
		
		return "";
	}
	public MobGrade getMob()
	{
		if(_type == 2)
			return _mob;
		
		return null;
	}
	public int getTeam()
	{
		return _team;
	}
	public int getTeam2()
	{
		return _fight.getTeamID(_id);
	}
	public int getOtherTeam()
	{
		return _fight.getOtherTeamID(_id);
	}
	public boolean canPlay()
	{
		return _canPlay;
	}
	public void setCanPlay(boolean b)
	{
		_canPlay = b;
	}
	public ArrayList<SpellEffect> getBuffsByEffectID(int effectID)
	{
		ArrayList<SpellEffect> buffs = new ArrayList<SpellEffect>();
		for(SpellEffect buff : _fightBuffs)
		{
			if(buff.getEffectID() == effectID)
				buffs.add(buff);
		}
		return buffs;
	}
	public Stats getTotalStatsLessBuff()
	{
		Stats stats = new Stats(new TreeMap<Integer,Integer>());
		if(_type == 1)
			stats = _perso.getTotalStats();
		if(_type == 2)
			stats =_mob.getStats();
		if(_type == 5)
			stats = World.data.getGuild(_Perco.get_guildID()).getStatsFight();
		if(_type == 10)
			stats = _double.getTotalStats();
		
		return stats;
	}
	public int getPA()
	{
		if(_type == 1)
			return getTotalStats().getEffect(Constants.STATS_ADD_PA);
		if(_type == 2)
			return getTotalStats().getEffect(Constants.STATS_ADD_PA) + _mob.getPa();
		if(_type == 5)
			return getTotalStats().getEffect(Constants.STATS_ADD_PM) + 6;
		if(_type == 10)
			return getTotalStats().getEffect(Constants.STATS_ADD_PA);
		
		return 0;
	}
	public int getPM()
	{
		if(_type == 1)
			return getTotalStats().getEffect(Constants.STATS_ADD_PM);
		if(_type == 2)
			return getTotalStats().getEffect(Constants.STATS_ADD_PM) + _mob.getPm();
		if(_type == 5)
			return getTotalStats().getEffect(Constants.STATS_ADD_PM) + 3;
		if(_type == 10)
			return getTotalStats().getEffect(Constants.STATS_ADD_PM);
		
		return 0;
	}
	public int getCurPA(Fight fight)
	{
		return fight.get_curFighterPA();
	}
	
	public int getCurPM(Fight fight)
	{
		return fight.get_curFighterPM();
	}
	
	public void setCurPM(Fight fight, int pm)
	{
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
		return (_Perco!=null);
	}

    public boolean isDouble()
	{
		return (_double!=null);
	}

	public void debuff()
	{
		ArrayList<SpellEffect> newBuffs = new ArrayList<SpellEffect>();
		//on v�rifie chaque buff en cours, si pas d�buffable, on l'ajout a la nouvelle liste
		for(SpellEffect SE : _fightBuffs)
		{
			if(!SE.isDebuffabe())newBuffs.add(SE);
			//On envoie les Packets si besoin
			switch(SE.getEffectID())
			{
				case Constants.STATS_ADD_PA:
				case Constants.STATS_ADD_PA2:
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, 7, 101,getGUID()+"",getGUID()+",-"+SE.getValue());
				break;
				
				case Constants.STATS_ADD_PM:
				case Constants.STATS_ADD_PM2:
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, 7, 127,getGUID()+"",getGUID()+",-"+SE.getValue());
				break;
			}
		}
		_fightBuffs.clear();
		_fightBuffs.addAll(newBuffs);
		if(_perso != null && !_hasLeft)
			SocketManager.GAME_SEND_STATS_PACKET(_perso);
	}

	public void fullPDV()
	{
		_PDV = _PDVMAX;
	}

	public void setIsDead(boolean b)
	{
		_isDead = b;
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
		buffs.addAll(get_fightBuff());
		for(SpellEffect SE : buffs)
		{
			if(SE.getEffectID() == 150)
				get_fightBuff().remove(SE);
		}
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, 7, 150,getGUID()+"",getGUID()+",0");
		//On actualise la position
		SocketManager.GAME_SEND_GIC_PACKET_TO_FIGHT(_fight, 7,this);
	}

	public int getPdvMaxOutFight()
	{
		if(_perso != null)return _perso.getMaxPdv();
		if(_mob != null)return _mob.getMaxPdv();
		return 0;
	}

	public Map<Integer, Integer> get_chatiValue() {
		return _chatiValue;
	}

	public int getDefaultGfx()
	{
		if(_perso != null)return _perso.getGfx();
		if(_mob != null)return _mob.getTemplate().getGfx();
		return 0;
	}

	public long getXpGive()
	{
		if(_mob != null)return _mob.getXp();
		return 0;
	}
	public void addPDV(int max) 
	{
		_PDVMAX = (_PDVMAX+max);
		_PDV = (_PDV+max);
	}
	public Case get_cell() {
		return _cell;
	}
	public void set_cell(Case _cell) {
		this._cell = _cell;
	}
	public Case getFakeCell() {
		return fakeCell;
	}
	public void setFakeCell(Case fakeCell) {
		this.fakeCell = fakeCell;
	}

}