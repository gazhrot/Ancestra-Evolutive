package org.ancestra.evolutive.entity.npc;

public class Npc {
	
	private int UUID;
	private NpcTemplate template;
	private int cellid;
	private byte orientation;
	
	public Npc(NpcTemplate template, int UUID, int cellid, byte orientation) {
		this.template = template;
		this.UUID = UUID;
		this.cellid = cellid;
		this.orientation = orientation;
	}

	public int getUUID() {
		return UUID;
	}

	public void setUUID(int uUID) {
		this.UUID = uUID;
	}

	public NpcTemplate getTemplate() {
		return template;
	}

	public void setTemplate(NpcTemplate template) {
		this.template = template;
	}

	public int getCellid() {
		return cellid;
	}

	public void setCellid(int cellid) {
		this.cellid = cellid;
	}

	public byte getOrientation() {
		return orientation;
	}

	public void setOrientation(byte orientation) {
		this.orientation = orientation;
	}

	public String parseToGM() {
		StringBuilder sock = new StringBuilder();
		sock.append("+");
		sock.append(this.getCellid()).append(";");
		sock.append(this.getOrientation()).append(";");
		sock.append("0").append(";");
		sock.append(this.getUUID()).append(";");
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