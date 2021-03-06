package org.ancestra.evolutive.entity.creature.monster;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.spell.Spell;
import org.ancestra.evolutive.fight.spell.SpellEffect;
import org.ancestra.evolutive.fight.spell.SpellStats;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MobGrade {

	private final MobTemplate template;
	private int maxPdv;
    private int pdv;
	private int initiative;
	private final int pa;
	private final int pm;
	private final int level;
	private final int grade;
	private int xp = 10;
	private ArrayList<SpellEffect> buffs = new ArrayList<>();
	private Map<Integer, Integer> stats = new TreeMap<>();
	private Map<Integer, SpellStats> spells = new TreeMap<>();
	
	public MobGrade(MobTemplate template, int grade, int level, int pa, int pm, String resistances, String stats, String spells, int maxPdv, int initiative, int xp) {
		this.template = template;

		this.grade = grade;
		this.level = level;
		this.maxPdv = maxPdv;
        this.pdv = maxPdv;
		this.initiative = initiative;
		this.pa = pa;
		this.pm = pm;
		this.xp = xp;
		
		String[] resistancesArray = resistances.split(";");
		String[] statsArray = stats.split(",");
		int RN = 0, RF = 0, RE = 0, RA = 0, RT = 0, AF = 0, MF = 0;
		int force = 0, intell = 0, sagesse = 0, chance = 0, agilite = 0;

		try {
			RN = Integer.parseInt(resistancesArray[0]);
			RT = Integer.parseInt(resistancesArray[1]);
			RF = Integer.parseInt(resistancesArray[2]);
			RE = Integer.parseInt(resistancesArray[3]);
			RA = Integer.parseInt(resistancesArray[4]);
			AF = Integer.parseInt(resistancesArray[5]);
			MF = Integer.parseInt(resistancesArray[6]);
			force = Integer.parseInt(statsArray[0]);
			sagesse = Integer.parseInt(statsArray[1]);
			intell = Integer.parseInt(statsArray[2]);
			chance = Integer.parseInt(statsArray[3]);
			agilite = Integer.parseInt(statsArray[4]);
		} catch(Exception e) {
			e.printStackTrace();
		}

        this.stats.put(Constants.STATS_ADD_INIT,initiative);
        this.stats.put(Constants.STATS_ADD_PA,pa);
        this.stats.put(Constants.STATS_ADD_PM,pm);
		this.stats.put(Constants.STATS_ADD_FORC, force);
		this.stats.put(Constants.STATS_ADD_SAGE, sagesse);
		this.stats.put(Constants.STATS_ADD_INTE, intell);
		this.stats.put(Constants.STATS_ADD_CHAN, chance);
		this.stats.put(Constants.STATS_ADD_AGIL, agilite);
		this.stats.put(Constants.STATS_ADD_RP_NEU, RN);
		this.stats.put(Constants.STATS_ADD_RP_FEU, RF);
		this.stats.put(Constants.STATS_ADD_RP_EAU, RE);
		this.stats.put(Constants.STATS_ADD_RP_AIR, RA);
		this.stats.put(Constants.STATS_ADD_RP_TER, RT);
		this.stats.put(Constants.STATS_ADD_AFLEE, AF);
		this.stats.put(Constants.STATS_ADD_MFLEE, MF);
		
		this.spells.clear();
		String[] spellsArray = spells.split(";");
		
		for(String str: spellsArray) {
			if(str.equals(""))
				continue;
			
			String[] spellInfo = str.split("@");
			int spellID = 0, spellLvl = 0;
			
			try {
				spellID = Integer.parseInt(spellInfo[0]);
				spellLvl = Integer.parseInt(spellInfo[1]);
			} catch(Exception e) {
				continue;
			}
			
			if(spellID == 0 || spellLvl == 0)
				continue;
			
			Spell sort = World.data.getSort(spellID);
			
			if(sort == null)
				continue;
			
			SpellStats SpellStats = sort.getStatsByLevel(spellLvl);
			
			if(SpellStats == null)
				continue;
			
			this.spells.put(spellID, SpellStats);
		}
	}

	private MobGrade(MobTemplate template, int grade, int level, int pdv, int maxPdv, int pa, int pm, Map<Integer, Integer> stats, Map<Integer, SpellStats> spells, int xp) {
		this.template = template;
		this.grade = grade;
		this.level = level;
		this.maxPdv = maxPdv;
        this.pdv = pdv;
		this.pa = pa;
		this.pm = pm;
		this.stats = stats;
		this.spells = spells;	
		this.xp = xp;
	}
	
	public MobTemplate getTemplate() {
		return template;
	}

	public int getPdv() {
		return pdv;
	}

	public void setPdv(int pdv) {
		this.pdv = pdv;
	}

    public int getMaxPdv() {
		return maxPdv;
	}

	public void setMaxPdv(int maxPdv) {
		this.maxPdv = maxPdv;
	}

	public int getInitiative() {
		return initiative;
	}

	public int getPa() {
		return pa;
	}

	public int getPm() {
		return pm;
	}

	public int getLevel() {
		return level;
	}

    public String getName() {
        return Integer.toString(this.getTemplate().getId());
    }

    public int getGrade() {
		return grade;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}

	public ArrayList<SpellEffect> getBuffs() {
		return buffs;
	}

	public void setBuffs(ArrayList<SpellEffect> buffs) {
		this.buffs = buffs;
	}
	
	public Map<Integer,Integer> getStats() {
		return  stats;
	}

    public void setStats(Map<Integer, Integer> stats) {
		this.stats = stats;
    }

	public Map<Integer, SpellStats> getSpells() {
		return spells;
	}

	public MobGrade getCopy() {
		Map<Integer, Integer> stats = new TreeMap<>();
		stats.putAll(this.stats);
		return new MobGrade(this.getTemplate(), this.getGrade(), this.getLevel(), this.getPdv(), this.getMaxPdv(), this.getPa(), this.getPm(), stats, this.getSpells(), this.getXp());
	}

	public void setStatByInvocator(Fighter caster) {

		int coef = (1 + (caster.getLvl()) / 100);
		this.setPdv(this.getMaxPdv() * coef);
		this.setMaxPdv(this.getPdv());
		int force = caster.getTotalStats().getEffect(Constants.STATS_ADD_FORC) * coef;
		int intel = caster.getTotalStats().getEffect(Constants.STATS_ADD_INTE) * coef;
		int agili = caster.getTotalStats().getEffect(Constants.STATS_ADD_AGIL) * coef;
		int sages = caster.getTotalStats().getEffect(Constants.STATS_ADD_SAGE) * coef;
		int chanc = caster.getTotalStats().getEffect(Constants.STATS_ADD_CHAN) * coef;
		this.stats.put(Constants.STATS_ADD_FORC, force);
		this.stats.put(Constants.STATS_ADD_INTE, intel);
		this.stats.put(Constants.STATS_ADD_AGIL, agili);
		this.stats.put(Constants.STATS_ADD_SAGE, sages);
		this.stats.put(Constants.STATS_ADD_CHAN, chanc);	
	}

}