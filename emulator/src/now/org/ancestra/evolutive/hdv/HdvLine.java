package org.ancestra.evolutive.hdv;

import java.util.ArrayList;
import java.util.Collections;

import org.ancestra.evolutive.object.ObjectType;

public class HdvLine {
	
	private int id;
	private int template;
	private String stats;
	private ArrayList<ArrayList<HdvEntry>> entries = new ArrayList<>(3);//La première ArrayList est un tableau de 3 (0=1 1=10 2=100 de quantité)
	
	public HdvLine(int id, HdvEntry entry) {
		this.id = id;
		this.stats = entry.getObject().parseStatsString();
		this.template = entry.getObject().getTemplate().getId();
		
		for(int i = 0; i < 3; i++)
			entries.add(new ArrayList<HdvEntry>());//Boucle 3 fois pour ajouter 3 List vide dans la SuperList
		
		this.addEntry(entry);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTemplate() {
		return template;
	}

	public void setTemplate(int template) {
		this.template = template;
	}

	public String getStats() {
		return stats;
	}

	public void setStats(String stats) {
		this.stats = stats;
	}

	public ArrayList<ArrayList<HdvEntry>> getEntries() {
		return entries;
	}

	public boolean addEntry(HdvEntry entry) {
		if(!haveSameStats(entry) && !isEmpty())
			return false;
		
		entry.setLine(this.getId());
		byte index = (byte) (entry.getAmount(false) - 1);
		
		this.getEntries().get(index).add(entry);
		this.trier(index);
		return true;
	}
	
	public boolean delEntry(HdvEntry entry)	{
		byte index = (byte) (entry.getAmount(false) - 1);
		boolean toReturn = this.getEntries().get(index).remove(entry);
		
		this.trier(index);
		return toReturn;
	}
	
	public HdvEntry delEntry(byte amount) {
		byte index = (byte) (amount -1);
		HdvEntry toReturn = this.getEntries().get(index).remove(0);
		
		this.trier(index);
		return toReturn;
	}
	
	public boolean haveSameStats(HdvEntry entry) {
		return this.getStats().equalsIgnoreCase(entry.getObject().parseStatsString()) && entry.getObject().getTemplate().getType() != ObjectType.PIERRE_AME_PLEINE;
		//Récupère les stats de l'objet et compare avec ceux de la ligne
	}
	
	public HdvEntry doYouHave(int amount, int price) {
		int index = amount-1;
		for(int i = 0; i < this.getEntries().get(index).size(); i++) 
			if(this.getEntries().get(index).get(i).getPrice() == price)
				return this.getEntries().get(index).get(i);		
		return null;
	}
	
	public int[] getFirsts() {
		int[] toReturn = new int[3];
		
		for (int i = 0; i < this.getEntries().size(); i++) {
			try {
				toReturn[i] = this.getEntries().get(i).get(0).getPrice();//Récupère le premier objet de chaque liste
			} catch(IndexOutOfBoundsException e) {
				toReturn[i] = 0;
			}
		}
		
		return toReturn;
	}
	public ArrayList<HdvEntry> getAll() {//Additionne le nombre d'objet de chaque quantité
		int totalSize = this.getEntries().get(0).size() + this.getEntries().get(1).size() + this.getEntries().get(2).size();
		ArrayList<HdvEntry> toReturn = new ArrayList<HdvEntry>(totalSize);
		
		for (int qte = 0; qte < this.getEntries().size(); qte++)//Boucler dans les quantité
			toReturn.addAll(this.getEntries().get(qte));
		
		return toReturn;
	}
	
	public void trier(byte index) {
		Collections.sort(this.getEntries().get(index));
	}
	
	public boolean isEmpty() {
		for (int i = 0; i < this.getEntries().size(); i++) {
			try {
				if(this.getEntries().get(i).get(0) != null)//Vérifie s'il existe un objet dans chacune des 3 quantité
					return false;
			} catch(IndexOutOfBoundsException e) {}
		}
		return true;
	}
	
	public String parseToEHl() {
		StringBuilder toReturn = new StringBuilder();

		int[] price = getFirsts();
		toReturn.append(this.getId()).append(";").append(this.getStats()).append(";").append((price[0]==0?"":price[0])).append(";").append((price[1]==0?"":price[1])).append(";").append((price[2]==0?"":price[2]));
		
		return toReturn.toString();
	}		
	
	public String parseToEHm() {
		StringBuilder toReturn = new StringBuilder();
		
		int[] prix = getFirsts();
		toReturn.append(this.getId()).append("|").append(this.getTemplate()).append("|").append(this.getStats()).append("|").append((prix[0]==0?"":prix[0])).append("|").append((prix[1]==0?"":prix[1])).append("|").append((prix[2]==0?"":prix[2]));
		
		return toReturn.toString();
	}
}