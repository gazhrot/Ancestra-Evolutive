package org.ancestra.evolutive.hdv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ancestra.evolutive.core.World;

public class HdvTemplate {
	
	private int id;
	private Map<Integer, HdvLine> lines = new HashMap<>();
	
	public HdvTemplate(int id, HdvEntry entry) {
		this.id = id;
		this.addEntry(entry);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Map<Integer, HdvLine> getLines() {
		return lines;
	}
	
	public HdvLine getLine(int id) {
		return lines.get(id);
	}

	@SuppressWarnings("deprecation")
	public void addEntry(HdvEntry entry) {
		for(HdvLine curLine : this.getLines().values())//Boucle dans toutes les lignes pour essayer de trouver des objets de mêmes stats
			if(curLine.addEntry(entry))//Si une ligne l'accepte, arrête la méthode.
				return;

		int id = World.data.getNextLigneID();
		this.getLines().put(id, new HdvLine(id, entry));
	}
	
	
	public boolean delEntry(HdvEntry entry) {
		boolean toReturn =  this.getLines().get(entry.getLine()).delEntry(entry);
		
		if(this.getLines().get(entry.getLine()).isEmpty())
			this.getLines().remove(entry.getLine());
		
		return toReturn;
	}
	
	public ArrayList<HdvEntry> getAllEntry() {
		ArrayList<HdvEntry> toReturn = new ArrayList<HdvEntry>();
		
		for(HdvLine curLine : this.getLines().values())
			toReturn.addAll(curLine.getAll());
		
		return toReturn;
	}
	
	public boolean isEmpty() {
		if(this.getLines().size() == 0)
			return true;
		return false;
	}
	
	public String parseToEHl() {
		String toReturn = this.getId() + "|";
		
		boolean isFirst = true;
		for(HdvLine curLine : this.getLines().values())	{
			if(!isFirst)
				toReturn += "|";
				
			toReturn += curLine.parseToEHl();
			isFirst = false;
		}
		return toReturn;
	}
}