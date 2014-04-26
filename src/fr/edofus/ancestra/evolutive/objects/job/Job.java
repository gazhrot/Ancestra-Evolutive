package fr.edofus.ancestra.evolutive.objects.job;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.objects.Objet;
import fr.edofus.ancestra.evolutive.objects.Objet.ObjTemplate;

public class Job {

	private int id;
	private ArrayList<Integer> tools = new ArrayList<Integer>();
	private Map<Integer,ArrayList<Integer>> crafts = new TreeMap<Integer,ArrayList<Integer>>();
	
	public Job(int id, String tools, String crafts)
	{
		this.id = id;
		if(!tools.equals(""))
		{
			for(String str : tools.split(","))
			{
				try	{
					this.tools.add(Integer.parseInt(str));
				}catch(Exception e) {continue;};
			}
		}
		if(!crafts.equals(""))
		{
			for(String str : crafts.split("\\|"))
			{
				try	{
					ArrayList<Integer> list = new ArrayList<Integer>();
					for(String str2 : str.split(";")[1].split(","))
						list.add(Integer.parseInt(str2));
					this.crafts.put(Integer.parseInt(str.split(";")[0]), list);
				}catch(Exception e) {continue;};
			}
		}
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean isValidTool(int id) {
		for(int i : this.tools)
			if(id == i)
				return true;
		return false;
	}
	
	public ArrayList<Integer> getListBySkill(int skID) {
		return this.crafts.get(skID);
	}
	
	public boolean canCraft(int skill,int template)	{
		if(this.crafts.get(skill) != null)
			for(int i : this.crafts.get(skill))
				if(i == template)
					return true;
		return false;
	}
	
	public static int getBaseMaxJet(int templateID, String statsModif) {
		ObjTemplate t = World.data.getObjTemplate(templateID);
		String[] splitted = t.getStrTemplate().split(",");
		for(String s : splitted) {
			String[] stats = s.split("#");
			if(stats[0].compareTo(statsModif) > 0) {//Effets n'existe pas de base
				continue;
			}else 
			if(stats[0].compareTo(statsModif) == 0) {//L'effet existe bien !
				int max = Integer.parseInt(stats[2],16);
				if(max == 0) 
					max = Integer.parseInt(stats[1],16);//Pas de jet maximum on prend le minimum
				return max;
			}
		}
		return 0;
	}
	
	public static int getActualJet(Objet obj, String statsModif) {
		for(Entry<Integer,Integer> entry : obj.getStats().getMap().entrySet()) {
			if(Integer.toHexString(entry.getKey()).compareTo(statsModif) > 0) {//Effets inutiles
				continue;
			}else 
			if(Integer.toHexString(entry.getKey()).compareTo(statsModif) == 0) {//L'effet existe bien !
				return entry.getValue();
			}
		}	
		return 0;
	}
	
	public static byte viewActualStatsItem(Objet obj, String stats)//retourne vrai si le stats est actuellement sur l'item
	{
		if (!obj.parseStatsString().isEmpty()) 
		{
			for (Entry<Integer, Integer> entry : obj.getStats().getMap().entrySet()) 
			{
				if (Integer.toHexString(entry.getKey()).compareTo(stats) > 0)//Effets inutiles
				{
					if (Integer.toHexString(entry.getKey()).compareTo("98") == 0 && stats.compareTo("7b") == 0) {
						return 2;
					}else if (Integer.toHexString(entry.getKey()).compareTo("9a") == 0 && stats.compareTo("77") == 0) {
						return 2;
					}else if (Integer.toHexString(entry.getKey()).compareTo("9b") == 0 && stats.compareTo("7e") == 0) {
						return 2;
					}else if (Integer.toHexString(entry.getKey()).compareTo("9d") == 0 && stats.compareTo("76") == 0) {
						return 2;
					}else if (Integer.toHexString(entry.getKey()).compareTo("74") == 0 && stats.compareTo("75") == 0) {
						return 2;
					}else if (Integer.toHexString(entry.getKey()).compareTo("99") == 0 && stats.compareTo("7d") == 0) {
						return 2;
					}else 
					{
						continue;
					}
				}else if (Integer.toHexString(entry.getKey()).compareTo(stats) == 0)//L'effet existe bien !
				{
					return 1;
				}
			}
			return 0;
		}else
		{
			return 0;
		}
	}
	
	public static byte viewBaseStatsItem(Objet obj, String ItemStats)//retourne vrai si le stats existe de base sur l'item
	{
		
		String[] splitted = obj.getTemplate().getStrTemplate().split(",");
		for(String s : splitted)
		{
			String[] stats = s.split("#");
			if(stats[0].compareTo(ItemStats) > 0)//Effets n'existe pas de base
			{
				if(stats[0].compareTo("98") == 0 && ItemStats.compareTo("7b") == 0)
				{
					return 2;
				}else if(stats[0].compareTo("9a") == 0 && ItemStats.compareTo("77") == 0)
				{
					return 2;
				}else if(stats[0].compareTo("9b") == 0 && ItemStats.compareTo("7e") == 0)
				{
					return 2;
				}else if(stats[0].compareTo("9d") == 0 && ItemStats.compareTo("76") == 0)
				{
					return 2;
				}else if(stats[0].compareTo("74") == 0 && ItemStats.compareTo("75") == 0)
				{
					return 2;
				}else if(stats[0].compareTo("99") == 0 && ItemStats.compareTo("7d") == 0)
				{
					return 2;
				}else
				{
					continue;
				}
			}else if(stats[0].compareTo(ItemStats) == 0)//L'effet existe bien !
			{
				return 1;
			}
		}
		return 0;
	}	
}
