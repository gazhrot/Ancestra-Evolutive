package org.ancestra.evolutive.fight.spell;

public class Animation {
	
	private int id;
	private int gfx;
	private String name;
	private int area;
	private int action;
	private int size;
	
	public Animation(int id, int gfx, String name, int area, int action, int size) 
	{
		this.id = id;
		this.gfx = gfx;
		this.name = name;
		this.area = area;
		this.action = action;
		this.size = size;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getArea() {
		return area;
	}
	
	public int getAction() {
		return action;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getGfx() {
		return gfx;
	}
	
	public static String parseToGA(Animation animation) {
		StringBuilder Packet = new StringBuilder();
		Packet.append(animation.getGfx()).append(",").append(animation.getArea()).append(",").append(animation.getAction()).append(",").append(animation.getSize());
		return Packet.toString();
	}
}