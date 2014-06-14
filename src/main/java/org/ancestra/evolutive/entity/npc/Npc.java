package org.ancestra.evolutive.entity.npc;

import org.ancestra.evolutive.entity.Creature;
import org.ancestra.evolutive.entity.Helper;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.Maps;

public class Npc extends Creature {
	private NpcTemplate template;
	
	public Npc(NpcTemplate template, int id,Maps map,Case cell, byte orientation) {
        super(id,"npc" + Integer.toString(id),map,cell,(int)orientation);
		this.template = template;
        helper = new NpcHelper(this);
	}

	public NpcTemplate getTemplate() {
		return template;
	}

	public void setTemplate(NpcTemplate template) {
		this.template = template;
	}

	public String parseToGM() {
		StringBuilder sock = new StringBuilder();
		sock.append("+");
		sock.append(this.getCell().getId()).append(";");
		sock.append(this.getOrientation()).append(";");
		sock.append("0").append(";");
		sock.append(this.getId()).append(";");
		sock.append(this.getTemplate().getId()).append(";");
		sock.append("-4").append(";");//type = NPC
		
		StringBuilder taille = new StringBuilder();
		if(this.getTemplate().getScaleX() == this.getTemplate().getScaleY())
			taille.append(this.getTemplate().getScaleY());
		else
			taille.append(this.getTemplate().getScaleX()).append("x").append(this.getTemplate().getScaleY());

		sock.append(this.getTemplate().getGfx()).append("^").append(taille.toString()).append(";");
		sock.append(this.getTemplate().getSex()).append(";");
		sock.append((this.getTemplate().getColor1() != -1?Integer.toHexString(this.getTemplate().getColor1()):"-1")).append(";");
		sock.append((this.getTemplate().getColor2() != -1?Integer.toHexString(this.getTemplate().getColor2()):"-1")).append(";");
		sock.append((this.getTemplate().getColor3() != -1?Integer.toHexString(this.getTemplate().getColor3()):"-1")).append(";");
		sock.append(this.getTemplate().getAcces()).append(";");
		sock.append((this.getTemplate().getExtraClip()!=-1?(this.getTemplate().getExtraClip()):(""))).append(";");
		sock.append(this.getTemplate().getCustomArtWork());
		return sock.toString();
	}	
}