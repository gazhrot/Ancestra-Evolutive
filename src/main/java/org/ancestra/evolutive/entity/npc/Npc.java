package org.ancestra.evolutive.entity.npc;

import org.ancestra.evolutive.entity.Creature;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.Maps;

public class Npc extends Creature {
	private final NpcTemplate template;
	
	public Npc(NpcTemplate template, int id,Maps map,Case cell, byte orientation) {
        super(id,"npc" + Integer.toString(id),map,cell,(int)orientation);
		this.template = template;
        helper = new NpcHelper(this);
	}

	public NpcTemplate getTemplate() {
		return template;
	}
}