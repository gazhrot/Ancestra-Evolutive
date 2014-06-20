package org.ancestra.evolutive.client.other;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;

public class Stats {
	
	private Map<Integer, Integer> effects = new TreeMap<>();
	
	public Stats() {
		this.effects = new TreeMap<Integer, Integer>();
	}
	
	public Stats(Map<Integer, Integer> stats) {
		this.effects = stats;
	}
	
	public Stats(boolean addBases, Player player) {
		this.effects = new TreeMap<Integer,Integer>();
		
		if(!addBases)
			return;
		
		this.effects.put(Constants.STATS_ADD_PA, player.getLevel()<100?6:7);
		this.effects.put(Constants.STATS_ADD_PM, 3);
		this.effects.put(Constants.STATS_ADD_PROS, player.getClasse()==Constants.CLASS_ENUTROF?120:100);
		this.effects.put(Constants.STATS_ADD_PODS, 1000);
		this.effects.put(Constants.STATS_CREATURE, 1);
		this.effects.put(Constants.STATS_ADD_INIT, 1);
	}
	
	public Stats(Map<Integer, Integer> stats, boolean addBases, Player player) {
		this.effects = stats;
		
		if(!addBases)
			return;
		
		this.effects.put(Constants.STATS_ADD_PA, player.getLevel()<100?6:7);
		this.effects.put(Constants.STATS_ADD_PM, 3);
		this.effects.put(Constants.STATS_ADD_PROS, player.getClasse()==Constants.CLASS_ENUTROF?120:100);
		this.effects.put(Constants.STATS_ADD_PODS, 1000);
		this.effects.put(Constants.STATS_CREATURE, 1);
		this.effects.put(Constants.STATS_ADD_INIT, 1);
	}
		
	public Map<Integer, Integer> getEffects() {
		return this.effects;
	}
	
	public int addOneStat(int id, int value) {
		if(this.effects.get(id) == null || this.effects.get(id) == 0)
			this.effects.put(id, value);
		else
			this.effects.put(id, (this.effects.get(id) + value));
		return this.effects.get(id);
	}
	
	public boolean isSameStats(Stats other)	{
		for(Entry<Integer,Integer> entry : this.effects.entrySet()) {
			//Si la stat n'existe pas dans l'autre map
			if(other.getEffects().get(entry.getKey()) == null)
				return false;
			//Si la stat existe mais n'a pas la même valueeur
			if(other.getEffects().get(entry.getKey()).compareTo(entry.getValue()) != 0)
				return false;	
		}
		for(Entry<Integer,Integer> entry : other.getEffects().entrySet()) {
			//Si la stat n'existe pas dans l'autre map
			if(this.effects.get(entry.getKey()) == null)
				return false;
			//Si la stat existe mais n'a pas la même valueeur
			if(this.effects.get(entry.getKey()).compareTo(entry.getValue()) != 0)
				return false;	
		}
		return true;
	}
	
