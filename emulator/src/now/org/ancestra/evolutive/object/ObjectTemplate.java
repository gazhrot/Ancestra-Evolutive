package org.ancestra.evolutive.object;

import java.util.ArrayList;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.fight.spell.SpellEffect;

public class ObjectTemplate {
	
	private int id;
	private String name;
	private	ObjectType type;
	private int level;
	private String strStats;
	private int pod;
	private int set;
	private String conditions;
	private int paCost, poMin, poMax, tauxCC, tauxEC, bonusCC;
	private boolean twoHanded;
	private int price;
	private long sold;
	private int avgPrice;
	private ArrayList<ObjectAction> actions = new ArrayList<>();
	
	public ObjectTemplate(int id, String strStats, String name, int type, int level, int pod, int price, int set, String conditions, String infos, int sold, int avgPrice)
	{
		this.id = id;
		this.strStats = strStats;
		this.name = name;
		this.type = ObjectType.getTypeById(type);
		this.level = level;
		this.pod = pod;
		this.price = price;
		this.set = set;
		this.conditions = conditions;
		this.paCost = -1;
		this.poMin = 1;
		this.poMax = 1;
		this.tauxCC = 100;
		this.tauxEC = 2;
		this.bonusCC = 0;
		this.sold = sold;
		this.avgPrice = avgPrice;
		
		try {
			String[] data = infos.split("\\;");
			this.paCost = Integer.parseInt(data[0]);
			this.poMin = Integer.parseInt(data[1]);
			this.poMax = Integer.parseInt(data[2]);
			this.tauxCC = Integer.parseInt(data[3]);
			this.tauxEC = Integer.parseInt(data[4]);
			this.bonusCC = Integer.parseInt(data[5]);
			this.twoHanded = data[6].equals("1");
		} catch(Exception e) {}
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
	}

	public ObjectType getType() {
		return type;
	}

