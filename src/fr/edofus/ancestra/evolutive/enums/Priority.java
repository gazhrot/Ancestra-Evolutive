package fr.edofus.ancestra.evolutive.enums;

public enum Priority {
	LOWEST(0),
	LOW(1),
	NORMAL(2),
	HIGH(3),
	HIGHEST(4),
	EXCEPTIONNAL(5);
	
	private int priority;
	
	private Priority(int priority) {
		this.priority = priority;
	}
	
	public int getPriority() {
		return priority;
	}	
}