package org.ancestra.evolutive.object;

import java.io.Serializable;

public enum ObjectPosition implements Serializable {
	NO_EQUIPED(-1),
	AMULETTE(0),
	ARME(1),
	ANNEAU1(2),
	CEINTURE(3),
	ANNEAU2(4),
	BOTTES(5),
	COIFFE (6),
	CAPE(7),
	FAMILIER(8),
	DOFUS1(9),
	DOFUS2(10),
	DOFUS3(11),
	DOFUS4(12),
	DOFUS5(13),
	DOFUS6(14),
	BOUCLIER(15);
	
	private final int value;
	
	private ObjectPosition(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static ObjectPosition getPositionById(int id) {
    	for(ObjectPosition position : ObjectPosition.values())
    		if(position.getValue() == id)
    			return position;
    	return null;
    }
}