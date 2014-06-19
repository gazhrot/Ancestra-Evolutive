package org.ancestra.evolutive.guild;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.core.World;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Map;
import java.util.TreeMap;

public class GuildMember {
	
	private Guild guild;
	private Player player;
	private int UUID;	
	private int rank = 0;
	private byte xpGive = 0;
	private long xpGave = 0;
	private int right = 0;
	private Map<Integer,Boolean> rights = new TreeMap<Integer,Boolean>();

	public GuildMember(Guild guild, int UUID, int rank, long xpGave, byte xpGive, int right) {
		this.guild = guild;
		this.UUID = UUID;
		this.rank = rank;
		this.xpGave = xpGave;
		this.xpGive = xpGive;
		this.right = right;
		this.parseIntToRight(right);
	}
	
	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

	public Player getPlayer() {
		if(this.player == null)
			this.player = World.data.getPlayer(this.getUUID());
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getUUID() {
		return UUID;
	}

	public void setUUID(int uUID) {
		UUID = uUID;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public byte getXpGive() {
		return xpGive;
	}

	public void setXpGive(byte xpGive) {
		this.xpGive = xpGive;
	}

	public long getXpGave() {
		return xpGave;
	}

	public void setXpGave(long xpGave) {
		this.xpGave = xpGave;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public Map<Integer, Boolean> getRights() {
		return rights;
	}

	public void setRights(Map<Integer, Boolean> rights) {
		this.rights = rights;
	}

	public int getLastConnection() {
		String[] strDate = this.getPlayer().getAccount().getLastConnection().toString().split("~");
		
		LocalDate lastCo = new LocalDate(Integer.parseInt(strDate[0]),Integer.parseInt(strDate[1]),Integer.parseInt(strDate[2]));
		LocalDate now = new LocalDate();
		
		return Days.daysBetween(lastCo,now).getDays()*24;
	}
	
	public void giveXpToGuild(long xp) {
		this.xpGave += xp;
		this.getGuild().addXp(xp);
	}

	public boolean canDo(int right) {
		if(this.getRight() == 1)
			return true;	
		return this.getRights().get(right);
	}

	public void setAllRights(int rank, byte xp, int right) {
		if(rank == -1)
			rank = this.getRank();
		
		if(xp < 0)
			xp = this.getXpGive();
		if(xp > 90)
			xp = 90;
		
		if(right == -1)
			right = this.getRight();
		
		this.setRank(rank);
		this.setXpGive(xp);
		
		if(right != this.getRight() && right != 1)//V�rifie si les droits sont pareille ou si des droits de meneur; pour ne pas faire la conversion pour rien
			parseIntToRight(right);
		
		this.setRight(right);
		World.database.getGuildMemberData().update(this);
	}
	
	public void initRight() {
		this.getRights().put(Constants.G_BOOST,false);
		this.getRights().put(Constants.G_RIGHT,false);
		this.getRights().put(Constants.G_INVITE,false);
		this.getRights().put(Constants.G_BAN,false);
		this.getRights().put(Constants.G_ALLXP,false);
		this.getRights().put(Constants.G_HISXP,false);
		this.getRights().put(Constants.G_RANK,false);
		this.getRights().put(Constants.G_POSPERCO,false);
		this.getRights().put(Constants.G_COLLPERCO,false);
		this.getRights().put(Constants.G_USEENCLOS,false);
		this.getRights().put(Constants.G_AMENCLOS,false);
		this.getRights().put(Constants.G_OTHDINDE,false);
	}
	
	public String parseRights() {
		return Integer.toString(this.right, 36);
	}
	
	public void parseIntToRight(int total) {
		if(this.getRights().isEmpty())
			initRight();
		if(total == 1)
			return;
		
		if(this.getRights().size() > 0)//Si les droits contiennent quelque chose -> Vidage (M�me si le TreeMap supprimerais les entr�es doublon lors de l'ajout)
			this.getRights().clear();
			
		initRight();
		
		Integer[] mapKey = this.getRights().keySet().toArray(new Integer[this.getRights().size()]);//R�cup�re les clef de map dans un tableau d'Integer
		
		while(total > 0) {
			for(int i = this.getRights().size()-1; i < this.getRights().size(); i--) {
				if(mapKey[i].intValue() <= total) {
					total ^= mapKey[i].intValue();
					this.getRights().put(mapKey[i],true);
					break;
				}
			}
		}
	}
}