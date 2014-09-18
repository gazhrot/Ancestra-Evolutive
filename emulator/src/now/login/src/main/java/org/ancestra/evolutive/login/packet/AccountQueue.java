package org.ancestra.evolutive.login.packet;

import org.ancestra.evolutive.kernel.Console;
import org.ancestra.evolutive.kernel.Main;
import org.ancestra.evolutive.login.LoginClient;
import org.ancestra.evolutive.login.LoginClient.Status;
import org.ancestra.evolutive.object.Account;
import org.ancestra.evolutive.object.Server;

public class AccountQueue {
	
	/** @author Locos :
	 * AlEa : Déjà en connexion. Veuillez réessayer
	 * AlEb : Connexion refusée. Ton compte a été banni.
	 * AlEc : Ce compte est déjà connecté à un serveur de jeu. Veuillez réessayer.
	 * AlEd : Tu viens de déconnecter un personnage utilisant déjà ce compte.
	 * AlEe : ATTENTION : Vous devez utilisez vos identifiants ANkama Games ! ...
	 * AlEf : Connexion refusée. Nom de compte ou mot de passe incorrect. 
	 */
	  
	public static void verify(LoginClient client) {
		Account account = client.getAccount();
		byte state = Main.database.getAccountData().load(account.getName()).getState();
		
		if(Main.database.getAccountData().isBanned(client.getIoSession().getRemoteAddress()
				.toString().replace("/", "").split(":")[0]) || account.isBanned())
			state = 3;
		
		switch(state) {
		case 0 : //disconnected
			account.setState(1);
			sendInformation(client);
			break;
			
		case 1 : //in login
			account.setState(0);
			try { 
				int logged = Main.database.getPlayerData().isLogged(account);
				if(logged == 0) {
					client.kick();
					return;
				}
				Server.get(logged).send("WK"+account.getUUID());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			} catch(Exception e) { }
			account.setState(1);
			sendInformation(client);
			return;
			
		case 2 : //in game		
			account.setState(0);
			int logged = Main.database.getPlayerData().isLogged(account);
			if(logged != 0)
				Server.get(logged).send("WK"+account.getUUID());
			
			client.send("AlEd");
			client.kick();
			return;
			
		case 3 : //banned
			client.send("AlEb");
			client.kick();
			return;
			
		default : //unknown
			Console.instance.write("state " + state +" not found for account " + account.getName());
			break;
		}	
	}
	
	public static void sendInformation(LoginClient client) {
		Account account = client.getAccount();
		
		if(account.getPseudo().isEmpty()) {
			client.send("AlEr");
			client.setStatus(Status.WAIT_NICKNAME);
			return;
		}
		
		Account.add(account);
		Main.database.getPlayerData().load(account);
		
		client.send("Af0|0|0|1|-1");
		client.send("Ad" + account.getPseudo());
		client.send("Ac0");
		client.send(Server.getHostList());
		client.send("AlK" + (account.getRank()!=0?1:0));
		client.send("AQ" + account.getQuestion());
	}
}
