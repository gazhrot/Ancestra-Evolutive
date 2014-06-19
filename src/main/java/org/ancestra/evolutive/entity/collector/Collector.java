package org.ancestra.evolutive.entity.collector;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Creature;
import org.ancestra.evolutive.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.object.Object;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Collector extends Creature {
	private int _GuildID = 0;

    private final Guild guild;
	private final short firstNameId;
	private final short lastNameId;
	private byte _inFight = 0;
	private int _inFightID = -1;
	private Map<Integer, Object> _objets = new TreeMap<>();
	private long _kamas = 0;
	private long _xp = 0;
	private boolean _inExchange = false;
	//Timer
	private int _timeTurn = 45000;
	//Les logs
	private Map<Integer, Object> _LogObjects = new TreeMap<>();
	private long _LogXP = 0;
	
	public Collector(int id, int map, int cellID, byte orientation, int GuildID,
			short N1, short N2, String items, long kamas, long xp) {
		super(id,Short.toString(N1) +","+ Short.toString(N2),map,cellID);
		_GuildID = GuildID;
        helper = new CollectorHelper(this);
        guild = World.data.getGuild(_GuildID);
		firstNameId = N1;
		lastNameId = N2;
		//Mise en place de son inventaire
		for(String item : items.split("\\|"))
		{
			if(item.equals(""))continue;
			String[] infos = item.split(":");
			int objectId = Integer.parseInt(infos[0]);
			Object obj = World.data.getObject(objectId);
			if(obj == null)continue;
			_objets.put(objectId, obj);

		}
		_xp = xp;
		_kamas = kamas;
	}
	
	public long getKamas() 
	{
		return _kamas;
	}
	
	public void setKamas(long kamas) 
	{
		this._kamas = kamas;
	}
	
	public long getXp() 
	{
		return _xp;
	}
	
	public void setXp(long xp) 
	{
		this._xp = xp;
	}
	
	public Map<Integer, Object> getObjects() 
	{
		return _objets;
	}
	
	public void removeObject(int guid)
	{
		_objets.remove(guid);
	}
	
	public boolean HaveObject(int guid)
	{
        return _objets.get(guid) != null;
	}
	
	public void remove_timeTurn(int time)
	{
		_timeTurn -= time;
	}
	
	public void set_timeTurn(int time)
	{
		_timeTurn = time;
	}
	
	public int get_turnTimer()
	{
		return _timeTurn;
	}
	
	public int get_guildID() {
		return _GuildID;
	}
	
	public void DelPerco(int percoGuid){
		for(Object obj : _objets.values()){
			//On supprime les objets non ramasser/drop
			World.data.removeObject(obj.getId());
		}
		World.data.getPercos().remove(percoGuid);
	}
	
	public int get_inFight()
	{
		return _inFight;
	}
	
	public void set_inFight(byte fight)
	{
		_inFight = fight;
	}
	
	public void set_inFightID(int ID)
	{
		_inFightID = ID;
	}
	
	public int get_inFightID()
	{
		return _inFightID;
	}

	
	public static String parsetoGuild(int GuildID)
	{
		StringBuilder packet = new StringBuilder();
		boolean isFirst = true;
		for(Entry<Integer, Collector> perco : World.data.getPercos().entrySet())
		{
			 if(perco.getValue().get_guildID() == GuildID)
    		 {
				 	Maps map = World.data.getMap(perco.getValue().getMap().getId());
				 	if(isFirst) packet.append("+");
	    			if(!isFirst) packet.append("|");
	    			packet.append(perco.getValue().getId()).append(";").append(perco.getValue().getFirstNameId()).append(",").append(perco.getValue().getLastNameId()).append(";");
	    			
	    			packet.append(Integer.toString(map.getId(), 36)).append(",").append(map.getX()).append(",").append(map.getY()).append(";");
	    			packet.append(perco.getValue().get_inFight()).append(";");
	    			if(perco.getValue().get_inFight() == 1)
	    			{
	    				if(map.getFights().get(perco.getValue().get_inFightID()) == null)
	    				{
	    					packet.append("45000;");//TimerActuel
	    				}else
	    				{
	    					packet.append(perco.getValue().get_turnTimer()).append(";");//TimerActuel
	    				}
	    				packet.append("45000;");//TimerInit
	    				packet.append("7;");//Nombre de place maximum FIXME : En fonction de la map
	    				packet.append("?,?,");//?
	    			}else
	    			{
	    				packet.append("0;");
	    				packet.append("45000;");
	    				packet.append("7;");
	    				packet.append("?,?,");
	    			}
	    			packet.append("1,2,3,4,5");
	    			
	    			//	?,?,callername,startdate(Base 10),lastHarvesterName,lastHarvestDate(Base 10),nextHarvestDate(Base 10)
	    			isFirst = false;
    		 }
   	 	}
		if(packet.length() == 0) packet = new StringBuilder("null");
		return packet.toString();
	}
	
	public static int CountPercoGuild(int GuildID) {
		int i = 0;
		for(Entry<Integer, Collector> perco :  World.data.getPercos().entrySet()){
			if(perco.getValue().get_guildID() == GuildID)
			{
				i++;
			}
		}
		return i;
	}
	
	public static void parseAttaque(Player perso, int guildID){
		for(Entry<Integer, Collector> perco :  World.data.getPercos().entrySet()) {
			if(perco.getValue().get_inFight() > 0 && perco.getValue().get_guildID() == guildID){
				SocketManager.GAME_SEND_gITp_PACKET(perso, parseAttaqueToGuild(perco.getValue().getId(), perco.getValue().getMap().getId(), perco.getValue().get_inFightID()));
			}
		}
	}
	
	public static void parseDefense(Player perso, int guildID){
		for(Entry<Integer, Collector> perco :  World.data.getPercos().entrySet()) {
			if(perco.getValue().get_inFight() > 0 && perco.getValue().get_guildID() == guildID){
				SocketManager.GAME_SEND_gITP_PACKET(perso, parseDefenseToGuild(perco.getValue().getId(), perco.getValue().getMap().getId(), perco.getValue().get_inFightID()));
			}
		}
	}
	
	public static String parseAttaqueToGuild(int guid, int mapid, int fightid)
	{
		StringBuilder str = new StringBuilder();
		str.append("+").append(guid);
			
		for(Entry<Integer, Fight> F : World.data.getMap(mapid).getFights().entrySet())
		{
			//Je boucle les combats de la map bien qu'inutile :/
			//Mais cela ?viter le bug F.getValue().getFighters(1) == null
				if(F.getValue().getId() == fightid)
				{
					for(Fighter f : F.getValue().getFighters(1))//Attaque
					{
						str.append("|");
						str.append(Integer.toString(f.getPersonnage().getId(), 36)).append(";");
						str.append(f.getPersonnage().getName()).append(";");
						str.append(f.getPersonnage().getLevel()).append(";");
						str.append("0;");
					}
				}
		}
		return str.toString();
	}
	
	public static String parseDefenseToGuild(int guid, int mapid, int fightid)
	{
		StringBuilder str = new StringBuilder();
		str.append("+").append(guid);
			
		for(Entry<Integer, Fight> F : World.data.getMap(mapid).getFights().entrySet())
		{
			//Je boucle les combats de la map bien qu'inutile :/
			//Mais cela ?viter le bug F.getValue().getFighters(2) == null
				if(F.getValue().getId() == fightid)
				{
					for(Fighter f : F.getValue().getFighters(2))//Defense
					{
						if(f.getPersonnage() == null) continue;//On sort le percepteur
						str.append("|");
						str.append(Integer.toString(f.getPersonnage().getId(), 36)).append(";");
						str.append(f.getPersonnage().getName()).append(";");
						str.append(f.getPersonnage().getGfx()).append(";");
						str.append(f.getPersonnage().getLevel()).append(";");
						str.append(Integer.toString(f.getPersonnage().getColor1(), 36)).append(";");
						str.append(Integer.toString(f.getPersonnage().getColor2(), 36)).append(";");
						str.append(Integer.toString(f.getPersonnage().getColor3(), 36)).append(";");
						str.append("0;");
					}
				}
		}
		return str.toString();
	}
	
	public String getItemPercepteurList()
	{
		StringBuilder items = new StringBuilder();
		if(!_objets.isEmpty())
		{
			for(Object obj : _objets.values())
			{
				items.append("O").append(obj.parseItem()).append(";");
			}
		}
		if(_kamas != 0) items.append("G").append(_kamas);
		return items.toString();
	}
	
	public String parseItemPercepteur()
	{
		String items = "";
		for(Object obj : _objets.values())
		{
			items+= obj.getId()+"|";
		}
		return items;
	}
	
	
	public void removeFromPercepteur(Player P, int guid, int qua)
	{
		Object PercoObj = World.data.getObject(guid);
		Object PersoObj = P.getSimilarItem(PercoObj);
		
		int newQua = PercoObj.getQuantity() - qua;
		
		if(PersoObj == null)//Si le joueur n'avait aucun item similaire
		{
			//S'il ne reste rien
			if(newQua <= 0)
			{
				//On retire l'item
				removeObject(guid);
				//On l'ajoute au joueur
				P.addObject(PercoObj);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OAKO_PACKET(P,PercoObj);
				String str = "O-"+guid;
				SocketManager.GAME_SEND_EsK_PACKET(P, str);
				
			}else //S'il reste des objets
			{
				//On cr?e une copy de l'item
				PersoObj = Object.getClone(PercoObj, qua);
				//On l'ajoute au monde
				World.data.addObject(PersoObj, true);
				//On retire X objet
				PercoObj.setQuantity(newQua);
				//On l'ajoute au joueur
				P.addObject(PersoObj);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OAKO_PACKET(P,PersoObj);
				String str = "O+"+PercoObj.getId()+"|"+PercoObj.getQuantity()+"|"+PercoObj.getTemplate().getId()+"|"+PercoObj.parseStatsString();
				SocketManager.GAME_SEND_EsK_PACKET(P, str);
				
			}
		}
		else
		{
			//S'il ne reste rien
			if(newQua <= 0)
			{
				//On retire l'item
				this.removeObject(guid);
				World.data.removeObject(PercoObj.getId());
				//On Modifie la quantit? de l'item du sac du joueur
				PersoObj.setQuantity(PersoObj.getQuantity() + PercoObj.getQuantity());
				
				//On envoie les packets
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P, PersoObj);
				String str = "O-"+guid;
				SocketManager.GAME_SEND_EsK_PACKET(P, str);
				
			}
			else//S'il reste des objets
			{
				//On retire X objet
				PercoObj.setQuantity(newQua);
				//On ajoute X objets
				PersoObj.setQuantity(PersoObj.getQuantity() + qua);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P,PersoObj);
				String str = "O+"+PercoObj.getId()+"|"+PercoObj.getQuantity()+"|"+PercoObj.getTemplate().getId()+"|"+PercoObj.parseStatsString();
				SocketManager.GAME_SEND_EsK_PACKET(P, str);
				
			}
		}
		SocketManager.GAME_SEND_Ow_PACKET(P);
		P.save();
	}
	
	public void LogXpDrop(long Xp)
	{
		_LogXP += Xp;
	}
	
	public void LogObjectDrop(int guid, Object obj)
	{
		_LogObjects.put(guid, obj);
	}
	
	public long get_LogXp()
	{
		return _LogXP;
	}
	
	public String get_LogItems()
	{
		StringBuilder str = new StringBuilder();
		boolean isFirst = true;
		if(_LogObjects.isEmpty()) return "";
		for(Object obj : _LogObjects.values())
		{
			if(!isFirst) str.append(";");
			 str.append(obj.getTemplate().getId()).append(",").append(obj.getQuantity());
			isFirst = false;
		}
		return str.toString();
	}
	
	public void addObject(Object newObj)
	{
		_objets.put(newObj.getId(), newObj);
	}
	
	public void set_Exchange(boolean Exchange)
	{
		_inExchange = Exchange;
	}
	
	public boolean get_Exchange()
	{
		return _inExchange;
	}
	
	public static void removePercepteur(int GuildID){
		for(Entry<Integer, Collector> perco : World.data.getPercos().entrySet()){
			if(perco.getValue().get_guildID() == GuildID){
				Collector collector = perco.getValue();
				World.data.getPercos().remove(perco.getKey());
				for(Player p : collector.getMap().getPlayers()){
					SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(p.getMap(), perco.getValue().getId());//Suppression visuelle
				}
				World.database.getCollectorData().delete(collector);//Supprime les percepteurs
			}
		}
	}

    //<editor-fold desc="Getters and setters">
    /**
     * Retourne la guilde proprietaire du percepteur
     * @return guilde proprietaire
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     * Retourne l'identifiant du prenom
     * Il est transforme en nom par le jeu
     * @return identifiant du prenom
     */
    public int getFirstNameId()
    {
        return firstNameId;
    }

    /**
     * Retourne l identifiant du nom
     * Il est transforme en nom par le jeu
     * @return identifiant du nom
     */
    public int getLastNameId()
    {
        return lastNameId;
    }
    //</editor-fold>
}