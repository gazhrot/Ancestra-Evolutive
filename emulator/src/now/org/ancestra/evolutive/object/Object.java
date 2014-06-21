package org.ancestra.evolutive.object;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.fight.spell.SpellEffect;
import org.ancestra.evolutive.job.Job;
import org.ancestra.evolutive.object.Object;

public class Object {
	
	private int id;
	private int quantity = 1;
	private int position = Constants.ITEM_POS_NO_EQUIPED;
	private ObjectTemplate template;
	private Stats stats = new Stats();
	private ArrayList<SpellEffect> effects = new ArrayList<>();
	private Map<Integer, String> txtStats = new TreeMap<>();
	
	public Object(int id, int template, int quantity, int position, String strStats) {
		this.id = id;
		this.template = World.data.getObjectTemplate(template);
		this.quantity = quantity;
		this.position = position;
		this.stats = new Stats();
		this.parseStringToStats(strStats);
	}
	
	public Object(int id, int template, int quantity, int position,	Stats stats, ArrayList<SpellEffect> effects) {
		this.id = id;
		this.template = World.data.getObjectTemplate(template);
		this.quantity = quantity;
		this.position = position;
		this.stats = stats;
		this.effects = effects;
	}	
	
	public static Object getClone(Object object, int quantity) {
		return new Object(World.data.getNewObjectGuid(), object.getTemplate().getId(), quantity, Constants.ITEM_POS_NO_EQUIPED, object.getStats(), object.getEffects());
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public ObjectTemplate getTemplate() {
		return template;
	}

	public void setTemplate(ObjectTemplate template) {
		this.template = template;
	}

	public Stats getStats() {
		return stats;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}
	
	public ArrayList<SpellEffect> getEffects() {
		return effects;
	}

	public Map<Integer, String> getTxtStats() {
		return txtStats;
	}

	public void clearStats() {
		this.stats = new Stats();
		this.effects.clear();
		this.txtStats.clear();
	}	
	
	public String getTraquedName() {
		for(Entry<Integer,String> entry : txtStats.entrySet())
			if(Integer.toHexString(entry.getKey()).compareTo("3dd") == 0)
				return entry.getValue();	
		return null;
	}
	
	public ArrayList<SpellEffect> getCritEffects() {
		ArrayList<SpellEffect> effets = new ArrayList<>();
		for(SpellEffect SE : this.getEffects()) {
			try	{
				boolean boost = true;
				
				for(int i : Constants.NO_BOOST_CC_IDS)
					if(i == SE.getEffectID())
						boost = false;
				
				String[] infos = SE.getArgs().split("\\;");
				
				if(!boost) {
					effets.add(SE);
					continue;
				}
				
				int min = Integer.parseInt(infos[0],16)+ (boost?template.getBonusCC():0);
				int max = Integer.parseInt(infos[1],16)+ (boost?template.getBonusCC():0);
				String jet = "1d"+(max-min+1)+"+"+(min-1);
				//exCode: String newArgs = Integer.toHexString(min)+";"+Integer.toHexString(max)+";-1;-1;0;"+jet;
				//osef du minMax, vu qu'on se sert du jet pour calculer les dégats
				String newArgs = "0;0;0;-1;0;"+jet;
				effets.add(new SpellEffect(SE.getEffectID(),newArgs,0,-1));
			} catch(Exception e) {
				continue;
			}
		}
		return effets;
	}
	
	public int getRandomValue(String statsTemplate, int statsId) {
		if(statsTemplate.equals("") || statsTemplate == null) 
			return 0;

		String[] splitted = statsTemplate.split("\\,");
		int value = 0;
		for(String s : splitted) {	
			String[] stats = s.split("\\#");
			int statID = Integer.parseInt(stats[0], 16);
			if(statID != statsId)
				continue;
			String jet = "";
			try	{
				jet = stats[4];
				value = Formulas.getRandomJet(jet);
			}catch(Exception e){return 0;};
		}
		return value;
	}
	
	public Stats generateNewStatsFromTemplate(String statsTemplate, boolean useMax) {
		Stats itemStats = new Stats(false, null);
		//Si stats Vides
		if(statsTemplate.equals("") || statsTemplate == null) return itemStats;

		String[] splitted = statsTemplate.split(",");
		for(String s : splitted)
		{	
			String[] stats = s.split("#");
			int statID = Integer.parseInt(stats[0],16);
			boolean follow = true;
			
			for(int a : Constants.ARMES_EFFECT_IDS)//Si c'est un Effet Actif
				if(a == statID)
					follow = false;
			if(!follow)continue;//Si c'était un effet Actif d'arme
			
			String jet = "";
			int value  = 1;
			try
			{
				jet = stats[4];
				value = Formulas.getRandomJet(jet);
				if(useMax)
				{
					try
					{
						//on prend le jet max
						int min = Integer.parseInt(stats[1],16);
						int max = Integer.parseInt(stats[2],16);
						value = min;
						if(max != 0)value = max;
					}catch(Exception e){value = Formulas.getRandomJet(jet);};			
				}
			}catch(Exception e){};
			itemStats.addOneStat(statID, value);
		}
		return itemStats;
	}
	/***********FM SYSTEM***********/
	/**   Rien a été changé ici   **/
	public static int getPoidOfActualItem(String statsTemplate)//Donne le poid de l'item actuel
	{
		int poid = 0;
		int somme = 0;
		String[] splitted = statsTemplate.split(",");
		for(String s : splitted)
		{
			String[] stats = s.split("#");
			int statID = Integer.parseInt(stats[0],16);
			boolean follow = true;
			
			for(int a : Constants.ARMES_EFFECT_IDS)//Si c'est un Effet Actif
				if(a == statID)
					follow = false;
			if(!follow)continue;//Si c'était un effet Actif d'arme
			
			String jet = "";
			int value  = 1;
			try
			{
				jet = stats[4];
				value = Formulas.getRandomJet(jet);
					try
					{
						//on prend le jet max
						int min = Integer.parseInt(stats[1],16);
						int max = Integer.parseInt(stats[2],16);
						value = min;
						if(max != 0)value = max;
					}catch(Exception e){value = Formulas.getRandomJet(jet);};			
			}catch(Exception e){};
			
			int multi = 1;
			if(statID == 118 || statID == 126 || statID == 125 || statID == 119 || statID == 123 || statID == 158 || statID == 174)//Force,Intel,Vita,Agi,Chance,Pod,Initiative
			{
				multi = 1;
			}
			else if(statID == 138 || statID == 666 || statID == 226 || statID == 220)//Domages %,Domage renvoyé,Piège %
			{
				multi = 2;
			}	
			else if(statID == 124 || statID == 176)//Sagesse,Prospec
			{
				multi = 3;
			}
			else if(statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244)//Ré Feu, Air, Eau, Terre, Neutre
			{
				multi = 4;
			}
			else if(statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214)//Ré % Feu, Air, Eau, Terre, Neutre
			{
				multi = 5;
			}
			else if(statID == 225)//Piège
			{
				multi = 15;
			}
			else if(statID == 178 || statID == 112)//Soins,Dommage
			{
				multi = 20;
			}
			else if(statID == 115 || statID == 182)//Cri,Invoc
			{
				multi = 30;
			}
			else if(statID == 117)//PO
			{
				multi = 50;
			}
			else if(statID == 128)//PM
			{
				multi = 90;
			}
			else if(statID == 111)//PA
			{
				multi = 100;
			}
				poid = value*multi; //poid de la carac
				somme += poid;
		}
		return somme;
	}

	public static int getPoidOfBaseItem(int i)//Donne le poid de l'item actuel
	{
		int poid = 0;
		int somme = 0;
		String NaturalStatsItem = World.database.getOtherData().getNaturalStats(i);

		if(NaturalStatsItem == null || NaturalStatsItem.isEmpty()) return 0;
		String[] splitted = NaturalStatsItem.split(",");
		for(String s : splitted)
		{
			String[] stats = s.split("#");
			int statID = Integer.parseInt(stats[0],16);
			boolean follow = true;
			
			for(int a : Constants.ARMES_EFFECT_IDS)//Si c'est un Effet Actif
				if(a == statID)
					follow = false;
			if(!follow)continue;//Si c'était un effet Actif d'arme
			
			String jet = "";
			int value  = 1;
			try
			{
				jet = stats[4];
				value = Formulas.getRandomJet(jet);
					try
					{
						//on prend le jet max
						int min = Integer.parseInt(stats[1],16);
						int max = Integer.parseInt(stats[2],16);
						value = min;
						if(max != 0)value = max;
					}catch(Exception e){value = Formulas.getRandomJet(jet);};			
			}catch(Exception e){};
			
			int multi = 1;
			if(statID == 118 || statID == 126 || statID == 125 || statID == 119 || statID == 123 || statID == 158 || statID == 174)//Force,Intel,Vita,Agi,Chance,Pod,Initiative
			{
				multi = 1;
			}
			else if(statID == 138 || statID == 666 || statID == 226 || statID == 220)//Domages %,Domage renvoyé,Piège %
			{
				multi = 2;
			}	
			else if(statID == 124 || statID == 176)//Sagesse,Prospec
			{
				multi = 3;
			}
			else if(statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244)//Ré Feu, Air, Eau, Terre, Neutre
			{
				multi = 4;
			}
			else if(statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214)//Ré % Feu, Air, Eau, Terre, Neutre
			{
				multi = 5;
			}
			else if(statID == 225)//Piège
			{
				multi = 15;
			}
			else if(statID == 178 || statID == 112)//Soins,Dommage
			{
				multi = 20;
			}
			else if(statID == 115 || statID == 182)//Cri,Invoc
			{
				multi = 30;
			}
			else if(statID == 117)//PO
			{
				multi = 50;
			}
			else if(statID == 128)//PM
			{
				multi = 90;
			}
			else if(statID == 111)//PA
			{
				multi = 100;
			}
			poid = value*multi; //poid de la carac
			somme +=poid;
		}
		return somme;
	}
	
	public String parseFMStatsString(String statsstr, Object obj, int add, boolean negatif)
	{
		StringBuilder stats = new StringBuilder();
		boolean isFirst = true;
		for(SpellEffect SE : obj.getEffects())
		{
			if(!isFirst)
				stats.append(",");
			
			String[] infos = SE.getArgs().split(";");
			try
			{
				stats.append(Integer.toHexString(SE.getEffectID())).append("#").append(infos[0]).append("#").append(infos[1]).append("#0#").append(infos[5]);
			}catch(Exception e)
			{
				e.printStackTrace();
				continue;
			};
			
			isFirst = false;
		}
		
		for(Entry<Integer,Integer> entry : obj.getStats().getEffects().entrySet())
		{
			if(!isFirst)stats.append(",");
			if(Integer.toHexString(entry.getKey()).compareTo(statsstr) == 0)
			{
				int newstats = 0;
				if(negatif)
				{
					newstats = entry.getValue()-add;
					if(newstats < 1) continue;
				}else
				{
					newstats = entry.getValue()+add;
				}
				String jet = "0d0+"+newstats;
				stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(entry.getValue())).append(add).append("#0#0#").append(jet);
			}
			else
			{
				String jet = "0d0+"+entry.getValue();
				stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(entry.getValue())).append("#0#0#").append(jet);
			}
			isFirst = false;
		}
		
