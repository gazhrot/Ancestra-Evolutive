package org.ancestra.evolutive.login.packet;

import org.ancestra.evolutive.kernel.Main;
import org.ancestra.evolutive.login.LoginClient;
import org.ancestra.evolutive.object.Account;
import org.ancestra.evolutive.object.Player;
import org.ancestra.evolutive.object.Server;

public class FriendServerList {
	
	public static void get(LoginClient client, String packet) {
		try {
			String name = Main.database.getAccountData().exist(packet);
			
			if(name == null) {
				client.send("AF");
				return;
			}
			
			Account account = Main.database.getAccountData().load(name);
			
			if(account == null) {
				client.send("AF");
				return;
			}
			
			Main.database.getPlayerData().load(account);
			
			client.send("AF" + getList(account));
		} catch(Exception e) { }
	}
	
	public static String getList(Account account) {
		StringBuilder sb = new StringBuilder();
		
		for(Server server : Server.servers.values()) {
			int i = getNumber(account, server.getId());
			if(i != 0)
				sb
				.append(server.getId()).append(",")
				.append(i).append(";");
		}
		return sb.toString();
	}
	
	public static int getNumber(Account account, int id) {
		int i = 0;
		for(Player character : account.getPlayers().values())
			if(character.getServer() != id) continue;
			else i++;
		return i;
	}
}
