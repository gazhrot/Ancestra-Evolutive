package org.ancestra.evolutive.login.packet;

import org.ancestra.evolutive.exchange.ExchangeClient;
import org.ancestra.evolutive.kernel.Main;
import org.ancestra.evolutive.login.LoginClient;
import org.ancestra.evolutive.object.Account;
import org.ancestra.evolutive.object.Server;

public class ServerSelected {
	
	public static void get(LoginClient client, String packet) {
		Server server = null;
		
		try { 
			int i = Integer.parseInt(packet);
			server = Server.get(i);
		} catch(Exception e) {
			client.send("AXEr");
			client.kick();
			return;
		}
		
		if(server == null) {
			client.send("AXEr");
			return;
		}
		
		if(server.getState() != 1) {
			client.send("AXEd");
			return;
		}

		Account account = client.getAccount();
		//FIXME: Problème avec la sélection de server ?
		if(account.getSubscribeRemaining() == 0
				&& server.getSub() == 1) {
			client.send(getFreeServer());
			return;
		}
		
		int logged = Main.database.getPlayerData().isLogged(account);
		if(logged > 0 && Server.get(logged).getClient() != null) {
			account.setState(0);
			client.send("AlEd");
			client.kick();
			Server.get(logged).send("WK"+account.getUUID());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		account.setServer(server.getId());
		server.send("WA" + account.getUUID());
		
		StringBuilder sb = new StringBuilder();
		String ip = client.getIoSession().getLocalAddress().toString().replace("/", "").split("\\:")[0];

		sb
		.append("AYK")
		.append((ip.equals("127.0.0.1") ? "127.0.0.1" : server.getIp())).append(":")
		.append(server.getPort()).append(";")
		.append(account.getUUID());
		
		client.send(sb.toString());
		
		client.getAccount().setState(0);
	}
	
	private static String getFreeServer() {
		StringBuilder sb = new StringBuilder("AXEf");
		
		for(ExchangeClient client : ExchangeClient.clients.values()) {
			Server server = client.getServer();
			if(server == null) 
				continue;
			if(server.getSub() == 0 && server.getFreePlaces() <= 0)
				sb.append(server.getId()).append("|");
		}
		
		return sb.toString();
	}
}
