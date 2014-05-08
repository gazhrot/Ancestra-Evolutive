package org.ancestra.evolutive.objects;

import java.util.ArrayList;

import org.ancestra.evolutive.core.World;

import org.ancestra.evolutive.objects.Area;
import org.ancestra.evolutive.objects.Carte;



public class SubArea
{
	private int _id;
	private Area _area;
	private int _alignement;
	private String _name;
	private ArrayList<Carte> _maps = new ArrayList<Carte>();
	
	public SubArea(int id, int areaID, int alignement,String name)
	{
		_id = id;
		_name = name;
		_area =  World.data.getArea(areaID);
		_alignement = alignement;
	}
	
	public String get_name()
	{
		return _name;
	}
	public int get_id() {
		return _id;
	}
	public Area get_area() {
		return _area;
	}
	public int get_alignement() {
		return _alignement;
	}
	public ArrayList<Carte> getMaps() {
		return _maps;
	}

	public void addMap(Carte carte)
	{
		_maps.add(carte);
	}
	
}