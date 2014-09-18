package org.ancestra.evolutive.login.packet;

import org.ancestra.evolutive.kernel.Main;
import org.ancestra.evolutive.login.LoginClient;
import org.ancestra.evolutive.login.LoginClient.Status;

public class Version {
	
	public static void verify(LoginClient client, String version) {
		if(!version.equalsIgnoreCase(Main.config.getVersion())) { 
			client.send("AlEv" + Main.config.getVersion());
			client.kick();
		}
		
		client.setStatus(Status.WAIT_ACCOUNT);
	}
}
