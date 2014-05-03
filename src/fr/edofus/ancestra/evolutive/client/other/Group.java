package fr.edofus.ancestra.evolutive.client.other;

import java.util.ArrayList;

import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.common.SocketManager;


public class Group {
	
	private ArrayList<Player> players = new ArrayList<Player>();
	private Player chief;
	
	public Group(Player p1,Player p2)
	{
		this.chief = p1;
		this.players.add(p1);
		this.players.add(p2);
	}
	
	public ArrayList<Player> getPersos() {
		return this.players;
	}

	public Player getChief() {
		return this.chief;
	}
	
	public boolean isChief(int i) {
		return this.chief.get_GUID() == i;
	}
	
	public void addPerso(Player p) {
		this.players.add(p);
	}
	
	public int getPersosNumber() {
		return this.players.size();
	}
	
	public int getGroupLevel() {
		int lvls = 0;
		for(Player p : this.players)
			lvls += p.get_lvl();
		return lvls;
	}	

	public void leave(Player i) 
	{
		if(!this.players.contains(i))
			return;
		i.setGroup(null);
		this.players.remove(i);
		if(this.players.size() == 1)
		{
			this.players.get(0).setGroup(null);
			if(this.players.get(0).getAccount() == null || this.players.get(0).getAccount().getGameClient() == null)
				return;
			SocketManager.GAME_SEND_PV_PACKET(this.players.get(0).getAccount().getGameClient(), "");
		}else
		{
			SocketManager.GAME_SEND_PM_DEL_PACKET_TO_GROUP(this, i.get_GUID());
		}
	}
}