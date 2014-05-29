package org.ancestra.evolutive.fight.spell;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.common.Pathfinding;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.map.Case;

import java.util.ArrayList;

public class SpellStats {
	
	private int spellID;
	private int level;
	private int PACost;
	private int minPO;
	private int maxPO;
	private int TauxCC;
	private int TauxEC;
	private boolean isLineLaunch;
	private boolean hasLDV;
	private boolean isEmptyCell;
	private boolean isModifPO;
	private int maxLaunchbyTurn;
	private int maxLaunchbyByTarget;
	private int coolDown;
	private int reqLevel;
	private boolean isEcEndTurn;
	private ArrayList<SpellEffect> effects;
	private ArrayList<SpellEffect> CCeffects;
	private String porteeType;
	
	public SpellStats(int AspellID,int Alevel,int cost, int minPO, int maxPO, int tauxCC,int tauxEC, boolean isLineLaunch, boolean hasLDV,
			boolean isEmptyCell, boolean isModifPO, int maxLaunchbyTurn,int maxLaunchbyByTarget, int coolDown,
			int reqLevel,boolean isEcEndTurn, String effects,String ceffects,String typePortee)
	{
		this.spellID = AspellID;
		this.level = Alevel;
		this.PACost = cost;
		this.minPO = minPO;
		this.maxPO = maxPO;
		this.TauxCC = tauxCC;
		this.TauxEC = tauxEC;
		this.isLineLaunch = isLineLaunch;
		this.hasLDV = hasLDV;
		this.isEmptyCell = isEmptyCell;
		this.isModifPO = isModifPO;
		this.maxLaunchbyTurn = maxLaunchbyTurn;
		this.maxLaunchbyByTarget = maxLaunchbyByTarget;
		this.coolDown = coolDown;
		this.reqLevel = reqLevel;
		this.isEcEndTurn = isEcEndTurn;
		this.effects = parseEffect(effects);
		this.CCeffects = parseEffect(ceffects);
		this.porteeType = typePortee;
	}
	
	private ArrayList<SpellEffect> parseEffect(String e)
	{
		ArrayList<SpellEffect> effets = new ArrayList<SpellEffect>();
		String[] splt = e.split("\\|");
		for(String a : splt)
		{
			try
			{
				if(e.equals("-1"))continue;
				int id = Integer.parseInt(a.split(";",2)[0]);
				String args = a.split(";",2)[1];
				effets.add(new SpellEffect(id, args,spellID,level));
			}catch(Exception f){f.printStackTrace();Console.instance.println(a);System.exit(1);};
		}
		return effets;
	}


	public int getSpellID() {
		return spellID;
	}
	
	public Spell getSpell()
	{
		return World.data.getSort(spellID);
	}
	public int getSpriteID()
	{
		return getSpell().getSpriteID();
	}
	
	public String getSpriteInfos()
	{
		return getSpell().getSpriteInfos();
	}
	
	public int getLevel() {
		return level;
	}

	public int getPACost() {
		return PACost;
	}

	public int getMinPO() {
		return minPO;
	}

	public int getMaxPO() {
		return maxPO;
	}

	public int getTauxCC() {
		return TauxCC;
	}

	public int getTauxEC() {
		return TauxEC;
	}

	public boolean isLineLaunch() {
		return isLineLaunch;
	}

	public boolean hasLDV() {
		return hasLDV;
	}

	public boolean isEmptyCell() {
		return isEmptyCell;
	}

	public boolean isModifPO() {
		return isModifPO;
	}

	public int getMaxLaunchbyTurn() {
		return maxLaunchbyTurn;
	}

	public int getMaxLaunchbyByTarget() {
		return maxLaunchbyByTarget;
	}

	public int getCoolDown() {
		return coolDown;
	}

	public int getReqLevel() {
		return reqLevel;
	}

	public boolean isEcEndTurn() {
		return isEcEndTurn;
	}

	public ArrayList<SpellEffect> getEffects() {
		return effects;
	}

	public ArrayList<SpellEffect> getCCeffects() {
		return CCeffects;
	}

