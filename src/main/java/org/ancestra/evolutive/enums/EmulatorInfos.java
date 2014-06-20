package org.ancestra.evolutive.enums;

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

    @Override
	public String toString() {
		return this.string;
	}
}
