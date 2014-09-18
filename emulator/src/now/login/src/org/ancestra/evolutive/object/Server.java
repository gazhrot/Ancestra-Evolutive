package org.ancestra.evolutive.object;

import java.util.*;

import org.ancestra.evolutive.exchange.ExchangeClient;
import org.ancestra.evolutive.login.LoginHandler;

public class Server {
	
	private int id, port, state, pop, sub, freePlaces;
	private String ip, key;
	private ExchangeClient client;
	
	public static Map<Integer, Server> servers = new HashMap<>();
	
	public Server(int id, String key, int pop, int sub) {
		this.id = id;
		this.key = key;
		this.state = 0;
		this.pop = pop;
		this.sub = sub;
		
		servers.put(this.id, this);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
		sendHostListToAll();
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public int getPop() {
		return pop;
	}
	
	public void setPop(int pop) {
		this.pop = pop;
	}
	
	public int getSub() {
		return sub;
	}
	
	public void setSub(int sub) {
		this.sub = sub;
	}
	
	public ExchangeClient getClient() {
		return client;
	}
	
	public void setClient(ExchangeClient client) {
		this.client = client;
	}
	
	public int getFreePlaces() {
		return freePlaces;
	}
	
	public void setFreePlaces(int freePlaces) {
		this.freePlaces = freePlaces;
	}
	
	public static Server get(int id) {
		if(!servers.containsKey(id)) return null;
		else return servers.get(id);
	}
	
	public void send(Object arg0) {
		if(arg0 instanceof String) {
			this.getClient().send((String) arg0);
		} else {
			this.getClient().getIoSession().write(arg0);
		}
	}
	
	public static void sendHostListToAll() {
		LoginHandler.sendToAll(getHostList());
	}
	
	public static String getHostList() {
		StringBuilder sb = new StringBuilder("AH");
		
		ArrayList<Server> list = new ArrayList<Server>();
		list.addAll(Server.servers.values());

		for(Server server: list) {
			sb
			.append(server.getId())
			.append(";")
			.append(server==null?0:server.getState()).append(";110;1|");	
		}

		return sb.toString();
	}
}
