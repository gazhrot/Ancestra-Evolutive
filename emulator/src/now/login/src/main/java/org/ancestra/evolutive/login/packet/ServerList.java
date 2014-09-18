package org.ancestra.evolutive.login.packet;

import org.ancestra.evolutive.login.LoginClient;
import org.ancestra.evolutive.object.Account;
import org.ancestra.evolutive.object.Player;
import org.ancestra.evolutive.object.Server;

public class ServerList {
	
	public static void get(LoginClient client) {
		client.send("AxK" + serverList(client.getAccount()));
	}
	
	public static String serverList(Account account) {
		StringBuilder sb = new StringBuilder(account.getSubscribeRemaining()+"");
		
		for(Server server : Server.servers.values()) {
			int i = characterNumber(account, server.getId());
			if(i == 0) continue;
			
			sb
			.append("|").append(server.getId())
			.append(",").append(i);
		}
		return sb.toString();
	}
	
	public static  int characterNumber(Account account, int server) {
		int i = 0;
		
		for(Player character : account.getPlayers().values())
			if(character.getServer() == server) i++;
		
		return i;
	}
}
