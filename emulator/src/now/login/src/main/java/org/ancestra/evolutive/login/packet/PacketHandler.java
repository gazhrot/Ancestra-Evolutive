package org.ancestra.evolutive.login.packet;

import org.ancestra.evolutive.login.LoginClient;

public class PacketHandler {

	public static void parser(LoginClient client, String packet) {
		switch(client.getStatus()) {
		case WAIT_VERSION : Version.verify(client, packet);
			break;
			
		case WAIT_ACCOUNT : AccountName.verify(client, packet);
			break;
			
		case WAIT_PASSWORD : Password.verify(client, packet);
			break;
			
		case WAIT_NICKNAME : ChooseNickName.verify(client, packet);
			break;
			
		case SERVER : 
			switch(packet.substring(0, 2)) {
			case "AF" : FriendServerList.get(client, packet.substring(2));
				break;
				
			case "Af" : AccountQueue.verify(client);
				break;
				
			case "AX" : ServerSelected.get(client, packet.substring(2));
				break;
				
			case "Ax" : ServerList.get(client);
				break;
				
			default : 
				client.kick();
				break;
			}
			break;
			
		}
	}
}
