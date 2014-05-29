package org.ancestra.evolutive.area;

import java.util.ArrayList;

public class Continent {
	
	private int id;
	private ArrayList<Area> areas = new ArrayList<>();
	
	public Continent(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Area> getAreas() {
		return areas;
	}

	public void addArea(Area area) {
		this.areas.add(area);
	}	
}