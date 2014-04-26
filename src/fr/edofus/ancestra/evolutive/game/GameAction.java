package fr.edofus.ancestra.evolutive.game;

public class GameAction {
	
	private int id;
	private int action;
	private String packet;
	private String args;
	
	public GameAction(int id, int action, String packet) {
		this.id = id;
		this.action = action;
		this.packet = packet;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public String getPacket() {
		return packet;
	}

	public void setPacket(String packet) {
		this.packet = packet;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}
}