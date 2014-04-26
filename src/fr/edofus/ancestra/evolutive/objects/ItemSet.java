package fr.edofus.ancestra.evolutive.objects;

import java.util.ArrayList;



import fr.edofus.ancestra.evolutive.client.Player.Stats;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.objects.Objet.ObjTemplate;


public class ItemSet
{
	private int _id;
	private ArrayList<ObjTemplate> _itemTemplates = new ArrayList<>();
	private ArrayList<Stats> _bonuses = new ArrayList<>();
	
	public ItemSet (int id,String items, String bonuses)
	{
		_id = id;
		//parse items String
		for(String str : items.split(","))
		{
			try
			{
				ObjTemplate t = World.data.getObjTemplate(Integer.parseInt(str.trim()));
				if(t == null)continue;
				_itemTemplates.add(t);
			}catch(Exception e){};
		}
		
		//on ajoute un bonus vide pour 1 item
		_bonuses.add(new Stats());
		//parse bonuses String
		for(String str : bonuses.split(";"))
		{
			Stats S = new Stats();
			//s�paration des bonus pour un m�me nombre d'item
			for(String str2 : str.split(","))
			{
				try
				{
					String[] infos = str2.split(":");
					int stat = Integer.parseInt(infos[0]);
					int value = Integer.parseInt(infos[1]);
					//on ajoute a la stat
					S.addOneStat(stat, value);
				}catch(Exception e){};
			}
			//on ajoute la stat a la liste des bonus
			_bonuses.add(S);
		}
	}

	public int getId()
	{
		return _id;
	}
	
	public Stats getBonusStatByItemNumb(int numb)
	{
		if(numb>_bonuses.size())return new Stats();
		return _bonuses.get(numb-1);
	}
	
	public ArrayList<ObjTemplate> getItemTemplates()
	{
		return _itemTemplates;
	}
}