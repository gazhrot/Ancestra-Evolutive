package org.ancestra.evolutive.object;

import java.util.ArrayList;

import org.ancestra.evolutive.common.Couple;

import org.ancestra.evolutive.object.Object;

public class SoulStone extends Object {
	
	private ArrayList<Couple<Integer, Integer>> monsters;
	
	public SoulStone (int id, int qua, int template, int pos, String strStats) {
		super(id, template, 1, -1, "");
		
		this.monsters = new ArrayList<Couple<Integer, Integer>>();//Couple<MonstreID,Level>
		this.parseStringToStats(strStats);
	}
	
	public void parseStringToStats(String monsters) {//Dans le format "monstreID,lvl|monstreID,lvl..."
		String[] split = monsters.split("\\|");
		for(String s : split) {	
			try	{
				int monstre = Integer.parseInt(s.split(",")[0]);
				int level = Integer.parseInt(s.split(",")[1]);
				this.monsters.add(new Couple<Integer, Integer>(monstre, level));	
			} catch(Exception e) {
				continue;
			}
		}
	}
	
	public String parseStatsString() {
		StringBuilder stats = new StringBuilder();
		boolean isFirst = true;
		for(Couple<Integer, Integer> coupl : this.monsters) {
			if(!isFirst)
				stats.append(",");
			
			try	{
				stats.append("26f#0#0#").append(Integer.toHexString(coupl.getKey()));
			} catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			
			isFirst = false;
		}
		return stats.toString();
	}
	
	public String parseGroupData() {//Format : id,lvlMin,lvlMax;id,lvlMin,lvlMax...
		StringBuilder toReturn = new StringBuilder();
		boolean isFirst = true;
		
		for(Couple<Integer, Integer> curMob : this.monsters) {
			if(!isFirst)
				toReturn.append(";");
			
			toReturn.append(curMob.getKey()).append(",").append(curMob.getValue()).append(",").append(curMob.getValue());
			
			isFirst = false;
		}
		return toReturn.toString();
	}
	
	public String parseToSave() {
		StringBuilder toReturn = new StringBuilder();
		boolean isFirst = true;
		for(Couple<Integer, Integer> curMob : this.monsters) {
			if(!isFirst)
				toReturn.append("|");
			toReturn.append(curMob.getKey()).append(",").append(curMob.getValue());
			isFirst = false;
		}
		return toReturn.toString();
	}
}