	public int getEffect(int id) {
		int value;
		if(this.effects.get(id) == null)
			value = 0;
		else
			value = this.effects.get(id);
		
		switch(id)//Bonus/Malus TODO
		{
			case Constants.STATS_ADD_AFLEE:
				if(this.effects.get(Constants.STATS_REM_AFLEE)!= null)
					value -= (int)(getEffect(Constants.STATS_REM_AFLEE));
				if(this.effects.get(Constants.STATS_ADD_SAGE) != null)
					value += (int)(getEffect(Constants.STATS_ADD_SAGE)/4);
			break;
			case Constants.STATS_ADD_MFLEE:
				if(this.effects.get(Constants.STATS_REM_MFLEE)!= null)
					value -= (int)(getEffect(Constants.STATS_REM_MFLEE));
				if(this.effects.get(Constants.STATS_ADD_SAGE) != null)
					value += (int)(getEffect(Constants.STATS_ADD_SAGE)/4);
			break;
			case Constants.STATS_ADD_INIT:
				if(this.effects.get(Constants.STATS_REM_INIT)!= null)
					value -= this.effects.get(Constants.STATS_REM_INIT);
			break;
			case Constants.STATS_ADD_AGIL:
				if(this.effects.get(Constants.STATS_REM_AGIL)!= null)
					value -= this.effects.get(Constants.STATS_REM_AGIL);
			break;
			case Constants.STATS_ADD_FORC:
				if(this.effects.get(Constants.STATS_REM_FORC)!= null)
					value -= this.effects.get(Constants.STATS_REM_FORC);
			break;
			case Constants.STATS_ADD_CHAN:
				if(this.effects.get(Constants.STATS_REM_CHAN)!= null)
					value -= this.effects.get(Constants.STATS_REM_CHAN);
			break;
			case Constants.STATS_ADD_INTE:
				if(this.effects.get(Constants.STATS_REM_INTE)!= null)
				value -= this.effects.get(Constants.STATS_REM_INTE);
			break;
			case Constants.STATS_ADD_PA:
				if(this.effects.get(Constants.STATS_ADD_PA2)!= null)
					value += this.effects.get(Constants.STATS_ADD_PA2);
				if(this.effects.get(Constants.STATS_REM_PA)!= null)
					value -= this.effects.get(Constants.STATS_REM_PA);
				if(this.effects.get(Constants.STATS_REM_PA2)!= null)//Non esquivable
					value -= this.effects.get(Constants.STATS_REM_PA2);
			break;
			case Constants.STATS_ADD_PM:
				if(this.effects.get(Constants.STATS_ADD_PM2)!= null)
					value += this.effects.get(Constants.STATS_ADD_PM2);
				if(this.effects.get(Constants.STATS_REM_PM)!= null)
					value -= this.effects.get(Constants.STATS_REM_PM);
				if(this.effects.get(Constants.STATS_REM_PM2)!= null)//Non esquivable
					value -= this.effects.get(Constants.STATS_REM_PM2);
			break;
			case Constants.STATS_ADD_PO:
				if(this.effects.get(Constants.STATS_REM_PO)!= null)
					value -= this.effects.get(Constants.STATS_REM_PO);
			break;
			case Constants.STATS_ADD_VITA:
				if(this.effects.get(Constants.STATS_REM_VITA)!= null)
					value -= this.effects.get(Constants.STATS_REM_VITA);
			break;
			case Constants.STATS_ADD_DOMA:
				if(this.effects.get(Constants.STATS_REM_DOMA)!= null)
					value -= this.effects.get(Constants.STATS_REM_DOMA);
			break;
			case Constants.STATS_ADD_PODS:
				if(this.effects.get(Constants.STATS_REM_PODS)!= null)
					value -= this.effects.get(Constants.STATS_REM_PODS);
			break;
			case Constants.STATS_ADD_PROS:
				if(this.effects.get(Constants.STATS_REM_PROS)!= null)
					value -= this.effects.get(Constants.STATS_REM_PROS);
			break;
			case Constants.STATS_ADD_R_TER:
				if(this.effects.get(Constants.STATS_REM_R_TER)!= null)
					value -= this.effects.get(Constants.STATS_REM_R_TER);
			break;
			case Constants.STATS_ADD_R_EAU:
				if(this.effects.get(Constants.STATS_REM_R_EAU)!= null)
					value -= this.effects.get(Constants.STATS_REM_R_EAU);
			break;
			case Constants.STATS_ADD_R_AIR:
				if(this.effects.get(Constants.STATS_REM_R_AIR)!= null)
					value -= this.effects.get(Constants.STATS_REM_R_AIR);
			break;
			case Constants.STATS_ADD_R_FEU:
				if(this.effects.get(Constants.STATS_REM_R_FEU)!= null)
					value -= this.effects.get(Constants.STATS_REM_R_FEU);
			break;
			case Constants.STATS_ADD_R_NEU:
				if(this.effects.get(Constants.STATS_REM_R_NEU)!= null)
					value -= this.effects.get(Constants.STATS_REM_R_NEU);
			break;
			case Constants.STATS_ADD_RP_TER:
				if(this.effects.get(Constants.STATS_REM_RP_TER)!= null)
					value -= this.effects.get(Constants.STATS_REM_RP_TER);
			break;
			case Constants.STATS_ADD_RP_EAU:
				if(this.effects.get(Constants.STATS_REM_RP_EAU)!= null)
					value -= this.effects.get(Constants.STATS_REM_RP_EAU);
			break;
			case Constants.STATS_ADD_RP_AIR:
				if(this.effects.get(Constants.STATS_REM_RP_AIR)!= null)
					value -= this.effects.get(Constants.STATS_REM_RP_AIR);
			break;
			case Constants.STATS_ADD_RP_FEU:
				if(this.effects.get(Constants.STATS_REM_RP_FEU)!= null)
					value -= this.effects.get(Constants.STATS_REM_RP_FEU);
			break;
			case Constants.STATS_ADD_RP_NEU:
				if(this.effects.get(Constants.STATS_REM_RP_NEU)!= null)
					value -= this.effects.get(Constants.STATS_REM_RP_NEU);
			break;
			case Constants.STATS_ADD_MAITRISE:
				if(this.effects.get(Constants.STATS_ADD_MAITRISE)!= null)
					value = this.effects.get(Constants.STATS_ADD_MAITRISE);
			break;
		}
		return value;
	}

	public static Stats cumulStat(Stats s1, Stats s2) {
		TreeMap<Integer, Integer> effects = new TreeMap<>();
		
		for(int a = 0; a <= Constants.MAX_EFFECTS_ID; a++) {
			if((s1.effects.get(a) == null  || s1.effects.get(a) == 0) && (s2.effects.get(a) == null || s2.effects.get(a) == 0))
				continue;
			int som = 0;
			if(s1.effects.get(a) != null)
				som += s1.effects.get(a);
			
			if(s2.effects.get(a) != null)
				som += s2.effects.get(a);
			
			effects.put(a, som);
		}
		return new Stats(effects, false, null);
	}
	
	public String parseToItemSetStats() {
		StringBuilder str = new StringBuilder();
		
		if(this.effects.isEmpty())
			return "";
		
		for(Entry<Integer,Integer> entry : this.effects.entrySet()) {
			if(str.length() >0)
				str.append(",");
			str.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(entry.getValue())).append("#0#0");
		}
		return str.toString();
	}
}