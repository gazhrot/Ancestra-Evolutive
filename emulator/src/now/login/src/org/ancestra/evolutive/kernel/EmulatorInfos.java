package org.ancestra.evolutive.kernel;

public enum EmulatorInfos {
	DEVELOPER("Locos & Erfive"),
	RELEASE(1.0),
	SOFT_NAME("Ancestra Evolutive Login v"+RELEASE.value),
    HARD_NAME(SOFT_NAME + " for dofus " + Main.config.getConfigFile().getString("network.version") + " by " + DEVELOPER);
	
	private String string;
	private double value;
	
	private EmulatorInfos(String s) {
		this.string = s;
	}
	
	private EmulatorInfos(double d) {
		this.value = d;
	}
	
	public static String uptime() {
		long uptime = System.currentTimeMillis() - Main.config.startTime;
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
