package org.ancestra.evolutive.client.other;

import java.util.ArrayList;

import org.ancestra.evolutive.client.Player;

public class Emote {
	
	private final Player player;
	private ArrayList<Integer> emotes;
	
	public Emote(Player player, ArrayList<Integer> emotes) {
		this.player = player;
		this.emotes = emotes;
		
		if(!this.emotes.contains(1))
			this.emotes.add(1);
	}
	
	public ArrayList<Integer> getAll() {
		return emotes;
	}
	
	public void add(int id) {
		if(!this.emotes.contains(id))
			this.emotes.add(id);
		this.send();
		player.send("eA" + id);
	}
	
	public void remove(int id) {
		if(this.emotes.contains(id))
			this.emotes.remove(id);
		this.send();
	}
	
	public void send() {
		player.send("eL" + this.getCompiled());
	}
	
	public String getCompiled() {
		int i = 0;
		
		for(Integer b : this.emotes) 
			i += (2 << (b-2));
		
		return i + "|0";
	}
	
	public String parseToSave() {
		String str = "";
		
		for(int i : this.emotes) 
			str += (str.isEmpty() ? i : "," + i);
			
		return str;
	}
	
	public static ArrayList<Integer> convertStringToArray(String str) {
		ArrayList<Integer> emotes = new ArrayList<>(); 
		
		for(String id : str.split("\\,")) {
			try {
				emotes.add(Integer.parseInt(id));
			} catch(Exception e) {}
		}
		
		return emotes;
	}
}