	public void setType(ObjectType type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getStrStats() {
		return strStats;
	}

	public void setStrStats(String strStats) {
		this.strStats = strStats;
	}

	public int getPod() {
		return pod;
	}

	public void setPod(int pod) {
		this.pod = pod;
	}

	public int getSet() {
		return set;
	}

	public void setSet(int set) {
		this.set = set;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public int getPaCost() {
		return paCost;
	}

	public void setPaCost(int paCost) {
		this.paCost = paCost;
	}

	public int getPoMin() {
		return poMin;
	}

	public void setPoMin(int poMin) {
		this.poMin = poMin;
	}

	public int getPoMax() {
		return poMax;
	}

	public void setPoMax(int poMax) {
		this.poMax = poMax;
	}

	public int getTauxCC() {
		return tauxCC;
	}

	public void setTauxCC(int tauxCC) {
		this.tauxCC = tauxCC;
	}

	public int getTauxEC() {
		return tauxEC;
	}

	public void setTauxEC(int tauxEC) {
		this.tauxEC = tauxEC;
	}

	public int getBonusCC() {
		return bonusCC;
	}

	public void setBonusCC(int bonusCC) {
		this.bonusCC = bonusCC;
	}

	public boolean isTwoHanded() {
		return twoHanded;
	}

	public void setTwoHanded(boolean twoHanded) {
		this.twoHanded = twoHanded;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public long getSold() {
		return sold;
	}

	public void setSold(long sold) {
		this.sold = sold;
	}

	public int getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(int avgPrice) {
		this.avgPrice = avgPrice;
	}

	public ArrayList<ObjectAction> getActions() {
		return actions;
	}

	public void applyAction(Player player, Player target, int object, short cellid) {
		for(ObjectAction action: this.getActions())
			action.apply(player, target, object, cellid);
	}

	public Object createNewItem(int qua, boolean useMax) {		
		Object object = new Object(World.database.getItemData().nextId(), this.getId(), qua, ObjectPosition.NO_EQUIPED, this.generateNewStatsFromTemplate(this.getStrStats(), useMax), this.getEffectTemplate(this.getStrStats()));
		return object;
	}

	private Stats generateNewStatsFromTemplate(String statsTemplate, boolean useMax) {
		Stats itemStats = new Stats(false, null);
		//Si stats Vides
		if(statsTemplate.equals("") || statsTemplate == null) 
			return itemStats;
		
		String[] splitted = statsTemplate.split(",");
		for(String s : splitted)
		{	
			String[] stats = s.split("#");
			int statID = Integer.parseInt(stats[0],16);
			boolean follow = true;
			
			for(int a : Constants.ARMES_EFFECT_IDS)//Si c'est un Effet Actif
				if(a == statID)
					follow = false;
			if(!follow)//Si c'�tait un effet Actif d'arme
				continue;
			boolean isStatsInvalid = false;
			switch(statID) {
				case 110:
				case 139:
				case 605:
				case 614:
					isStatsInvalid = true;
				break;
			}
			if(isStatsInvalid)				
				continue;
			String jet = "";
			int value  = 1;
			try	{
				jet = stats[4];
				value = Formulas.getRandomJet(jet);
				if(useMax)
				{
					try	{
						//on prend le jet max
						int min = Integer.parseInt(stats[1],16);
						int max = Integer.parseInt(stats[2],16);
						value = min;
						if(max != 0)value = max;
					}catch(Exception e){value = Formulas.getRandomJet(jet);};			
				}
			} catch(Exception e) {}
			itemStats.addOneStat(statID, value);
		}
		return itemStats;
		/*Stats itemStats = new Stats(false, null);
		//Si stats Vides
		if(statsTemplate.equals("") || statsTemplate == null) 
			return itemStats;
		
		String[] splitted = statsTemplate.split("\\,");
		for(String s : splitted) {	
			String[] stats = s.split("\\#");
			int statID = Integer.parseInt(stats[0], 16);
			boolean follow = true;
			
			for(int a : Constants.ARMES_EFFECT_IDS)//Si c'est un Effet Actif
				if(a == statID)
					follow = false;
			
			if(!follow)
				continue;//Si c'�tait un effet Actif d'arme
			
			String jet = "";
			int id  = 1;
			try	{
				jet = stats[4];
				id = Formulas.getRandomJet(jet);
				if(useMax) {
					try {
						//on prend le jet max
						int min = Integer.parseInt(stats[1],16);
						int max = Integer.parseInt(stats[2],16);
						id = min;
						if(max != 0)id = max;
					} catch(Exception e) {
						id = Formulas.getRandomJet(jet);
					}	
				}
			} catch(Exception e) {}
			itemStats.addOneStat(statID, id);
		}
		return itemStats;*/
	}
	
	private ArrayList<SpellEffect> getEffectTemplate(String statsTemplate) {
		ArrayList<SpellEffect> effects = new ArrayList<>();
		if(statsTemplate.equals("") || statsTemplate == null) 
			return effects;
		
		String[] splitted = statsTemplate.split("\\,");
		
		for(String s : splitted) {	
			
			String[] stats = s.split("\\#");
			int id = Integer.parseInt(stats[0], 16);
			
			for(int a : Constants.ARMES_EFFECT_IDS) {
				if(a == id) {
					String min = stats[1];
					String max = stats[2];
					String jet = stats[4];
					String args = min+";"+max+";-1;-1;0;"+jet;
					effects.add(new SpellEffect(id, args, 0, -1));
				}
			}
			switch(id) {
				case 110:
				case 139:
				case 605:
				case 614:
					String min = stats[1];
					String max = stats[2];
					String jet = stats[4];
					String args = min+";"+max+";-1;-1;0;"+jet;
					effects.add(new SpellEffect(id, args, 0, -1));
				break;
			}
		}
		return effects;
	}
	
	public synchronized void newSold(int amount, int price) {
		long oldSold = sold;
		sold += amount;
		avgPrice = (int)((avgPrice * oldSold + price) / sold);
	}
	
	public String parseItemTemplateStats() {
		return (this.getId() + ";" + this.getStrStats());
	}
}