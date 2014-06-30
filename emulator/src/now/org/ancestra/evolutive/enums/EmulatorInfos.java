package org.ancestra.evolutive.enums;

import org.ancestra.evolutive.core.Server;

public enum EmulatorInfos {
	DEVELOPER("John-r & Locos & Erfive"),
	RELEASE(0.7),
	CLIENT_RELEASE("1.29.1"),
	SOFT_NAME("Ancestra Evolutive v"+RELEASE.value),
    HARD_NAME(SOFT_NAME + " for dofus " + CLIENT_RELEASE + " by " + DEVELOPER);
	
	private String string;
	private double value;
	
	private EmulatorInfos(String s) {
		this.string = s;
	}
	
	private EmulatorInfos(double d) {
		this.value = d;
	}
	
	public static String uptime() {
		long uptime = System.currentTimeMillis() - Server.config.getGameServer().getStartTime();
		int jour = (int) (uptime/(1000*3600*24));
		uptime %= (1000*3600*24);
		int hour = (int) (uptime/(1000*3600));
		uptime %= (1000*3600);
		int min = (int) (uptime/(1000*60));
		uptime %= (1000*60);
		int sec = (int) (uptime/(1000));
		
		return jour+"j "+hour+"h "+min+"m "+sec+"s";
	}

    @Override
	public String toString() {
		return this.string;
	}
}
