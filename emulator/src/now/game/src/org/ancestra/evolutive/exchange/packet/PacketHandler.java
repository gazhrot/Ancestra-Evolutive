package org.ancestra.evolutive.exchange.packet;

import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.exchange.ExchangeClient;
import org.ancestra.evolutive.util.Migration;
import org.apache.mina.core.buffer.IoBuffer;

public class PacketHandler {

	public static void parser(String packet) {	
		switch(packet.charAt(0)) {
		case 'F' : //Free places
			switch(packet.charAt(1)) {
			case '?' : //Required
				int i = 50000 - Server.config.getGameServer().getPlayerNumber();
				Server.config.getExchangeClient().send("F" + i);
				break;
			}
			break;
			
		case 'S' : //Server
			switch(packet.charAt(1)) {
			case 'H' : //Host
				switch(packet.charAt(2)) {
				case 'K' : //Ok
					break;
				}
				break;
				
			case 'K' : //Key
				switch(packet.charAt(2)) {
				case '?' : //Required
					int i = 50000 - Server.config.getGameServer().getPlayerNumber();
					Server.config.getExchangeClient().send("SK" + Server.config.getServerId() + ";" + Server.config.getServerKey() + ";" + i);
					break;
					
				case 'K' : //Ok
					ExchangeClient client = Server.config.getExchangeClient();
					client.logger.info("server accepted by the login");
					client.send("SH" + Server.config.getIp() + ";" + Server.config.getGamePort());
					break;
					
				case 'R' : //Refused
					Server.config.getExchangeClient().logger.info("server refused by the login");
					System.exit(0);
					break;
				}
				break;
			}
			break;
			
		case 'W' : //Waiting
			switch(packet.charAt(1)) {
			case 'A' : //Add
				int id = Integer.parseInt(packet.substring(2));
				Account account = World.database.getAccountData().load(id);
				System.out.println(account);
				if(account != null) {
					if(account.getCurPlayer() != null)
						World.data.getCompte(id).getGameClient().kick();
					Server.config.getGameServer().addWaitingCompte(account);
				}
				break;
			case 'K' : //Kick
				id = Integer.parseInt(packet.substring(2));
				account = World.data.getCompte(id);
				
				if(account != null)
					if(account.getGameClient() != null) 
						account.getGameClient().kick();
				break;
			}
			break;
			
		case 'M': // Migration
			switch(packet.charAt(1)) {
			case 'G': // Get
				String[] split = packet.substring(2).split("\\|");
				String account = split[0], server = split[1];
				StringBuilder alks = new StringBuilder("MT" + account + "|" + server);
				
				for(String id : split[2].split("\\,")) {
					alks.append(World.data.getPlayer(Integer.parseInt(id)).parseALK());
				}
				
				Server.config.getExchangeClient().send(alks.toString());
			break;
			
			case 'F': // Finish
				split = packet.substring(2).split("\\|");
				int id = Integer.parseInt(split[0]);
				int sender = Integer.parseInt(split[1]);
				String players = packet.substring(packet.indexOf("|" , packet.indexOf("|") + 1) + 1);

				Migration.migrations.get(id).add(sender, "|" + players);
				break;
				
			case 'D': //Delete
				int player = Integer.parseInt(packet.substring(2));
				World.data.deletePerso(World.data.getPlayer(player));
				break;
				
			case 'O': // Okey
				split = packet.substring(2).split("\\|");
				player = Integer.parseInt(split[0]);
				server = split[1];
				
				IoBuffer buffer = IoBuffer.allocate(2048);				
				buffer.put(String.valueOf("MS" + server + "|").getBytes());
				buffer.putObject(World.data.getPlayer(player).serialize());
				buffer.flip();
							
				Server.config.getExchangeClient().send(buffer);
				break;
			}
			break;
		}
	}
}
