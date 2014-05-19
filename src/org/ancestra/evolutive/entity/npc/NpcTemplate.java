package org.ancestra.evolutive.entity.npc;

import java.util.ArrayList;

import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.npc.NpcTemplate;
import org.ancestra.evolutive.object.ObjectTemplate;

public class NpcTemplate {
	
	private int id;
	private int bonus;
	private int gfx;
	private int scaleX;
	private int scaleY;
	private int sex;
	private int color1;
	private int color2;
	private int color3;
	private String acces;
	private int extraClip;
	private int customArtWork;
	private int initQuestion;
	private ArrayList<ObjectTemplate> objects = new ArrayList<>();
		
	public NpcTemplate(int id, int bonus, int gfx, int scaleX, int scaleY, int sex, int color1,	int color2, 
			int color3, String acces, int extraClip, int customArtWork, int initQuestion, String objects) {
		this.id = id;
		this.bonus = bonus;
		this.gfx = gfx;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.sex = sex;
		this.color1 = color1;
		this.color2 = color2;
		this.color3 = color3;
		this.acces = acces;
		this.extraClip = extraClip;
		this.customArtWork = customArtWork;
		this.initQuestion = initQuestion;
		
		if(objects.equals(""))
			return;
		
		for(String obj : objects.split("\\,")) {
			try	{
				ObjectTemplate template = World.data.getObjTemplate(Integer.parseInt(obj));
				
				if(template == null)
					continue;
				
				this.objects.add(template);
			} catch(NumberFormatException e) {}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}

	public int getGfx() {
		return gfx;
	}

	public void setGfx(int gfx) {
		this.gfx = gfx;
	}

	public int getScaleX() {
		return scaleX;
	}

	public void setScaleX(int scaleX) {
		this.scaleX = scaleX;
	}

	public int getScaleY() {
		return scaleY;
	}

	public void setScaleY(int scaleY) {
		this.scaleY = scaleY;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getColor1() {
		return color1;
	}

	public void setColor1(int color1) {
		this.color1 = color1;
	}

	public int getColor2() {
		return color2;
	}

	public void setColor2(int color2) {
		this.color2 = color2;
	}

	public int getColor3() {
		return color3;
	}

	public void setColor3(int color3) {
		this.color3 = color3;
	}

	public String getAcces() {
		return acces;
	}

	public void setAcces(String acces) {
		this.acces = acces;
	}

	public int getExtraClip() {
		return extraClip;
	}

	public void setExtraClip(int extraClip) {
		this.extraClip = extraClip;
	}

	public int getCustomArtWork() {
		return customArtWork;
	}

	public void setCustomArtWork(int customArtWork) {
		this.customArtWork = customArtWork;
	}

	public int getInitQuestion() {
		return initQuestion;
	}

	public void setInitQuestion(int initQuestion) {
		this.initQuestion = initQuestion;
	}
	
	public ArrayList<ObjectTemplate> getObjects() {
		return objects;
	}
	
	public String getObjectList() {
		if(this.getObjects().isEmpty())
			return "";
		
		StringBuilder items = new StringBuilder();
		
		for(ObjectTemplate obj : this.getObjects())
			items.append(obj.parseItemTemplateStats()).append("|");
		
		return items.toString();
	}

	public boolean addObject(ObjectTemplate template) {
		if(this.getObjects().contains(template))
			return false;
		this.getObjects().add(template);
		return true;
	}
	
	public boolean removeObject(int id) {
		ArrayList<ObjectTemplate> objects = new ArrayList<>();
		boolean remove = false;
		
		for(ObjectTemplate template: this.getObjects()) {
			if(template.getId() == id) {
				remove = true;
				continue;
			}
			objects.add(template);
		}
		
		this.objects = objects;
		return remove;
	}
	
	public boolean haveObject(int id) {
		for(ObjectTemplate template : this.getObjects())
			if(template.getId() == id)
				return true;		
		return false;
	}
}
