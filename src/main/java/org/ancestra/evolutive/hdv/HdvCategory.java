package org.ancestra.evolutive.hdv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ancestra.evolutive.hdv.HdvEntry;
import org.ancestra.evolutive.hdv.HdvTemplate;

public class HdvCategory {
	
	private int id;
	private Map<Integer, HdvTemplate> templates = new HashMap<>();//Dans le format <templateID,Template>

	public HdvCategory(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Map<Integer, HdvTemplate> getTemplates() {
		return templates;
	}

	public ArrayList<HdvEntry> getAllEntry() {
		ArrayList<HdvEntry> toReturn = new ArrayList<>();
		
		for(HdvTemplate curTemp : this.getTemplates().values())
			toReturn.addAll(curTemp.getAllEntry());

		return toReturn;
	}
	
	public void addEntry(HdvEntry entry) {
		int tempID = entry.getObject().getTemplate().getID();
		
		if(this.getTemplates().get(tempID) == null)
			addTemplate(tempID, entry);
		else
			this.getTemplates().get(tempID).addEntry(entry);
	}
	
	public boolean delEntry(HdvEntry entry) {
		boolean toReturn = false;
		this.getTemplates().get(entry.getObject().getTemplate().getID()).delEntry(entry);
		
		if((toReturn = this.getTemplates().get(entry.getObject().getTemplate().getID()).isEmpty()))
			delTemplate(entry.getObject().getTemplate().getID());
		
		return toReturn;
	}
	
	public HdvTemplate getTemplate(int id) {
		return this.getTemplates().get(id);
	}
	
	public void addTemplate(int id, HdvEntry entry)	{
		this.getTemplates().put(id, new HdvTemplate(id, entry));
	}
	
	public void delTemplate(int templateID) {
		this.getTemplates().remove(templateID);
	}
	
	public String parseTemplate() {
		boolean isFirst = true;
		String str = "";
		
		for(int template : this.getTemplates().keySet()) {
			if(!isFirst)
				str += ";";
			
			str += template;
			isFirst = false;
		}
		
		return str;
	}	
}