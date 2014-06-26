package org.ancestra.evolutive.hdv;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Couple;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;

public class Hdv {
	
	private int id;
	private float taxe;
	private short sellTime;
	private short maxObject;
	private String strCategorys;
	private short levelMax;
	private Map<Integer, HdvCategory> categorys = new HashMap<>();
	private Map<Integer,Couple<Integer, Integer>> path = new HashMap<>();//<LigneID,<CategID,TemplateID>>
	private DecimalFormat pattern = new DecimalFormat("0.0"); 
	
	public Hdv(int hdvID, float taxe, short sellTime, short maxItemCompte, short lvlMax, String categories) {
		this.id = hdvID;
		this.taxe = taxe;
		this.maxObject = maxItemCompte;
		this.strCategorys = categories;
		this.levelMax = lvlMax;
		
		int category;
		for(String str: categories.split("\\,")) {
			category = Integer.parseInt(str);
			this.categorys.put(category, new HdvCategory(category));
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getTaxe() {
		return taxe;
	}

	public void setTaxe(float taxe) {
		this.taxe = taxe;
	}

	public short getSellTime() {
		return sellTime;
	}

	public void setSellTime(short sellTime) {
		this.sellTime = sellTime;
	}

	public short getMaxObject() {
		return maxObject;
	}

	public void setMaxObject(short maxObject) {
		this.maxObject = maxObject;
	}

	public String getStrCategorys() {
		return strCategorys;
	}

	public void setStrCategorys(String strCategorys) {
		this.strCategorys = strCategorys;
	}

	public short getLevelMax() {
		return levelMax;
	}

	public void setLevelMax(short levelMax) {
		this.levelMax = levelMax;
	}

	public DecimalFormat getPattern() {
		return pattern;
	}

	public void setPattern(DecimalFormat pattern) {
		this.pattern = pattern;
	}

	public Map<Integer, HdvCategory> getCategorys() {
		return categorys;
	}

	public Map<Integer, Couple<Integer, Integer>> getPath() {
		return path;
	}

	public HdvLine getLigne(int line) {
		try	{
			int categ = this.getPath().get(line).first;
			int template = this.getPath().get(line).second;
			return this.getCategorys().get(categ).getTemplate(template).getLine(line);
		}catch(NullPointerException e) {
			return null;
		}
	}
	
	public ArrayList<HdvEntry> getAllEntry() {
		ArrayList<HdvEntry> toReturn = new ArrayList<>();
		
		for(HdvCategory curCat : this.getCategorys().values())
			toReturn.addAll(curCat.getAllEntry());
		
		return toReturn;
	}
	
	public void addEntry(HdvEntry entry) {
		entry.setHdv(this.getId());
		int categ = entry.getObject().getTemplate().getType().getValue();
		int template = entry.getObject().getTemplate().getId();
		this.getCategorys().get(categ).addEntry(entry);
		this.getPath().put(entry.getLine(), new Couple<Integer, Integer>(categ, template));
		
		World.data.addHdvObject(entry.getOwner(), this.getId(), entry);		
	}
	
	public boolean delEntry(HdvEntry entry) {
		boolean toReturn =  this.getCategorys().get(entry.getObject().getTemplate().getType()).delEntry(entry);
	
		if(toReturn) {
			this.getPath().remove(entry.getLine());
			World.data.removeHdvObject(entry.getOwner(), entry.getHdv(), entry);
		}
		
		return toReturn;
	}
	
	public synchronized boolean buyItem(int line, byte amount, int price, Player player) {
		boolean toReturn = true;
		
		try	{
			if(player.getKamas() < price)
				return false;
			
			HdvLine ligne = getLigne(line);
			HdvEntry toBuy = ligne.doYouHave(amount, price);
			
			if(toBuy.isPurchased())
				return false;
			
			toBuy.setPurchased(true);
			player.addKamas(price * -1);//Retire l'argent � l'acheteur (prix et taxe de vente)
			
			if(toBuy.getOwner() != -1) {
				Account account = World.data.getCompte(toBuy.getOwner());
				if(account != null)
					account.setBankKamas(account.getBankKamas() + toBuy.getPrice());//Ajoute l'argent au vendeur
			}
			
			SocketManager.GAME_SEND_STATS_PACKET(player);//Met a jour les kamas de l'acheteur
			player.addObject(toBuy.getObject(), true);//Ajoute l'objet au nouveau propri�taire
			toBuy.getObject().getTemplate().newSold(toBuy.getAmount(true), price);//Ajoute la ventes au statistiques
			
			this.delEntry(toBuy);//Retire l'item de l'HDV ainsi que de la liste du vendeur
			
			if(World.data.getCompte(toBuy.getOwner()) != null && World.data.getCompte(toBuy.getOwner()).getCurPlayer() != null)
				SocketManager.GAME_SEND_Im_PACKET(World.data.getCompte(toBuy.getOwner()).getCurPlayer(),"065;"+price+"~"+toBuy.getObject().getTemplate().getId()+"~"+toBuy.getObject().getTemplate().getId()+"~1");
				//Si le vendeur est connecter, envoie du packet qui lui annonce la vente de son objet
			if(toBuy.getOwner() == -1)
				World.database.getItemData().update(toBuy.getObject());
			toBuy = null;
		} catch(NullPointerException e) {
			toReturn = false;
		}
		
		return toReturn;
	}
	
	public String parseTaxe() {
		return pattern.format(this.getTaxe()).replace(",", ".");
	}
	
	public String parseTemplate(int category) {
		return this.getCategorys().get(category).parseTemplate();
	}
	
	public String parseToEHl(int template) {
		int type = World.data.getObjectTemplate(template).getType().getValue();
		return this.getCategorys().get(type).getTemplate(template).parseToEHl();
	}	
}
