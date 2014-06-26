package org.ancestra.evolutive.area;

import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.map.Maps;

import java.util.ArrayList;

public class SubArea {
	
	private int id;
	private String name;
	private Alignement alignement;
	private Area area;
	private ArrayList<Maps> maps = new ArrayList<>();
	
	public SubArea(int id, int area, int alignement, String name) {
		this.id = id;
		this.name = name;
		this.area = World.data.getArea(area);
		this.alignement = Alignement.getAlignement(alignement);
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

	public Alignement getAlignement() {
		return alignement;
	}

	public void setAlignement(Alignement alignement) {
		this.alignement = alignement;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public ArrayList<Maps> getMaps() {
		return maps;
	}

	public void addMap(Maps map) {
		this.maps.add(map);
	}		
}