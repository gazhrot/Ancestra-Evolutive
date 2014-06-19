package org.ancestra.evolutive.hdv;

import org.ancestra.evolutive.object.Objet;

public class HdvEntry implements Comparable<HdvEntry> {
	
	private int hdv;
	private int line;
	private int owner;
	private int price;
	private byte amount;//Dans le format : 1=1 2=10 3=100
	private Objet object;
	private boolean purchased = false;
	
	public HdvEntry(int price, byte amount, int owner, Objet object) {
		this.owner = owner;
		this.price = price;
		this.amount = amount;
		this.object = object;	
	}

	public int getHdv() {
		return hdv;
	}

	public void setHdv(int hdv) {
		this.hdv = hdv;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	
	public byte getAmount(boolean parse) {
		if(parse)
			return (byte) (Math.pow(10, (double) this.amount) / 10);
		else
			return this.amount;
	}

	public void setAmount(byte amount) {
		this.amount = amount;
	}

	public Objet getObject() {
		return object;
	}

	public void setObject(Objet object) {
		this.object = object;
	}

	public boolean isPurchased() {
		return purchased;
	}

	public void setPurchased(boolean purchased) {
		this.purchased = purchased;
	}

	public int compareTo(HdvEntry o) {
		HdvEntry e = (HdvEntry)o;
		int celuiCi = this.getPrice();
		int autre = e.getPrice();
		if(autre > celuiCi)
			return -1;
		if(autre == celuiCi)
			return 0;
		if(autre < celuiCi )
			return 1;
		return 0;
	}

	public String parseToEL() {
		StringBuilder toReturn = new StringBuilder();
		int count = getAmount(true);//Transfère dans le format (1,10,100) le montant qui etait dans le format (1,2,3)
		toReturn.append(this.getLine()).append(";").append(count).append(";").append(this.getObject().getTemplate().getID()).append(";").append(this.getObject().parseStatsString()).append(";").append(this.getPrice()).append(";350");//350 = temps restant
		return toReturn.toString();
	}
	
	public String parseToEmK() {
		StringBuilder toReturn = new StringBuilder();
		int count = getAmount(true);//Transfère dans le format (1,10,100) le montant qui etait dans le format (1,2,3)
		toReturn.append(this.getObject().getGuid()).append("|").append(count).append("|").append(this.getObject().getTemplate().getID()).append("|").append(this.getObject().parseStatsString()).append("|").append(this.getPrice()).append("|350");//350 = temps restant
		return toReturn.toString();
	}
}