	public String getPorteeType() {
		return porteeType;
	}

	
	public void applySpellEffectToFight(Fight fight, Fighter perso,Case cell,ArrayList<Case> cells,boolean isCC)
	{
		//Seulement appell� par les pieges, or les sorts de piege
		ArrayList<SpellEffect> effets;
		
		if(isCC)
			effets = CCeffects;
		else
			effets = effects;
		Log.addToLog("Nombre d'effets: "+effets.size());
		int jetChance = Formulas.getRandomValue(0, 99);
		int curMin = 0;
		for(SpellEffect SE : effets)
		{
			if(SE.getChance() != 0 && SE.getChance() != 100)//Si pas 100% lancement
			{
				if(jetChance <= curMin || jetChance >= (SE.getChance() + curMin))
				{
					curMin += SE.getChance();
					continue;
				}
				curMin += SE.getChance();
			}
			
			ArrayList<Fighter> cibles = SpellEffect.getTargets(SE,fight,cells);
			SE.applyToFight(fight, perso, cell,cibles);

		}
	}
	
	public void applySpellEffectToFight(Fight fight, Fighter perso,Case cell,boolean isCC)
	{
		ArrayList<SpellEffect> effets;
		
		if(isCC)
			effets = CCeffects;
		else
			effets = effects;
		Log.addToLog("Nombre d'effets: "+effets.size());
		int jetChance = Formulas.getRandomValue(0, 99);
		int curMin = 0;
		int num = 0;
		for(SpellEffect SE : effets)
		{
			if(fight.get_state()>=Constants.FIGHT_STATE_FINISHED)return;
			if(SE.getChance() != 0 && SE.getChance() != 100)//Si pas 100% lancement
			{
				if(jetChance <= curMin || jetChance >= (SE.getChance() + curMin))
				{
					curMin += SE.getChance();
					continue;
				}
				curMin += SE.getChance();
			}
			
			int POnum = num*2;
			if(isCC)
			{
				POnum += effects.size()*2;//On zaap la partie du String des effets hors CC
			} 
			ArrayList<Case> cells = Pathfinding.getCellListFromAreaString(fight.get_map(),cell.getId(),perso.get_fightCell(false).getId(),porteeType,POnum,isCC);
			
			ArrayList<Case> finalCells = new ArrayList<Case>();
			
			int TE = 0;
			Spell S = World.data.getSort(spellID);
			//on prend le targetFlag corespondant au num de l'effet
			if(S!= null?S.getEffectTargets().size()>num:false)TE = S.getEffectTargets().get(num);
			
			for(Case C : cells)
			{
				if(C == null)continue;
				Fighter F = C.getFirstFighter();
				if(F == null)continue;
				//Ne touches pas les alli�s
				if(((TE & 1) == 1) && (F.getTeam() == perso.getTeam()))continue;
				//Ne touche pas le lanceur
				if((((TE>>1) & 1) == 1) && (F.getGUID() == perso.getGUID()))continue;
				//Ne touche pas les ennemies
				if((((TE>>2) & 1) == 1) && (F.getTeam() != perso.getTeam()))continue;
				//Ne touche pas les combatants (seulement invocations)
				if((((TE>>3) & 1) == 1) && (!F.isInvocation()))continue;
				//Ne touche pas les invocations
				if((((TE>>4) & 1) == 1) && (F.isInvocation()))continue;
				//N'affecte que le lanceur
				if((((TE>>5) & 1) == 1) && (F.getGUID() != perso.getGUID()))continue;
				//Si pas encore eu de continue, on ajoute la case
				finalCells.add(C);
			}
			//Si le sort n'affecte que le lanceur et que le lanceur n'est pas dans la zone
			if(((TE>>5) & 1) == 1)if(!finalCells.contains(perso.get_fightCell(false)))finalCells.add(perso.get_fightCell(false));
			ArrayList<Fighter> cibles = SpellEffect.getTargets(SE,fight,finalCells);
			SE.applyToFight(fight, perso, cell,cibles);
			num++;
		}
	}	
}