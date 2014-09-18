package org.ancestra.evolutive.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.core.World;

public class Migration {
	
	public static Map<Integer, Migration> migrations = new TreeMap<>();
	
	private int account;
	private ArrayList<Integer> servers = new ArrayList<>();
	private Map<Integer, ArrayList<Integer>> players = new TreeMap<>();
	
	private StringBuilder packet;
	
	public Migration(int account, String servers) {
		this.account = account;
		
		for(String server: servers.split("\\,"))
			if(!this.servers.contains(Integer.parseInt(server)))
				this.servers.add(Integer.parseInt(server));
		
		migrations.put(account, this);
	}
	
	public Map<Integer, ArrayList<Integer>> getPlayers() {
		return players;
	}
	
	public void add(int server, String packet) {
		if(this.packet == null) 
			this.packet = new StringBuilder();
		
		this.packet.append(packet);
		this.servers.remove((Integer) server);

		if(this.servers.isEmpty()) {
			Account account = World.data.getCompte(this.account);
			StringBuilder AM = new StringBuilder("AM!");
			
			this.parseAM();
			
			AM.append(account.getSubscribeRemaining()).append("|")
			.append(account.getPlayers().size()).append(this.packet.toString());

			account.send(AM.toString());

			this.packet = null;
		}
	}
	
	public int search(int player) {
		for(Entry<Integer, ArrayList<Integer>> entry : this.players.entrySet()) 
			for(int search : entry.getValue()) 
				if(search == player) 
					return entry.getKey();	
		return -1;
	}
	
	private void parseAM() {
		String[] players = this.packet.toString().substring(1).split("\\|");
		
		for(String player : players) {
			String[] data = player.split("\\;");
			int server = Integer.parseInt(data[9]);
			ArrayList<Integer> array = this.players.get(server);
			
			if(array == null) {
				array = new ArrayList<>();
				array.add(Integer.parseInt(data[0]));
				this.players.put(server, array);
			} else {
				this.players.get(server).add(Integer.parseInt(data[0]));
			}
		}
	}
}