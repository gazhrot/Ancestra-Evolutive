package org.ancestra.evolutive.entity;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.core.World;

import org.ancestra.evolutive.object.Objet;

public class Mount {

	private int id;
	private String name;
	private int color;
	private int sex;
	private int level;
	private long experience;
	private int fatigue;
	private int energy;
	private int reproduction;
	private int amour;
	private int endurance;
	private int maturite;
	private int serenite;
	private Stats stats = new Stats();
	private String ancestor = ",,,,,,,,,,,,,";
	private ArrayList<Objet> objects = new ArrayList<Objet>();
	
	public Mount(int color) {
		this.id = World.data.getNextIdForMount();
		this.name = "SansNom";
		this.color = color;
		this.level = 1;
		this.experience = 0;
		
		this.fatigue = 0;
		this.energy = 10000;
		this.reproduction = 0;
		this.maturite = 1000;
		this.serenite = 0;
		this.stats = Constants.getMountStats(this.color,this.level);
		this.ancestor = ",,,,,,,,,,,,,";
		
		World.data.addDragodinde(this);
		World.database.getMountData().create(this);
	}
	
	public Mount(int id, int color, int sex, int amour, int endurance, int level, long experience, String name, 
		int fatigue, int energy, int reproduction, int maturite, int serenite, String items, String ancestor) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.sex = sex;
		this.level = level;
		this.experience = experience;
		
		this.fatigue = fatigue;
		this.energy = energy;
		this.reproduction = reproduction;
		this.amour = amour;
		this.endurance = endurance;
		this.maturite = maturite;
		this.serenite = serenite;
		this.ancestor = ancestor;
		this.stats = Constants.getMountStats(this.color, this.level);
		
		for(String str : items.split("\\;")) {
			try	{		
				Objet obj = World.data.getObjet(Integer.parseInt(str));
				if(obj != null)
					this.objects.add(obj);
			} catch(Exception e) {}
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		World.database.getMountData().update(this);
	}
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getExperience() {
		return experience;
	}

	public void addExperience(long experience) {
		this.experience += experience;
		while(this.getExperience() >= World.data.getExpLevel(this.getLevel() + 1).dinde && this.getLevel() < 100)
			levelUp();	
	}

	public int getFatigue() {
		return fatigue;
	}

	public void setFatigue(int fatigue) {
		this.fatigue = fatigue;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public int getReproduction() {
		return reproduction;
	}

	public void setReproduction(int reproduction) {
		this.reproduction = reproduction;
	}

	public int getAmour() {
		return amour;
	}

	public void setAmour(int amour) {
		this.amour = amour;
	}

	public int getEndurance() {
		return endurance;
	}

	public void setEndurance(int endurance) {
		this.endurance = endurance;
	}

	public int getMaturite() {
		return maturite;
	}

	public void setMaturite(int maturite) {
		this.maturite = maturite;
	}

	public int getSerenite() {
		return serenite;
	}

	public void setSerenite(int serenite) {
		this.serenite = serenite;
	}

	public Stats getStats() {
		return stats;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public String getAncestor() {
		return ancestor;
	}

	public void setAncestor(String ancestor) {
		this.ancestor = ancestor;
	}

	public ArrayList<Objet> getObjects() {
		return objects;
	}

	public void setObjects(ArrayList<Objet> objects) {
		this.objects = objects;
	}	

	public boolean isMountable() {
		if(this.getEnergy() < 10 || this.getMaturite() < 1000 || this.getFatigue() == 240)
			return false;
		return true;
	}

	public String getObjectsId() {
		String str = "";
		for(Objet obj : this.getObjects())
			str += (str.length()>0 ? ";" : "") + obj.getGuid();
		return str;
	}

	public void levelUp() {
		this.level++;
		this.stats = Constants.getMountStats(this.getColor(), this.getLevel());
	}
	
	public String parse() {
		StringBuilder str = new StringBuilder();
		str.append(this.id).append(":");
		str.append(this.color).append(":");
		str.append(this.ancestor).append(":");
		str.append(",").append(":");//FIXME capacités
		str.append(this.name).append(":");
		str.append(this.sex).append(":");
		str.append(this.parseXpString()).append(":");
		str.append(this.level).append(":");
		str.append("1").append(":");//FIXME
		str.append("1000").append(":");//Total pod
		str.append("0").append(":");//FIXME podActuel?
		str.append(this.endurance).append(",10000:");
		str.append(this.maturite).append(",").append(1000).append(":");
		str.append(this.energy).append(",").append(10000).append(":");
		str.append(this.serenite).append(",-10000,10000:");
		str.append(this.amour).append(",10000:");
		str.append("-1").append(":");//FIXME
		str.append("0").append(":");//FIXME
		str.append(parseStats()).append(":");
		str.append(this.fatigue).append(",240:");
		str.append(this.reproduction).append(",20:");
		return str.toString();
	}
	
	private String parseXpString() {
		return this.getExperience() + "," + World.data.getExpLevel(this.getLevel()).dinde + "," + World.data.getExpLevel(this.getLevel() + 1).dinde;
	}

	private String parseStats() {
		String stats = "";
		for(Entry<Integer,Integer> entry : this.getStats().getEffects().entrySet()) {
			if(entry.getValue() <= 0)
				continue;
			if(stats.length() > 0)
				stats += ",";
			
			stats += Integer.toHexString(entry.getKey())+"#"+Integer.toHexString(entry.getValue())+"#0#0";
		}
		return stats;
	}
}
