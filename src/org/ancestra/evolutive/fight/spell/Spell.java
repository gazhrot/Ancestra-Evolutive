package org.ancestra.evolutive.fight.spell;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.fight.spell.Spell;

public class Spell {
	
	private int spellID;
	private int spriteID;
	private String spriteInfos;
	private Map<Integer,SpellStats> SpellStats = new TreeMap<Integer,SpellStats>();
	private ArrayList<Integer> effectTargets = new ArrayList<Integer>();
	private ArrayList<Integer> CCeffectTargets = new ArrayList<Integer>();
	
	
	
	public SpellStats parseSpellStats(int id, int lvl, String str) {
		try {
			SpellStats stats = null;
			String[] stat = str.split(",");
			String effets = stat[0];
			String CCeffets = stat[1];
			int PACOST = 6;
			try {
				PACOST = Integer.parseInt(stat[2].trim());
			} catch (NumberFormatException e) {
			}
			;

			int POm = Integer.parseInt(stat[3].trim());
			int POM = Integer.parseInt(stat[4].trim());
			int TCC = Integer.parseInt(stat[5].trim());
			int TEC = Integer.parseInt(stat[6].trim());
			boolean line = stat[7].trim().equalsIgnoreCase("true");
			boolean LDV = stat[8].trim().equalsIgnoreCase("true");
			boolean emptyCell = stat[9].trim().equalsIgnoreCase("true");
			boolean MODPO = stat[10].trim().equalsIgnoreCase("true");
			// int unk = Integer.parseInt(stat[11]);//All 0
			int MaxByTurn = Integer.parseInt(stat[12].trim());
			int MaxByTarget = Integer.parseInt(stat[13].trim());
			int CoolDown = Integer.parseInt(stat[14].trim());
			String type = stat[15].trim();
			int level = Integer.parseInt(stat[stat.length - 2].trim());
			boolean endTurn = stat[19].trim().equalsIgnoreCase("true");
			stats = new SpellStats(id, lvl, PACOST, POm, POM, TCC, TEC, line,
					LDV, emptyCell, MODPO, MaxByTurn, MaxByTarget, CoolDown,
					level, endTurn, effets, CCeffets, type);
			return stats;
		} catch (Exception e) {
			e.printStackTrace();
			int nbr = 0;
			Console.instance.println("[DEBUG]Sort " + id + " lvl " + lvl);
			for (String z : str.split(",")) {
				Console.instance.println("[DEBUG]" + nbr + " " + z);
				nbr++;
			}
			System.exit(1);
			return null;
		}
	}
	
	public Spell(int aspellID, int aspriteID, String aspriteInfos,String ET)
	{
		spellID = aspellID;
		spriteID = aspriteID;
		spriteInfos = aspriteInfos;
		String nET = ET.split(":")[0];
		String ccET = "";
		if(ET.split(":").length>1)ccET = ET.split(":")[1];
		for(String num : nET.split(";"))
		{
			try
			{
				effectTargets.add(Integer.parseInt(num));
			}catch(Exception e)
			{
				effectTargets.add(0);
				continue;
			};
		}
		for(String num : ccET.split(";"))
		{
			try
			{
				CCeffectTargets.add(Integer.parseInt(num));
			}catch(Exception e)
			{
				CCeffectTargets.add(0);
				continue;
			};
		}
	}
	
	
	public ArrayList<Integer> getEffectTargets()
	{
		return effectTargets;
	}


	public int getSpriteID() {
		return spriteID;
	}

	public String getSpriteInfos() {
		return spriteInfos;
	}

	public int getSpellID() {
		return spellID;
	}
	
	public SpellStats getStatsByLevel(int lvl)
	{
		return SpellStats.get(lvl);
	}
	
	public void addSpellStats(Integer lvl,SpellStats stats)
	{
		if(SpellStats.get(lvl) != null)return;
		SpellStats.put(lvl,stats);
	}
	
}
