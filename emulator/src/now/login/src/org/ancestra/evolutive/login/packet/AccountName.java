package org.ancestra.evolutive.login.packet;

import org.ancestra.evolutive.kernel.Main;
import org.ancestra.evolutive.login.LoginClient;
import org.ancestra.evolutive.login.LoginServer;
import org.ancestra.evolutive.login.LoginClient.Status;

public class AccountName {
	
	public static void verify(LoginClient client, String name) {
		try {
			client.setAccount(Main.database.getAccountData().load(name.toLowerCase()));
			client.getAccount().setClient(client);
		} catch(Exception e) {
			client.send("AlEf");
			client.kick();
			return;
		}
		
		if(client.getAccount() == null) { 
			client.send("AlEf");
			client.kick();
			return;
		}	
		
		if(LoginServer.clients.containsKey(name))
			LoginServer.clients.get(name).kick();
		
		LoginServer.clients.put(name, client);
		client.setStatus(Status.WAIT_PASSWORD);
	}
}
