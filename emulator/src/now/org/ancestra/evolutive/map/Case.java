package org.ancestra.evolutive.map;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Creature;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.game.GameAction;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.house.Trunk;
import org.ancestra.evolutive.job.JobConstant;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.other.Action;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Case {

    private final Logger logger;
	private final int id;
	private final Maps map;
	private final boolean walkable;
	private final boolean LoS;
	private final InteractiveObject interactiveObject;
	private Object object;
	private ArrayList<Action> onCellStop;
	private ArrayList<Creature> creatures;
	private Map<Integer, Fighter> fighters;
	
	public Case(Maps map, int id, boolean walkable, boolean LoS, int interactiveObject){
        this.map = map;
		this.id = id;
        this.logger = (Logger)LoggerFactory.getLogger("Maps : "+map.getId() +" Case : " + id);
		this.walkable = walkable;
		this.LoS = LoS;
		if(interactiveObject != -1) {
            this.interactiveObject = new InteractiveObject(interactiveObject, map, this);
        } else {
            this.interactiveObject = null;
        }
	}

    public Case copy(){
        return new Case(this.map,this.id,this.walkable,this.LoS,
                this.interactiveObject==null?-1:this.interactiveObject.getId());
    }

	public int getId() {
		return id;
	}

	public Maps getMap() {
		return map;
	}
	
	public boolean isWalkable(boolean object) {
		if(this.interactiveObject != null && object)
			return walkable && interactiveObject.isWalkable();
		return walkable;
	}

	public boolean isLoS() {
		return LoS;
	}
	
	public boolean blockLoS() {
		if(this.fighters == null) 
			return LoS;
		
		boolean fighter = true;
		for(Entry<Integer,Fighter> f : this.fighters.entrySet())
			if(!f.getValue().isHide())
				fighter = false;
		
		return LoS && fighter;
	}
	
	public InteractiveObject getInteractiveObject() {
		return interactiveObject;
	}
	
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public void addOnCellStopAction(int id, String args, String condition) {
		if(this.onCellStop == null) 
			this.onCellStop = new ArrayList<>();
		this.onCellStop.add(new Action(id, args, condition));
	}
	
	public void applyOnCellStopActions(Player player) {
		if(this.onCellStop != null) 
			for(Action action: this.onCellStop) {
                action.apply(player, null, -1, -1);
            }
	}
	
	public void clearOnCellAction() {
		this.onCellStop = null;
	}
	
	public Map<Integer, Player> getPlayers() {
        Map<Integer,Player> mapPlayer = new HashMap<>();
        if(creatures == null) return mapPlayer;
		for(Creature creature : creatures){
            if(creature instanceof Player){
                mapPlayer.put(creature.getId(),(Player)creature);
            }
        }
        return mapPlayer;
	}
	
	public void addCreature(Creature creature) {
		if(this.creatures == null)
			this.creatures = new ArrayList<>();
		if(!this.creatures.contains(creature)){
            this.creatures.add(creature);
        }
	}
	
	public void removeCreature(Creature creature) {
		if(this.creatures == null || !this.creatures.contains(creature))
			return;
        this.creatures.remove(creature);
            if(this.creatures.isEmpty())
                this.creatures = null;
  	}
	
	public Map<Integer, Fighter> getFighters() {
		if(this.fighters == null) 
			return new TreeMap<>();
		return this.fighters;
	}
	
	public void addFighter(Fighter fighter) {
		if(this.fighters == null) 
			this.fighters = new TreeMap<>();
		this.fighters.put(fighter.getId(), fighter);
	}

    public boolean isFree(){
        if(isWalkable(true) && creatures == null && interactiveObject == null)
            return true;

        return false;
    }

	public void removeFighter(Fighter fighter) {
		this.fighters.remove(fighter.getId());
	}
	
	public Fighter getFirstFighter() {
		if(this.fighters != null) 
			for(Entry<Integer,Fighter> entry : this.fighters.entrySet())
				return entry.getValue();
		return null;
	}
	
	public void startAction(Player perso, GameAction GA){
		int actionID = -1;
		short CcellID = -1;
		
		try	{
			actionID = Integer.parseInt(GA.getArgs().split("\\;")[1]);
			CcellID = Short.parseShort(GA.getArgs().split("\\;")[0]);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(actionID == -1)
			return;
		
		if(JobConstant.isJobAction(actionID)) {
			perso.doJobAction(actionID, this.interactiveObject, GA, this);
			return;
		}
		
		switch(actionID)
		{
			case 44://Sauvegarder pos
				String str = this.map+","+this.id;
				perso.setSavePos(str);
				SocketManager.GAME_SEND_Im_PACKET(perso, "06");
				perso.getAccount().getGameClient().removeAction(GA);
			break;
		
			case 102://Puiser
				if(!this.interactiveObject.isInteractive())return;//Si l'objet est utilis�
				if(this.interactiveObject.getState() != Constants.IOBJECT_STATE_FULL)return;//Si le puits est vide
				this.interactiveObject.setState(Constants.IOBJECT_STATE_EMPTYING);
				this.interactiveObject.setInteractive(false);
				SocketManager.GAME_SEND_GA_PACKET_TO_MAP(perso.getMap(),""+GA.getId(), 501, perso.getId()+"", this.id+","+this.interactiveObject.getUseDuration()+","+this.interactiveObject.getUnknowValue());
				SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(perso.getMap(),this);
			break;
			case 114://Utiliser (zaap)
				perso.openZaapMenu();
				perso.getAccount().getGameClient().removeAction(GA);
			break;
			case 153 :
				Trunk bin = Trunk.getTrunkByPos(perso.getMap().getId(), CcellID);
				if(bin == null) {
					perso.sendText("La poubelle actuel est inutilisable, merci de contacter un administrateur.");
					return;
				}
				perso.setCurTrunk(bin);
				Trunk.open(perso, "-", true);
				break;
			case 157: //Zaapis
				String ZaapiList= "";
				String[] Zaapis;
				int count = 0;
				int price = 20;
				
				if (perso.getMap().getSubArea().getArea().getId() == 7 && (perso.getAlign() == Alignement.BONTARIEN || perso.getAlign() == Alignement.NEUTRE || perso.getAlign() == Alignement.MERCENAIRE))//Ange, Neutre ou S�rianne
				{
					Zaapis = Constants.ZAAPI.get(Alignement.BONTARIEN).split(",");
					if (perso.getAlign() == Alignement.BONTARIEN) price = 10;
				}
				else if (perso.getMap().getSubArea().getArea().getId() == 11 && (perso.getAlign() == Alignement.BRAKMARIEN || perso.getAlign() == Alignement.NEUTRE || perso.getAlign() == Alignement.MERCENAIRE))//D�mons, Neutre ou S�rianne
				{
					Zaapis = Constants.ZAAPI.get(Alignement.BRAKMARIEN).split(",");
					if (perso.getAlign() == Alignement.BRAKMARIEN) price = 10;
				}
				else
				{
					Zaapis = Constants.ZAAPI.get(Alignement.NEUTRE).split(",");
				}
				
				if(Zaapis.length > 0)
				{
					for (String s : Zaapis)
					{
						if(count == Zaapis.length)
							ZaapiList += s+";"+price;
						else
							ZaapiList += s+";"+price+"|";
						count++;
					}
					perso.setZaaping(true);
					SocketManager.GAME_SEND_ZAAPI_PACKET(perso, ZaapiList);
				}
			break;
			case 175://Acceder a un enclos
				if(this.interactiveObject.getState() != Constants.IOBJECT_STATE_EMPTY);
				//SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(perso.getMap(),this);
				perso.openMountPark();
			break;
			case 176://Achat enclo
				MountPark MP = perso.getMap().getMountPark();
				if(MP.getOwner() == -1)//Public
				{
					SocketManager.GAME_SEND_Im_PACKET(perso, "196");
					return;
				}
				if(MP.getPrice() == 0)//Non en vente
				{
					SocketManager.GAME_SEND_Im_PACKET(perso, "197");
					return;
				}
				if(perso.getGuild() == null)//Pas de guilde
				{
					SocketManager.GAME_SEND_Im_PACKET(perso, "1135");
					return;
				}
				if(perso.getGuildMember().getRank() != 1)//Non meneur
				{
					SocketManager.GAME_SEND_Im_PACKET(perso, "198"); 
					return;
				}
				SocketManager.GAME_SEND_R_PACKET(perso, "D"+MP.getPrice()+"|"+MP.getPrice());
			break;
			case 177://Vendre enclo
			case 178://Modifier prix de vente
				MountPark MP1 = perso.getMap().getMountPark();
				if(MP1.getOwner() == -1)
				{
					SocketManager.GAME_SEND_Im_PACKET(perso, "194");
					return;
				}
				if(MP1.getOwner() != perso.getId())
				{
					SocketManager.GAME_SEND_Im_PACKET(perso, "195");
					return;
				}
				SocketManager.GAME_SEND_R_PACKET(perso, "D"+MP1.getPrice()+"|"+MP1.getPrice());
			break;
			case 183://Retourner sur Incarnam
				if(perso.getLevel()>15)
				{
					SocketManager.GAME_SEND_Im_PACKET(perso, "1127");
				} else {
                    int mapID = perso.getClasse().getInkarnamStartMap();
                    int cellID = perso.getClasse().getInkarnamStartCell();
                    perso.setPosition(mapID, cellID);
                }
				perso.getAccount().getGameClient().removeAction(GA);
			break;
			case 81://V�rouiller maison
				House h = House.getHouseByCoord(perso.getMap().getId(), CcellID);
				if(h == null)
					return;
				perso.setCurHouse(h);
				h.lock(perso);
			break;
			case 84://Rentrer dans une maison
				House h2 = House.getHouseByCoord(perso.getMap().getId(), CcellID);
				if(h2 == null)
					return;
				perso.setCurHouse(h2);
				h2.open(perso);
			break;
			case 97://Acheter maison
				House h3 = House.getHouseByCoord(perso.getMap().getId(), CcellID);
				if(h3 == null)
					return;
				perso.setCurHouse(h3);
				h3.buyIt(perso);
			break;
			
            case 104://Ouvrir coffre priv�
            	Trunk trunk = Trunk.getTrunkByPos(perso.getMap().getId(), CcellID);
            	if(trunk == null)
                {
                	Log.addToLog("Game: INVALID TRUNK ON MAP : "+perso.getMap().getId()+" CELLID : "+CcellID);
                	return;
                }
                perso.setCurTrunk(trunk);
                trunk.open(perso);
            break;
            case 105://V�rouiller coffre
                Trunk t = Trunk.getTrunkByPos(perso.getMap().getId(), CcellID);
                if(t == null)
                {
                	Log.addToLog("Game: INVALID TRUNK ON MAP : "+perso.getMap().getId()+" CELLID : "+CcellID);
                	return;
                }
                perso.setCurTrunk(t);
                t.lock(perso);
            break;
            
			case 98://Vendre
			case 108://Modifier prix de vente
				House h4 = House.getHouseByCoord(perso.getMap().getId(), CcellID);
				if(h4 == null)return;
				perso.setCurHouse(h4);
				h4.sellIt(perso);
			break;
			
			default:
				logger.info("Case.startAction non definie pour l'actionID = "+actionID);
			break;
		}
	}
	
	public void finishAction(Player perso, GameAction GA){
		int actionID = -1;
		
		try	{
			actionID = Integer.parseInt(GA.getArgs().split("\\;")[1]);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(actionID == -1)
			return;
		
		if(JobConstant.isJobAction(actionID)) {
			perso.finishJobAction(actionID, this.interactiveObject, GA, this);
			return;
		}
		
		switch(actionID)
		{
			case 81://V�rouiller maison
			case 84://ouvrir maison
			case 97://Acheter maison.
			case 98://Vendre
			case 104://Ouvrir coffre
			case 105://Code coffre
			case 108://Modifier prix de vente
			case 157://Zaapi
			case 153://Poubelle
			break;
			case 102://Puiser
				this.interactiveObject.setState(Constants.IOBJECT_STATE_EMPTY);
				this.interactiveObject.setInteractive(false);
				this.interactiveObject.startTimer();
				SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(perso.getMap(),this);
				int qua = Formulas.getRandomValue(1, 10);//On a entre 1 et 10 eaux
				Object obj = World.data.getObjectTemplate(311).createNewItem(qua, false);
				if(perso.addObject(obj, true))
					World.data.addObject(obj,true);
				SocketManager.GAME_SEND_IQ_PACKET(perso,perso.getId(),qua);
			break;
			
			case 183:
			break;
			
			default:
				Log.addToLog("[FIXME]Case.finishAction non definie pour l'actionID = "+actionID);
			break;
		}
	}
	
	public boolean canDoAction(int id){
        if(this.interactiveObject == null) 
        	return false;
        
		switch(id) {
			//Moudre et egrenner - Paysan
			case 122:
			case 47:
				return this.interactiveObject.getId() == 7007;
			//Faucher Bl�
			case 45:
				switch(this.interactiveObject.getId())
				{
					case 7511://Bl�
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Faucher Orge
			case 53:
				switch(this.interactiveObject.getId())
				{
					case 7515://Orge
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			
			//Faucher Avoine
			case 57:
				switch(this.interactiveObject.getId())
				{
					case 7517://Avoine
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;	
			//Faucher Houblon
			case 46:
				switch(this.interactiveObject.getId())
				{
					case 7512://Houblon
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Faucher Lin
			case 50:
			case 68:
				switch(this.interactiveObject.getId())
				{
					case 7513://Lin
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Faucher Riz
			case 159:
				switch(this.interactiveObject.getId())
				{
					case 7550://Riz
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Faucher Seigle
			case 52:
				switch(this.interactiveObject.getId())
				{
					case 7516://Seigle
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Faucher Malt
			case 58:
				switch(this.interactiveObject.getId())
				{
					case 7518://Malt
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;			
			//Faucher Chanvre - Cueillir Chanvre
			case 69:
			case 54:
				switch(this.interactiveObject.getId())
				{
					case 7514://Chanvre
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Scier - Bucheron
			case 101:
				return this.interactiveObject.getId() == 7003;
			//Couper Fr�ne
			case 6:
				switch(this.interactiveObject.getId())
				{
					case 7500://Fr�ne
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Ch�taignier
			case 39:
				switch(this.interactiveObject.getId())
				{
					case 7501://Ch�taignier
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Noyer
			case 40:
				switch(this.interactiveObject.getId())
				{
					case 7502://Noyer
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Ch�ne
			case 10:
				switch(this.interactiveObject.getId())
				{
					case 7503://Ch�ne
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Oliviolet
			case 141:
				switch(this.interactiveObject.getId())
				{
					case 7542://Oliviolet
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Bombu
			case 139:
				switch(this.interactiveObject.getId())
				{
					case 7541://Bombu
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Erable
			case 37:
				switch(this.interactiveObject.getId())
				{
					case 7504://Erable
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Bambou
			case 154:
				switch(this.interactiveObject.getId())
				{
					case 7553://Bambou
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper If
			case 33:
				switch(this.interactiveObject.getId())
				{
					case 7505://If
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Merisier
			case 41:
				switch(this.interactiveObject.getId())
				{
					case 7506://Merisier
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Eb�ne
			case 34:
				switch(this.interactiveObject.getId())
				{
					case 7507://Eb�ne
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Kalyptus
			case 174:
				switch(this.interactiveObject.getId())
				{
					case 7557://Kalyptus
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Charme
			case 38:
				switch(this.interactiveObject.getId())
				{
					case 7508://Charme
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Orme
			case 35:
				switch(this.interactiveObject.getId())
				{
					case 7509://Orme
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Bambou Sombre
			case 155:
				switch(this.interactiveObject.getId())
				{
					case 7554://Bambou Sombre
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Couper Bambou Sacr�
			case 158:
				switch(this.interactiveObject.getId())
				{
					case 7552://Bambou Sacr�
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Puiser
			case 102:
				switch(this.interactiveObject.getId())
				{
					case 7519://Puits
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Polir
			case 48:
				return this.interactiveObject.getId() == 7005;//7510
			//Moule/Fondre - Mineur
			case 32:
				return this.interactiveObject.getId() == 7002;
			//Miner Fer
			case 24:
				switch(this.interactiveObject.getId())
				{
					case 7520://Miner
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Miner Cuivre
			case 25:
				switch(this.interactiveObject.getId())
				{
					case 7522://Miner
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Miner Bronze
			case 26:
				switch(this.interactiveObject.getId())
				{
					case 7523://Miner
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Miner Kobalte
			case 28:
				switch(this.interactiveObject.getId())
				{
					case 7525://Miner
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Miner Manga
			case 56:
				switch(this.interactiveObject.getId())
				{
					case 7524://Miner
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Miner Sili
			case 162:
				switch(this.interactiveObject.getId())
				{
					case 7556://Miner
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Miner Etain
			case 55:
				switch(this.interactiveObject.getId())
				{
					case 7521://Miner
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Miner Argent
			case 29:
				switch(this.interactiveObject.getId())
				{
					case 7526://Miner
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Miner Bauxite
			case 31:
				switch(this.interactiveObject.getId())
				{
					case 7528://Miner
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Miner Or
			case 30:
				switch(this.interactiveObject.getId())
				{
					case 7527://Miner
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Miner Dolomite
			case 161:
				switch(this.interactiveObject.getId())
				{
					case 7555://Miner
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Fabriquer potion - Alchimiste
			case 23:
				return this.interactiveObject.getId() == 7019;
			//Cueillir Tr�fle
			case 71:
				switch(this.interactiveObject.getId())
				{
					case 7533://Tr�fle
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Cueillir Menthe
			case 72:
				switch(this.interactiveObject.getId())
				{
					case 7534://Menthe
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Cueillir Orchid�e
			case 73:
				switch(this.interactiveObject.getId())
				{
					case 7535:// Orchid�e
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Cueillir Edelweiss
			case 74:
				switch(this.interactiveObject.getId())
				{
					case 7536://Edelweiss
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Cueillir Graine de Pandouille
			case 160:
				switch(this.interactiveObject.getId())
				{
					case 7551://Graine de Pandouille
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Vider - P�cheur
			case 133:
				return this.interactiveObject.getId() == 7024;
			//P�cher Petits poissons de mer
			case 128:
				switch(this.interactiveObject.getId())
				{
					case 7530://Petits poissons de mer
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//P�cher Petits poissons de rivi�re
			case 124:
				switch(this.interactiveObject.getId())
				{
					case 7529://Petits poissons de rivi�re
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//P�cher Pichon
			case 136:
				switch(this.interactiveObject.getId())
				{
					case 7544://Pichon
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//P�cher Ombre Etrange
			case 140:
				switch(this.interactiveObject.getId())
				{
					case 7543://Ombre Etrange
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//P�cher Poissons de rivi�re
			case 125:
				switch(this.interactiveObject.getId())
				{
					case 7532://Poissons de rivi�re
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//P�cher Poissons de mer
			case 129:
				switch(this.interactiveObject.getId())
				{
					case 7531://Poissons de mer
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//P�cher Gros poissons de rivi�re
			case 126:
				switch(this.interactiveObject.getId())
				{
					case 7537://Gros poissons de rivi�re
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//P�cher Gros poissons de mers
			case 130:
				switch(this.interactiveObject.getId())
				{
					case 7538://Gros poissons de mers
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//P�cher Poissons g�ants de rivi�re
			case 127:
				switch(this.interactiveObject.getId())
				{
					case 7539://Poissons g�ants de rivi�re
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//P�cher Poissons g�ants de mer
			case 131:
				switch(this.interactiveObject.getId())
				{
					case 7540://Poissons g�ants de mer
						return this.interactiveObject.getState() == Constants.IOBJECT_STATE_FULL;
				}
			return false;
			//Boulanger
			case 109://Pain
			case 27://Bonbon
				return this.interactiveObject.getId() == 7001;
			//Poissonier
			case 135://Faire un poisson (mangeable)
				return this.interactiveObject.getId() == 7022;
			//Chasseur
			case 134:
				return this.interactiveObject.getId() == 7023;
			//Boucher
			case 132:
				return this.interactiveObject.getId() == 7025;
			case 157:
				return (this.interactiveObject.getId() == 7030 || this.interactiveObject.getId() == 7031);
			case 44://Sauvegarder le Zaap
			case 114://Utiliser le Zaap
				switch(this.interactiveObject.getId())
				{
					//Zaaps
					case 7000:
					case 7026:
					case 7029:
					case 4287:
						return true;
				}
			return false;
			
			case 175://Acc�der
			case 176://Acheter
			case 177://Vendre
			case 178://Modifier le prix de vente
				switch(this.interactiveObject.getId())
				{
					//Enclos
					case 6763:
					case 6766:
					case 6767:
					case 6772:
						return true;
				}
			return false;
			
			//Se rendre � incarnam
			case 183:
				switch(this.interactiveObject.getId())
				{
					case 1845:
					case 1853:
					case 1854:
					case 1855:
					case 1856:
					case 1857:
					case 1858:
					case 1859:
					case 1860:
					case 1861:
					case 1862:
					case 2319:
						return true;
				}
			return false;
			
			//Enclume magique
			case  1:
			case 113:
			case 115:
			case 116:
			case 117:
			case 118:
			case 119:
			case 120:
				return this.interactiveObject.getId() == 7020;

			//Enclume
			case 19:
			case 143:
			case 145:
			case 144:
			case 142:
			case 146:
			case 67:
			case 21:
			case 65:
			case 66:
			case 20:
			case 18:
				return this.interactiveObject.getId() == 7012;

			//Costume Mage
			case 167:
			case 165:
			case 166:
				return this.interactiveObject.getId() == 7036;

			//Coordo Mage
			case 164:
			case 163:
				return this.interactiveObject.getId() == 7037;

			//Joai Mage
			case 168:
			case 169:
				return this.interactiveObject.getId() == 7038;

			//Bricoleur
			case 171:
			case 182:
				return this.interactiveObject.getId() == 7039;

			//Forgeur Bouclier
			case 156:
				return this.interactiveObject.getId() == 7027;

			//Coordonier
			case 13:
			case 14:
				return this.interactiveObject.getId() == 7011;

			//Tailleur (Dos)
			case 123:
			case 64:
				return this.interactiveObject.getId() == 7015;


			//Sculteur
			case 17:
			case 16:
			case 147:
			case 148:
			case 149:
			case 15:
				return this.interactiveObject.getId() == 7013;

			//Tailleur (Haut)
			case 63:
				return (this.interactiveObject.getId() == 7014 || this.interactiveObject.getId() == 7016);
			//Atelier : Cr�er Amu // Anneau
			case 11:
			case 12:
				return (this.interactiveObject.getId() >= 7008 && this.interactiveObject.getId() <= 7010);
			//Maison
			case 81://V�rouiller
			case 84://Acheter
			case 97://Entrer
			case 98://Vendre
			case 108://Modifier le prix de vente
				return (this.interactiveObject.getId() >= 6700 && this.interactiveObject.getId() <= 6776);
			//Coffre	
			case 104://Ouvrir
			case 105://Code
				return (this.interactiveObject.getId() == 7350 || this.interactiveObject.getId() == 7351 || this.interactiveObject.getId() == 7353);
			case 153 :
				return this.interactiveObject.getId() == 7352;
			//Action ID non trouv�
			default:
				Log.addToLog("MapActionID non existant dans Case.canDoAction: "+id);
				return false;
		}
	}
}