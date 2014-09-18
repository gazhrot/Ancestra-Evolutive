package org.ancestra.evolutive.game;

public class GameAction {
	
	private final int id;
	private final int action;
	private final String packet;
	private String args;
	
	public GameAction(int id, int action, String packet) {
		this.id = id;
		this.action = action;
		this.packet = packet;
	}

	public int getId() {
		return id;
	}

	public int getAction() {
		return action;
	}

	public String getPacket() {
		return packet;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}
}