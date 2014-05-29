package org.ancestra.evolutive.area;

import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.map.Maps;

import java.util.ArrayList;

public class Area {
	
	private int id;
	private String name;
	private Continent continent;
	private ArrayList<SubArea> subAreas = new ArrayList<>();
	
	public Area(int id, int continent, String name) {
		this.id = id;
		this.name = name;
		this.continent = World.data.getContinent(continent);
		
		if(this.continent == null) {
			this.continent = new Continent(continent);
			World.data.addContinent(this.continent);
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
	}

	public Continent getContinent() {
		return continent;
	}

	public void setContinent(Continent continent) {
		this.continent = continent;
	}
	
	public void addSubArea(SubArea subArea) {
		this.subAreas.add(subArea);
	}

	public ArrayList<Maps> getMaps() {
		ArrayList<Maps> maps = new ArrayList<>();
		for(SubArea sa : subAreas)
			maps.addAll(sa.getMaps());
		return maps;
	}
}