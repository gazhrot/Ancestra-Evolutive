package org.ancestra.evolutive.object;

import java.util.ArrayList;

import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.object.ObjectTemplate;

public class ObjectSet {
	
	private int id;
	private ArrayList<ObjectTemplate> templates = new ArrayList<>();
	private ArrayList<Stats> bonus = new ArrayList<>();
	
	public ObjectSet (int id, String templates, String bonuses) {
		this.id = id;

		for(String str : templates.split("\\,")) {
			try	{
				ObjectTemplate template = World.data.getObjectTemplate(Integer.parseInt(str.trim()));
				if(template == null)
					continue;
				this.templates.add(template);
			} catch(Exception e) {}
		}
		
		this.bonus.add(new Stats());
		
		for(String str : bonuses.split("\\;")) {
			Stats stats = new Stats();
			for(String str2 : str.split("\\,")) {
				try	{
					String[] infos = str2.split("\\:");
					int stat = Integer.parseInt(infos[0]);
					int value = Integer.parseInt(infos[1]);

					stats.addOneStat(stat, value);
				} catch(Exception e) {}
			}
			//on ajoute la stat a la liste des bonus
			this.bonus.add(stats);
		}
	}

	public int getId() {
		return id;
	}
	
	public ArrayList<ObjectTemplate> getItemTemplates() {
		return templates;
	}
	
	public Stats getBonusStatByItemNumb(int id) {
		if(id > this.bonus.size())
			return new Stats();
		return bonus.get(id - 1);
	}
}