package org.ancestra.evolutive.client.other;

import org.ancestra.evolutive.client.Player;

public class Stalk 
{
	private long time;
	private Player tracked;
	
	public Stalk(long time, Player player) {
		this.time = time;
		this.tracked = player;
	}
	
	public Player getTraque() {
		return tracked;
	}
	
	public void setTraque(Player player) {
		this.tracked = player;
	}
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}