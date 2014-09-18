package org.ancestra.evolutive.other;

public class ExpLevel {
	
	public final long player;
	public final int job;
	public final int mount;
	public final int pvp;
	public final long guild;
	
	public ExpLevel(long player, int job, int mount, int pvp) {
		this.player = player;
		this.job = job;
		this.mount = mount;
		this.pvp = pvp;
		this.guild = player * 10;
	}
}