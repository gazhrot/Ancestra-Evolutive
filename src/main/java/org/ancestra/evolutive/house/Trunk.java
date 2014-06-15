package org.ancestra.evolutive.house;

import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.object.Objet;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Trunk {
	
	private int _id;
	private int _house_id;
	private short _mapid;
	private int _cellid;
	private Map<Integer, Objet> _object = new TreeMap<Integer, Objet>();
	private long _kamas;
	private String _key;
	private int _owner_id;
	
	public Trunk(int id, int house_id, short mapid, int cellid, String object, long kamas, String key, int owner_id)
	{
		_id = id;
		_house_id = house_id;
		_mapid = mapid;
		_cellid = cellid;
		
		for(String item : object.split("\\|"))
		{
			if(item.equals(""))continue;
			String[] infos = item.split(":");
			int guid = Integer.parseInt(infos[0]);

			Objet obj = World.data.getObjet(guid);
			if( obj == null)continue;
			_object.put(obj.getGuid(), obj);
		}

		_kamas = kamas;
		_key = key;
		_owner_id = owner_id;
	}
	
	public int get_id()
	{
		return _id;
	}
	
    public int get_house_id()
    {
            return _house_id;
    }
	
	public int get_mapid()
	{
		return _mapid;
	}
	
	public int get_cellid()
	{
		return _cellid;
	}
	
	public Map<Integer, Objet> get_object()
	{
		return _object;
	}
	
	public long get_kamas()
	{
		return _kamas;
	}
	
	public void set_kamas(long kamas)
	{
		_kamas = kamas;
	}
	
	public String get_key()
	{
		return _key;
	}
	
	public void set_key(String key)
	{
		_key = key;
	}
	
	public int get_owner_id()
	{
		return _owner_id;
	}
	
	public void set_owner_id(int owner_id)
	{
		_owner_id = owner_id;
	}
	
	public void Lock(Player P) 
	{
		SocketManager.GAME_SEND_KODE(P, "CK1|8");
	}
	
	public static Trunk get_trunk_id_by_coord(int map_id, int cell_id)
	{
		for(Entry<Integer, Trunk> trunk : World.data.getTrunks().entrySet())
		{
			if(trunk.getValue().get_mapid() == map_id && trunk.getValue().get_cellid() == cell_id)
			{
				return trunk.getValue();
			}
		}
		return null;
	}
	
	public static void LockTrunk(Player P, String packet) 
	{
		Trunk t = P.getCurTrunk();
		if(t == null) return;
		if(t.isTrunk(P, t))
		{
			World.database.getTrunkData().update(P, t, packet);//Change le code
			t.set_key(packet);
			closeCode(P);
		}else
		{
			closeCode(P);
		}
		P.setCurTrunk(null);
		return;
	}
	
	public void HopIn(Player P)//Ouvrir coffre
	{
		// En gros si il fait quelque chose :)
		if(P.getFight() != null ||
		   P.getIsTalkingWith() != 0 ||
		   P.getIsTradingWith() != 0 ||
		   P.getCurJobAction() != null ||
		   P.getCurExchange() != null)
		{
			return;
		}
		
		Trunk t = P.getCurTrunk();
		House h = World.data.getHouse(_house_id);
		
		if(t == null) return;
		if(t.get_owner_id() == P.getAccount().getUUID() || (P.getGuild() == null ? false : P.getGuild().getId() == h.get_guild_id() && h.canDo(Constants.C_GNOCODE)))
		{
			OpenTrunk(P, "-", true);
		}
		else if(P.getGuild() == null && h.canDo(Constants.C_OCANTOPEN))//si on compare par id �a bug (guild null)
		{
			SocketManager.GAME_SEND_MESSAGE(P, "Ce coffre ne peut �tre ouvert que par les membres de la guilde !", Server.config.getMotdColor());
		return;
		}
		else if(t.get_owner_id() > 0)//Une personne autre le poss�de, il faut le code pour rentrer
		{
			SocketManager.GAME_SEND_KODE(P, "CK0|8");//8 �tant le nombre de chiffre du code
		}
		else if(t.get_owner_id() == 0)//Coffre a personne
		{
			return;
		}else
		{
			return;
		}
	}
	
	public static void OpenTrunk(Player P, String packet, boolean isTrunk)//Ouvrir un coffre
	{	
		Trunk t = P.getCurTrunk();
		if(t == null) return;
		
		if(packet.compareTo(t.get_key()) == 0 || isTrunk)//Si c'est chez lui ou que le mot de passe est bon
		{
			SocketManager.GAME_SEND_ECK_PACKET(P.getAccount().getGameClient(), 5, "");
			SocketManager.GAME_SEND_EL_TRUNK_PACKET(P, t);
			closeCode(P);
		}
		
		else if(packet.compareTo(t.get_key()) != 0)//Mauvais code
		{
			SocketManager.GAME_SEND_KODE(P, "KE");
			closeCode(P);
			P.setCurTrunk(null);
		}
	}
	
	public static void closeCode(Player P)
	{
		SocketManager.GAME_SEND_KODE(P, "V");
	}
	
	public boolean isTrunk(Player P, Trunk t)//Savoir si c'est son coffre
	{
		if(t.get_owner_id() == P.getAccount().getUUID()) return true;
		else return false;
	}
	
    public static ArrayList<Trunk> getTrunksByHouse(House h)
    {
            ArrayList<Trunk> trunks = new ArrayList<Trunk>();
            for(Entry<Integer, Trunk> trunk : World.data.getTrunks().entrySet())
            {
                    if(trunk.getValue().get_house_id() == h.get_id())
                    {
                            trunks.add(trunk.getValue());
                    }
            }
           
            return trunks;
    }
    
	public String parseToTrunkPacket()
	{
		StringBuilder packet = new StringBuilder();
		for(Objet obj : _object.values())
			packet.append("O").append(obj.parseItem()).append(";");
		if(get_kamas() != 0)
			packet.append("G").append(get_kamas());
		return packet.toString();
	}
	
	public void addInTrunk(int guid, int qua, Player P)
	{
		if(P.getCurTrunk().get_id() != get_id()) return;
		
		if(_object.size() >= 80) // Le plus grand c'est pour si un admin ajoute des objets via la bdd...
		{
			SocketManager.GAME_SEND_MESSAGE(P, "Le nombre d'objets maximal de ce coffre � �t� atteint !", Server.config.getMotdColor());
			return;
		}
		
		Objet PersoObj = World.data.getObjet(guid);
		if(PersoObj == null) return;
		//Si le joueur n'a pas l'item dans son sac ...
		if(P.getItems().get(guid) == null)
		{
			Log.addToLog("Le joueur "+P.getName()+" a tenter d'ajouter un objet dans un coffre qu'il n'avait pas.");
			return;
		}
		
		String str = "";
		
		//Si c'est un item �quip� ...
		if(PersoObj.getPosition() != Constants.ITEM_POS_NO_EQUIPED)return;
		
		Objet TrunkObj = getSimilarTrunkItem(PersoObj);
		int newQua = PersoObj.getQuantity() - qua;
		if(TrunkObj == null)//S'il n'y pas d'item du meme Template
		{
			//S'il ne reste pas d'item dans le sac
			if(newQua <= 0)
			{
				//On enleve l'objet du sac du joueur
				P.removeItem(PersoObj.getGuid());
				//On met l'objet du sac dans le coffre, avec la meme quantit�
				_object.put(PersoObj.getGuid() ,PersoObj);
				str = "O+"+PersoObj.getGuid()+"|"+PersoObj.getQuantity()+"|"+PersoObj.getTemplate().getID()+"|"+PersoObj.parseStatsString();
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(P, guid);
				
			}
			else//S'il reste des objets au joueur
			{
				//on modifie la quantit� d'item du sac
				PersoObj.setQuantity(newQua);
				//On ajoute l'objet au coffre et au monde
				TrunkObj = Objet.getCloneObjet(PersoObj, qua);
				World.data.addObjet(TrunkObj, true);
				_object.put(TrunkObj.getGuid() ,TrunkObj);
				
				//Envoie des packets
				str = "O+"+TrunkObj.getGuid()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getID()+"|"+TrunkObj.parseStatsString();
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P, PersoObj);
				
			}
		}else // S'il y avait un item du meme template
		{
			//S'il ne reste pas d'item dans le sac
			if(newQua <= 0)
			{
				//On enleve l'objet du sac du joueur
				P.removeItem(PersoObj.getGuid());
				//On enleve l'objet du monde
				World.data.removeItem(PersoObj.getGuid());
				//On ajoute la quantit� a l'objet dans le coffre
				TrunkObj.setQuantity(TrunkObj.getQuantity() + PersoObj.getQuantity());
				//on envoie l'ajout au coffre de l'objet
			    str = "O+"+TrunkObj.getGuid()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getID()+"|"+TrunkObj.parseStatsString();
				//on envoie la supression de l'objet du sac au joueur
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(P, guid);
				
			}else //S'il restait des objets
			{
				//on modifie la quantit� d'item du sac
				PersoObj.setQuantity(newQua);
				TrunkObj.setQuantity(TrunkObj.getQuantity() + qua);
				str = "O+"+TrunkObj.getGuid()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getID()+"|"+TrunkObj.parseStatsString();
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P, PersoObj);
				
			}
		}
		
		for(Player perso : P.getMap().getPlayers())
		{
			if(perso.getCurTrunk() != null && get_id() == perso.getCurTrunk().get_id())
			{
				SocketManager.GAME_SEND_EsK_PACKET(perso, str);
			}
		}
		
		SocketManager.GAME_SEND_Ow_PACKET(P);
		World.database.getTrunkData().update(this);
	}
	
	public void removeFromTrunk(int guid, int qua, Player P)
	{
		if(P.getCurTrunk().get_id() != get_id()) return;
		
		Objet TrunkObj = World.data.getObjet(guid);
		if(TrunkObj == null) return;
		//Si le joueur n'a pas l'item dans son coffre
		if(_object.get(guid) == null)
		{
			Log.addToLog("Le joueur "+P.getName()+" a tenter de retirer un objet dans un coffre qu'il n'avait pas.");
			return;
		}
		
		Objet PersoObj = P.getSimilarItem(TrunkObj);
		
		String str = "";
		
		int newQua = TrunkObj.getQuantity() - qua;
		
		if(PersoObj == null)//Si le joueur n'avait aucun item similaire
		{
			//S'il ne reste rien dans le coffre
			if(newQua <= 0)
			{
				//On retire l'item du coffre
				_object.remove(guid);
				//On l'ajoute au joueur
				P.getItems().put(guid, TrunkObj);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OAKO_PACKET(P,TrunkObj);
				str = "O-"+guid;
				
			}else //S'il reste des objets dans le coffre
			{
				//On cr�e une copy de l'item dans le coffre
				PersoObj = Objet.getCloneObjet(TrunkObj, qua);
				//On l'ajoute au monde
				World.data.addObjet(PersoObj, true);
				//On retire X objet du coffre
				TrunkObj.setQuantity(newQua);
				//On l'ajoute au joueur
				P.getItems().put(PersoObj.getGuid(), PersoObj);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OAKO_PACKET(P,PersoObj);
				str = "O+"+TrunkObj.getGuid()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getID()+"|"+TrunkObj.parseStatsString();
				
			}
		}
		else
		{
			//S'il ne reste rien dans le coffre
			if(newQua <= 0)
			{
				//On retire l'item du coffre
				_object.remove(TrunkObj.getGuid());
				World.data.removeItem(TrunkObj.getGuid());
				//On Modifie la quantit� de l'item du sac du joueur
				PersoObj.setQuantity(PersoObj.getQuantity() + TrunkObj.getQuantity());
				
				//On envoie les packets
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P, PersoObj);
				str = "O-"+guid;
				
			}
			else//S'il reste des objets dans le coffre
			{
				//On retire X objet du coffre
				TrunkObj.setQuantity(newQua);
				//On ajoute X objets au joueurs
				PersoObj.setQuantity(PersoObj.getQuantity() + qua);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P,PersoObj);
				str = "O+"+TrunkObj.getGuid()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getID()+"|"+TrunkObj.parseStatsString();
			}
		}
		
		for(Player perso : P.getMap().getPlayers())
		{
			if(perso.getCurTrunk() != null && get_id() == perso.getCurTrunk().get_id())
			{
				SocketManager.GAME_SEND_EsK_PACKET(perso, str);
			}
		}
		
		SocketManager.GAME_SEND_Ow_PACKET(P);
		World.database.getTrunkData().update(this);
	}
	
	private Objet getSimilarTrunkItem(Objet obj)
	{
		for(Objet value : _object.values())
		{
			if(value.getTemplate().getType() == 85)
				continue;
			if(value.getTemplate().getID() == obj.getTemplate().getID() && value.getStats().isSameStats(obj.getStats()))
				return value;
		}
		return null;
	}
	
	public String parseTrunkObjetsToDB()
	{
		StringBuilder str = new StringBuilder();
		for(Entry<Integer,Objet> entry : _object.entrySet())
		{
			Objet obj = entry.getValue();
			str.append(obj.getGuid()).append("|");
		}
		return str.toString();
	}
	
	public void purgeTrunk()
	{
		for(Entry<Integer, Objet> obj : get_object().entrySet())
		{
			World.data.removeItem(obj.getKey());
		}
		get_object().clear();
	}
	
	public void moveTrunktoBank(Account Cbank)
	{
		for(Entry<Integer, Objet> obj : get_object().entrySet())
		{
			Cbank.getBank().put(obj.getKey(), obj.getValue());
		}
		get_object().clear();
	}
}