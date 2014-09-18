package org.ancestra.evolutive.object;

import java.util.HashMap;
import java.util.Map;

import org.ancestra.evolutive.kernel.Main;
import org.ancestra.evolutive.login.LoginClient;

public class Account {

	private int UUID, server;
	private String name, pass, pseudo, question;
	private byte rank, state;
	private LoginClient client;
	private long subscribe;
	private boolean banned = false;
	
	public static Map<String, Account> accounts = new HashMap<>();
	private Map<Integer, Player> players = new HashMap<>();
	
	public Account(int UUID, String name, String pass, String pseudo, 
			String question, byte rank, byte state, long subscribe, byte banned) {
		this.UUID = UUID;
		this.name = name;
		this.pass = pass;
		this.pseudo = pseudo;
		this.question = question;
		this.rank = rank;
		this.state = state;
		this.subscribe = subscribe;
		this.banned = (banned != 0);
	}
	
	public int getUUID() {
		return UUID;
	}
	
	public void setUUID(int UUID) {
		this.UUID = UUID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPass() {
		return pass;
	}
	
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public String getPseudo() {
		return pseudo;
	}
	
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public byte getRank() {
		return rank;
	}
	
	public void setRank(byte rank) {
		this.rank = rank;
	}
	
	public LoginClient getClient() {
		return client;
	}
	
	public void setClient(LoginClient client) {
		this.client = client;
	}
	
	public byte getState() {
		return this.state;
	}
	
	public void setState(int state) {
		this.state = (byte) state;
		Main.database.getAccountData().update(this);
	}
	
	public static boolean add(Account account) {
		if(accounts.containsKey(account.getName()))return true;
		accounts.put(account.getName(), account); return false;
	}
	
	public static Account get(String name) {
		if(!accounts.containsKey(name)) return null;
		return accounts.get(name);
	}
	
	public int getServer() {
		return server;
	}
	
	public void setServer(int server) {
		this.server = server;
	}
	
	public long getSubscribeRemaining() {
		long remaining = (subscribe - System.currentTimeMillis());
		return remaining<=0?0:remaining;
	}
	
	public long getSubscribe() {
		long remaining = (subscribe - System.currentTimeMillis());
		return remaining<=0?0:subscribe;
	}
	
	public void setSubscribe(long subscribe) {
		this.subscribe = subscribe;
	}
	
	public boolean isSubscribes() {
		return this.getSubscribeRemaining()==0?false:true;
	}
	
	public void setBanned(boolean banned) {
		this.banned = banned;
	}
	
	public boolean isBanned() {
		return this.banned;
	}
	
	public void addPlayer(Player player) {
		if(players.containsKey(player.getId()))
			return;
		players.put(player.getId(), player);
	}
	
	public void delPlayer(Player player) {
		if(!players.containsKey(player.getId())) 
			return;
		players.remove(player.getId());
	}
	
	public Map<Integer, Player> getPlayers() {
		return players;
	}
}
