package org.ancestra.evolutive.client.other;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;

import java.util.ArrayList;

public class Group {
	
	private Player chief;
	private ArrayList<Player> players = new ArrayList<Player>();
	
	public Group(Player p1, Player p2) {
		this.chief = p1;
		this.players.add(p1);
		this.players.add(p2);
	}
	
	public Player getChief() {
		return this.chief;
	}
	
	public boolean isChief(int i) {
		return this.chief.getId() == i;
	}
	
	public ArrayList<Player> getPlayers() {
		return this.players;
	}

	public void addPlayer(Player p) {
		this.players.add(p);
	}
	
	public int getGroupLevel() {
		int lvls = 0;
		for(Player p : this.players)
			lvls += p.getLevel();
		return lvls;
	}	

	public void leave(Player player) {
		if(!this.players.contains(player))
			return;
		player.setGroup(null);
		this.players.remove(player);
		if(this.players.size() == 1) {
			this.players.get(0).setGroup(null);
			if(this.players.get(0).getAccount() == null || this.players.get(0).getAccount().getGameClient() == null)
				return;
			SocketManager.GAME_SEND_PV_PACKET(this.players.get(0).getAccount().getGameClient(), "");
		} else {
			SocketManager.GAME_SEND_PM_DEL_PACKET_TO_GROUP(this, player.getId());
		}
	}
}