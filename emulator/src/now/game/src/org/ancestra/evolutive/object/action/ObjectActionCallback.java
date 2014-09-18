package org.ancestra.evolutive.object.action;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.object.Object;

public interface ObjectActionCallback {
	String getName();
	
	ObjectActionResult execute(Player perso, int type, String arg, Object objet, int cellid) throws Exception;
}

