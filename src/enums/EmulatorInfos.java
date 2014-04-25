package enums;

public enum EmulatorInfos {
	DEVELOPPER("John-r & Locos"),
	RELEASE(0.7),
	CLIENT_RELESE("1.29.1"),
	SOFT_NAME("Ancestra Evolutive v"+RELEASE.toDouble());
	
	private String string;
	private double value;
	
	private EmulatorInfos(String s) {
		this.string = s;
	}
	
	private EmulatorInfos(double d) {
		this.value = d;
	}
	
	public double toDouble() {
		return this.value;
	}
	
	public String toString() {
		return this.string;
	}
}
