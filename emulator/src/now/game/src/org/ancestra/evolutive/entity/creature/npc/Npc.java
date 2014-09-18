package org.ancestra.evolutive.entity.creature.npc;

import org.ancestra.evolutive.entity.creature.Creature;
import org.ancestra.evolutive.enums.IdType;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.Maps;

public class Npc extends Creature {
	private final NpcTemplate template;

    /**
     * Creer un npc
     * @param template template du npc
     * @param id id du npc
     * @param map map de depart du npc
     * @param cell cellule de depart du npc
     * @param orientation orientation initiale
     */
	public Npc(NpcTemplate template, int id,Maps map,Case cell, byte orientation) {
        super(id,"npc" + Integer.toString(id),map,cell,(int)orientation);
		this.template = template;
        helper = new NpcHelper(this);
        this.getMap().addEntity(this);
        this.cell.addCreature(this);
    }

    /**
     * Creer un npc aec un id formatte selon la cellule de depart
     * @param template template du npc
     * @param map map de depart du npc
     * @param cell cellule de depart du npc
     * @param orientation orientation initiale
     */
    public Npc(NpcTemplate template,Maps map,Case cell, byte orientation) {
        this(template,IdType.PNJ.MAXIMAL_ID-map.getId()*1000 - cell.getId(),map,cell,(byte)orientation);
    }

	public NpcTemplate getTemplate() {
		return template;
	}

}