		for(Entry<Integer,String> entry : obj.txtStats.entrySet())
		{
			if(!isFirst)stats.append(",");
			stats.append(Integer.toHexString(entry.getKey())).append("#0#0#0#").append(entry.getValue());
			isFirst = false;
		}
		
		return stats.toString();
	}
	
	public String parseFMEchecStatsString(Object obj, double poid)
	{
		StringBuilder stats = new StringBuilder();
		boolean isFirst = true;
		for(SpellEffect SE : obj.getEffects())
		{
			if(!isFirst)
				stats.append(",");
			
			String[] infos = SE.getArgs().split(";");
			try
			{
				stats.append(Integer.toHexString(SE.getEffectID())).append("#").append(infos[0]).append("#").append(infos[1]).append("#0#").append(infos[5]);
			}catch(Exception e)
			{
				e.printStackTrace();
				continue;
			};
			
			isFirst = false;
		}
		
		for(Entry<Integer,Integer> entry : obj.getStats().getEffects().entrySet())
		{
				//En cas d'echec les stats négatives Chance,Agi,Intel,Force,Portee,Vita augmentes
				int newstats = 0;
				
				if(entry.getKey() == 152 ||
				   entry.getKey() == 154 ||
				   entry.getKey() == 155 ||
				   entry.getKey() == 157 ||
				   entry.getKey() == 116 ||
				   entry.getKey() == 153)
				{
					float a = (float)((entry.getValue()*poid)/100);
					if(a < 1) a = 1;
					float chute = (float)(entry.getValue()+a);
					newstats = (int)Math.floor(chute);
					//On limite la chute du négatif a sont maximum
					if(newstats > Job.getBaseMaxJet(obj.getTemplate().getId(), Integer.toHexString(entry.getKey())))
					{
						newstats = Job.getBaseMaxJet(obj.getTemplate().getId(), Integer.toHexString(entry.getKey()));
					}
				}else
				{
				if(entry.getKey() == 127 || entry.getKey() == 101) continue;//PM, pas de négatif ainsi que PA
				
					float chute = (float)(entry.getValue()-((entry.getValue()*poid)/100));
					newstats = (int)Math.floor(chute);
				}
				if(newstats < 1) continue;
				String jet = "0d0+"+newstats;
				if(!isFirst)stats.append(",");
				stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(newstats)).append("#0#0#").append(jet);
				isFirst = false;
		}
		
		for(Entry<Integer,String> entry : obj.txtStats.entrySet())
		{
			if(!isFirst)stats.append(",");
			stats.append(Integer.toHexString(entry.getKey())).append("#0#0#0#").append(entry.getValue());
			isFirst = false;
		}
		return stats.toString();
	}
	/***********FM SYSTEM***********/
	
	public String parseItem() {
		StringBuilder str = new StringBuilder();
		String posi = this.getPosition() == Constants.ITEM_POS_NO_EQUIPED ? "" : Integer.toHexString(this.getPosition());
		str.append(Integer.toHexString(this.getId())).append("~").append(Integer.toHexString(this.getTemplate().getId())).append("~").append(Integer.toHexString(this.getQuantity())).append("~").append(posi).append("~").append(this.parseStatsString()).append(";");
		return str.toString();
	}
	
	public String parseStringStatsEC_FM(Object obj, double poid) {
		String stats = "";
		boolean first = false;
		for (SpellEffect EH : obj.getEffects()) {
			if (first)
				stats += ",";
			String[] infos = EH.getArgs().split("\\;");
			try {
				stats += Integer.toHexString(EH.getEffectID()) + "#" + infos[0] + "#" + infos[1] + "#0#" + infos[5];
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			first = true;
		}
		for (Entry<Integer, Integer> entry : obj.getStats().getEffects().entrySet()) {
			int newstats = 0;
			int statID = (entry.getKey());
			int value = (entry.getValue());
			if ((statID == 152) || (statID == 154) || (statID == 155) || (statID == 157) || (statID == 116) || (statID == 153))	{
				float a = (float) (value * poid / 100.0D);
				if (a < 1.0F)
					a = 1.0F;
				float chute = value + a;
				newstats = (int) Math.floor(chute);
				if (newstats > Job.getBaseMaxJet(obj.getTemplate().getId(), Integer.toHexString(entry.getKey()))) 
					newstats = Job.getBaseMaxJet(obj.getTemplate().getId(), Integer.toHexString(entry.getKey()));
			} else {
				if ((statID == 127) || (statID == 101))
					continue;
				float chute = (float) (value - value * poid / 100.0D);
				newstats = (int) Math.floor(chute);
			}
			if (newstats < 1)
				continue;
			String jet = "0d0+" + newstats;
			if (first)
				stats += ",";
			stats += Integer.toHexString(statID) + "#" + Integer.toHexString(newstats) + "#0#0#" + jet;
			first = true;
		}
		for (Entry<Integer, String> entry : obj.txtStats.entrySet()) {
			if (first)
				stats += ",";
			stats += Integer.toHexString((entry.getKey())) + "#0#0#0#" + entry.getValue();
			first = true;
		}
		return stats;
	}
	
	public String parseStatsString() {
		if(getTemplate().getType() == 83)	//Si c'est une pierre d'âme vide
			return getTemplate().getStrStats();
		
		StringBuilder stats = new StringBuilder();
		boolean isFirst = true;
		
		for(SpellEffect SE : this.getEffects()) {
			if(!isFirst)
				stats.append(",");
			
			String[] infos = SE.getArgs().split("\\;");
			try {
				stats.append(Integer.toHexString(SE.getEffectID())).append("#").append(infos[0]).append("#").append(infos[1]).append("#0#").append(infos[5]);
			} catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			
			isFirst = false;
		}
		
		for(Entry<Integer,Integer> entry : this.getStats().getEffects().entrySet()) {
			if(!isFirst)
				stats.append(",");
			
			String jet = "0d0+"+entry.getValue();
			stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(entry.getValue())).append("#0#0#").append(jet);
			isFirst = false;
		}
		
		for(Entry<Integer,String> entry : txtStats.entrySet()) {
			if(!isFirst)
				stats.append(",");
			
			if(entry.getKey() == Constants.CAPTURE_MONSTRE)
				stats.append(Integer.toHexString(entry.getKey())).append("#0#0#").append(entry.getValue());	
			else
				stats.append(Integer.toHexString(entry.getKey())).append("#0#0#0#").append(entry.getValue());
			
			isFirst = false;
		}
		return stats.toString();
	}
	
	public void parseStringToStats(String strStats) {
		String[] split = strStats.split(",");
		for(String s : split)
		{	
			try {
				String[] stats = s.split("\\#");
				int id = Integer.parseInt(stats[0],16);
				
				if(id == 997 || id == 996) {
					txtStats.put(id, stats[4]);
					continue;
				}
				//Si stats avec Texte (Signature, apartenance, etc)
				if((!stats[3].equals("") && !stats[3].equals("0")))	{
					txtStats.put(id, stats[3]);
					continue;
				}
				
				boolean follow1 = true;
				switch(id) {
					case 110:
					case 139:
					case 605:
					case 614:
						String min = stats[1];
						String max = stats[2];
						String jet = stats[4];
						String args = min+";"+max+";-1;-1;0;"+jet;
						this.effects.add(new SpellEffect(id, args ,0, -1));
						follow1 = false;
					break;
				}
				if(!follow1)
					continue;
				
				boolean follow2 = true;
				for(int a : Constants.ARMES_EFFECT_IDS) {
					if(a == id)	{
						this.effects.add(new SpellEffect(id, stats[1]+";"+stats[2]+";-1;-1;0;"+stats[4], 0, -1));
						follow2 = false;
					}
				}
				if(!follow2)
					continue;//Si c'était un effet Actif d'arme ou une signature
				this.stats.addOneStat(id, Integer.parseInt(stats[1], 16));
			} catch(Exception e) {
				continue;
			}
		}
		
		/*String[] split = strStats.split("\\,");
		for(String s : split) {	
			try	{
				String[] stats = s.split("\\#");
				int statID = Integer.parseInt(stats[0], 16);
				
				//Stats spécials
				if(statID == 997 || statID == 996) {
					txtStats.put(statID, stats[4]);
					continue;
				}
				//Si stats avec Texte (Signature, apartenance, etc)
				if((!stats[3].equals("") && !stats[3].equals("0")))	{
					txtStats.put(statID, stats[3]);
					continue;
				}
				
				String jet = stats[4];
				boolean follow = true;
				for(int a : Constants.ARMES_EFFECT_IDS) {
					if(a == statID) {
						int id = statID;
						String min = stats[1];
						String max = stats[2];
						String args = min+";"+max+";-1;-1;0;"+jet;
						this.getEffects().add(new SpellEffect(id, args,0,-1));
						follow = false;
					}
				}
				
				if(!follow)
					continue;//Si c'était un effet Actif d'arme ou une signature
				
				int value = Integer.parseInt(stats[1],16);
				this.getStats().addOneStat(statID, value);
			} catch(Exception e) { 
				continue;
			}
		}*/
	}
}
