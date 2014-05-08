package org.ancestra.evolutive.objects;

import java.util.ArrayList;

import org.ancestra.evolutive.core.World;

import org.ancestra.evolutive.objects.Carte;
import org.ancestra.evolutive.objects.SubArea;
import org.ancestra.evolutive.objects.SuperArea;




public class Area
{
	private int _id;
	private SuperArea _superArea;
	private String _name;
	private ArrayList<SubArea> _subAreas = new ArrayList<SubArea>();
	
	public Area(int id, int superArea,String name)
	{
		_id = id;
		_name = name;
		_superArea = World.data.getSuperArea(superArea);
		//Si le continent n'est pas encore cr�er, on le cr�er et on l'ajoute au monde
		if(_superArea == null)
		{
			_superArea = new SuperArea(superArea);
			World.data.addSuperArea(_superArea);
		}
	}
	public String get_name()
	{
		return _name;
	}
	public int get_id()
	{
		return _id;
	}
	
	public SuperArea get_superArea()
	{
		return _superArea;
	}
	
	public void addSubArea(SubArea sa)
	{
		_subAreas.add(sa);
	}
	
	public ArrayList<Carte> getMaps()
	{
		ArrayList<Carte> maps = new ArrayList<Carte>();
		for(SubArea SA : _subAreas)maps.addAll(SA.getMaps());
		return maps;
	}
}