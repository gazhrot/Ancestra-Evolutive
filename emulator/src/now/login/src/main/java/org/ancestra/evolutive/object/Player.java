package org.ancestra.evolutive.object;

import java.io.Serializable;

public class Player implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id, server;
	
	public Player(int id, int server) {
		this.id = id;
		this.server = server;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getServer() {
		return server;
	}
	
	public void setServer(int server) {
		this.server = server;
	}
}
