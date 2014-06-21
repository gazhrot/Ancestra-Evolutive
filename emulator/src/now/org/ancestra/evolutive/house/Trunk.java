package org.ancestra.evolutive.house;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.house.Trunk;

import org.ancestra.evolutive.object.Object;

public class Trunk {
	
	private int id;
	private int house;
	private int owner;
	private String key;
	private short mapid;
	private int cellid;
	private long kamas;	
	private Map<Integer, Object> objects = new TreeMap<>();
	
	public Trunk(int id, int house, short mapid, int cellid, String objects, long kamas, String key, int owner) {
		this.id = id;
		this.house = house;
		this.mapid = mapid;
		this.cellid = cellid;
		this.kamas = kamas;
		this.key = key;
		this.owner = owner;
		
		for(String object: objects.split("\\|")) {
			if(object.equals(""))
				continue;
			
			String[] infos = object.split("\\:");
			int guid = Integer.parseInt(infos[0]);

			Object obj = World.data.getObject(guid);
			
			if(obj == null)
				continue;
			
			this.objects.put(obj.getId(), obj);
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHouse() {
		return house;
	}

	public void setHouse(int house) {
		this.house = house;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public short getMapid() {
		return mapid;
	}

	public void setMapid(short mapid) {
		this.mapid = mapid;
	}

	public int getCellid() {
		return cellid;
	}

	public void setCellid(int cellid) {
		this.cellid = cellid;
	}

	public long getKamas() {
		return kamas;
	}

	public void setKamas(long kamas) {
		this.kamas = kamas;
	}

	public Map<Integer, Object> getObjects() {
		return objects;
	}

	public void lock(Player player) {
		SocketManager.GAME_SEND_KODE(player, "CK1|8");
	}
	
	public static void lock(Player player, String packet) {
		Trunk trunk = player.getCurTrunk();
		if(trunk == null) 
			return;
		if(trunk.isTrunk(player, trunk)) {
			World.database.getTrunkData().update(player, trunk, packet);
			trunk.setKey(packet);
			closeCode(player);
		} else {
			closeCode(player);
		}
		player.setCurTrunk(null);
		return;
	}
	
	public static Trunk getTrunkByPos(int mapid, int cellid) {
		for(Entry<Integer, Trunk> trunk : World.data.getTrunks().entrySet())
			if(trunk.getValue().getMapid() == mapid && trunk.getValue().getCellid() == cellid)
				return trunk.getValue();
		return null;
	}
		
	public void open(Player player) {//Ouvrir coffre
		if(player.getFight() != null || player.getIsTalkingWith() != 0 || player.getIsTradingWith() != 0 || player.getCurJobAction() != null || player.getCurExchange() != null)
			return;
		
		House house = World.data.getHouse(this.getHouse());
		Trunk trunk = player.getCurTrunk();
		
		if(trunk == null) 
			return;
		
		if(trunk.getOwner() == player.getAccount().getUUID() || (player.getGuild() == null ? false : player.getGuild().getId() == house.getGuildId() && house.canDo(Constants.C_GNOCODE))) {
			open(player, "-", true);
	    } else 
	    if(player.getGuild() == null && house.canDo(Constants.C_OCANTOPEN)) {//si on compare par id ça bug (guild null)
			SocketManager.GAME_SEND_MESSAGE(player, "Ce coffre ne peut être ouvert que par les membres de la guilde !", Server.config.getMotdColor());
			return;
		} else
		if(trunk.getOwner() > 0) {//Une personne autre le possède, il faut le code pour rentrer
			SocketManager.GAME_SEND_KODE(player, "CK0|8");//8 étant le nombre de chiffre du code
		} else
		if(trunk.getOwner() == 0) {//Coffre a personne
			return;
		} else {
			return;
		}
	}
	
	public static void open(Player player, String packet, boolean isTrunk) {//Ouvrir un coffre
		Trunk trunk = player.getCurTrunk();
		
		if(trunk == null) 
			return;
		
		if(packet.compareTo(trunk.getKey()) == 0 || isTrunk) {//Si c'est chez lui ou que le mot de passe est bon
			SocketManager.GAME_SEND_ECK_PACKET(player.getAccount().getGameClient(), 5, "");
			SocketManager.GAME_SEND_EL_TRUNK_PACKET(player, trunk);
			closeCode(player);
		}else
		if(packet.compareTo(trunk.getKey()) != 0) {//Mauvais code
			SocketManager.GAME_SEND_KODE(player, "KE");
			Trunk.closeCode(player);
			player.setCurTrunk(null);
		}
	}
	
	public static void closeCode(Player player) {
		SocketManager.GAME_SEND_KODE(player, "V");
	}
	
	public boolean isTrunk(Player player, Trunk trunk) {//Savoir si c'est son coffre
		if(trunk.getOwner() == player.getAccount().getUUID()) 
			return true;
		else 
			return false;
	}
	
    public static ArrayList<Trunk> getTrunksByHouse(House house)
    {
    	ArrayList<Trunk> trunks = new ArrayList<Trunk>();
    	for(Entry<Integer, Trunk> trunk: World.data.getTrunks().entrySet())
    		if(trunk.getValue().getHouse() == house.getId())
    			trunks.add(trunk.getValue());           
    	return trunks;
    }
    	
	public void addInTrunk(int id, int qua, Player player) {
		if(player.getCurTrunk().getId() != this.getId()) 
			return;
		
		if(this.getObjects().size() >= 80) {// Le plus grand c'est pour si un admin ajoute des objets via la bdd...
			SocketManager.GAME_SEND_MESSAGE(player, "Le nombre d'objets maximal de ce coffre à été atteint !", Server.config.getMotdColor());
			return;
		}
		
		Object object = World.data.getObject(id);
		
		if(object == null) 
			return;

		if(player.getItems().get(id) == null) {
			Log.addToLog("Le joueur "+player.getName()+" a tenter d'ajouter un objet dans un coffre qu'il n'avait pas.");
			return;
		}
		
		String str = "";
		
		//Si c'est un item équipé ...
		if(object.getPosition() != Constants.ITEM_POS_NO_EQUIPED)
			return;
		
		Object TrunkObj = this.getSimilarTrunkItem(object);
		int newQua = object.getQuantity() - qua;
		
		if(TrunkObj == null) {//S'il n'y pas d'item du meme Template
			//S'il ne reste pas d'item dans le sac
			if(newQua <= 0) {
				//On enleve l'objet du sac du joueur
				player.removeItem(object.getId());
				//On met l'objet du sac dans le coffre, avec la meme quantité
				this.getObjects().put(object.getId() ,object);
				str = "O+"+object.getId()+"|"+object.getQuantity()+"|"+object.getTemplate().getId()+"|"+object.parseStatsString();
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, id);
				
			} else {//S'il reste des objets au joueur
				//on modifie la quantité d'item du sac
				object.setQuantity(newQua);
				//On ajoute l'objet au coffre et au monde
				TrunkObj = Object.getClone(object, qua);
				World.data.addObject(TrunkObj, true);
				this.getObjects().put(TrunkObj.getId() ,TrunkObj);
				
				//Envoie des packets
				str = "O+"+TrunkObj.getId()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getId()+"|"+TrunkObj.parseStatsString();
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(player, object);
				
			}
		} else {// S'il y avait un item du meme template
			//S'il ne reste pas d'item dans le sac
			if(newQua <= 0)	{
				//On enleve l'objet du sac du joueur
				player.removeItem(object.getId());
				//On enleve l'objet du monde
				World.data.removeObject(object.getId());
				//On ajoute la quantité a l'objet dans le coffre
				TrunkObj.setQuantity(TrunkObj.getQuantity() + object.getQuantity());
				//on envoie l'ajout au coffre de l'objet
			    str = "O+"+TrunkObj.getId()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getId()+"|"+TrunkObj.parseStatsString();
				//on envoie la supression de l'objet du sac au joueur
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, id);
				
			} else {//S'il restait des objets
				//on modifie la quantité d'item du sac
				object.setQuantity(newQua);
				TrunkObj.setQuantity(TrunkObj.getQuantity() + qua);
				str = "O+"+TrunkObj.getId()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getId()+"|"+TrunkObj.parseStatsString();
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(player, object);
			}
		}
		
		for(Player perso: player.getMap().getPlayers())
			if(perso.getCurTrunk() != null && getId() == perso.getCurTrunk().getId())
				SocketManager.GAME_SEND_EsK_PACKET(perso, str);
		
		SocketManager.GAME_SEND_Ow_PACKET(player);
		World.database.getTrunkData().update(this);
	}
	
	public void removeFromTrunk(int id, int qua, Player player) {
		if(player.getCurTrunk().getId() != this.getId()) 
			return;
		
		Object object = World.data.getObject(id);
		
		if(object == null) 
			return;
		
		//Si le joueur n'a pas l'item dans son coffre
		if(this.getObjects().get(id) == null) {
			Log.addToLog("Le joueur "+player.getName()+" a tenter de retirer un objet dans un coffre qu'il n'avait pas.");
			return;
		}
		
		Object object2 = player.getSimilarItem(object);
		
		String str = "";
		
		int newQua = object.getQuantity() - qua;
		
		if(object2 == null) {
			//S'il ne reste rien dans le coffre
			if(newQua <= 0) {
				//On retire l'item du coffre
				this.getObjects().remove(id);
				//On l'ajoute au joueur
				player.getItems().put(id, object);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OAKO_PACKET(player, object);
				str = "O-"+id;
				
			} else {
				//On crée une copy de l'item dans le coffre
				object2 = Object.getClone(object, qua);
				//On l'ajoute au monde
				World.data.addObject(object2, true);
				//On retire X objet du coffre
				object.setQuantity(newQua);
				//On l'ajoute au joueur
				player.getItems().put(object2.getId(), object2);
				//On envoie les packets
				SocketManager.GAME_SEND_OAKO_PACKET(player, object2);
				str = "O+"+object.getId()+"|"+object.getQuantity()+"|"+object.getTemplate().getId()+"|"+object.parseStatsString();
			}
		} else {
			//S'il ne reste rien dans le coffre
			if(newQua <= 0) {
				//On retire l'item du coffre
				this.getObjects().remove(object.getId());
				World.data.removeObject(object.getId());
				//On Modifie la quantité de l'item du sac du joueur
				object2.setQuantity(object2.getQuantity() + object.getQuantity());	
				//On envoie les packets
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(player, object2);
				str = "O-"+id;	
			} else {//S'il reste des objets dans le coffre
				//On retire X objet du coffre
				object.setQuantity(newQua);
				//On ajoute X objets au joueurs
				object2.setQuantity(object2.getQuantity() + qua);
				//On envoie les packets
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(player, object2);
				str = "O+"+object.getId()+"|"+object.getQuantity()+"|"+object.getTemplate().getId()+"|"+object.parseStatsString();
			}
		}
		
		for(Player p: player.getMap().getPlayers())
			if(p.getCurTrunk() != null && getId() == p.getCurTrunk().getId())
				SocketManager.GAME_SEND_EsK_PACKET(p, str);
		
		SocketManager.GAME_SEND_Ow_PACKET(player);
		World.database.getTrunkData().update(this);
	}
	
	private Object getSimilarTrunkItem(Object object) {
		for(Object value : this.getObjects().values()) {
			if(value.getTemplate().getType() == 85)
				continue;
			if(value.getTemplate().getId() == object.getTemplate().getId() && value.getStats().isSameStats(object.getStats()))
				return value;
		}
		return null;
	}
	
	public void purgeTrunk() {
		for(Entry<Integer, Object> obj : this.getObjects().entrySet())
			World.data.removeObject(obj.getKey());
		this.getObjects().clear();
	}
	
	public void moveTrunkToBank(Account account) {
		for(Entry<Integer, Object> obj : this.getObjects().entrySet())
			account.getBank().put(obj.getKey(), obj.getValue());
		this.getObjects().clear();
	}
	
	public String parseTrunkObjectsToDB() {
		StringBuilder str = new StringBuilder();
		for(Entry<Integer, Object> entry : this.getObjects().entrySet()) 
			str.append(entry.getValue().getId()).append("|");
		return str.toString();
	}
	
	public String parseToTrunkPacket() {
		StringBuilder packet = new StringBuilder();
		for(Object object: this.getObjects().values())
			packet.append("O").append(object.parseItem()).append(";");
		if(this.getKamas() != 0)
			packet.append("G").append(this.getKamas());
		return packet.toString();
	}
}