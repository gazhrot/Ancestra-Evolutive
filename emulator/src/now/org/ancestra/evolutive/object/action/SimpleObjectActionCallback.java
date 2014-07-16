package org.ancestra.evolutive.object.action;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.object.Object;

public class SimpleObjectActionCallback implements ObjectActionCallback {

	private String name;
	
	public SimpleObjectActionCallback() {
		this.name = "Undefined";
	}
	
	public SimpleObjectActionCallback(String name) {
		this.name = name;
	}
	
	@Override
	public ObjectActionResult execute(Player perso, int type, String arg,
			Object objet, int cellid) throws Exception {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}
}
