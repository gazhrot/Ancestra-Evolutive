package org.ancestra.evolutive.login.packet;

import org.ancestra.evolutive.kernel.Main;
import org.ancestra.evolutive.login.LoginClient;
import org.ancestra.evolutive.login.LoginClient.Status;
import org.ancestra.evolutive.object.Account;


public class ChooseNickName {
	
	public static void verify(LoginClient client, String nickname) {
		Account account = client.getAccount();
		
		if(!account.getPseudo().isEmpty()) {
			client.kick();
			return;
		}
		
		if(nickname.toLowerCase().equals(account.getName().toLowerCase())) {
			client.send("AlEr");
			return;
		}

		String s[] = {"admin", "modo", " ", "&", "é", "\"", "'", 
				"(", "-", "è", "_", "ç", "à", ")", "=", "~", "#",
				"{", "[", "|", "`", "^", "@", "]", "}", "°", "+",
				"^", "$", "ù", "*", ",", ";", ":", "!", "<", ">",
				"¨", "£", "%", "µ", "?", ".", "/", "§", "\n"};

		for(int i = 0; i < s.length; i++) {
			if(nickname.contains(s[i])) {
				client.send("AlEs");
				break;
			}
		}
	
		if(Main.database.getAccountData().exist(nickname) != null) {
			client.send("AlEs");
			return;
		}

		client.getAccount().setPseudo(nickname);
		client.setStatus(Status.SERVER);
		client.getAccount().setState(0);
		AccountQueue.verify(client);
	}
}
