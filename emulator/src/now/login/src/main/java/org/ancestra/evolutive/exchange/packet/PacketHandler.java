package org.ancestra.evolutive.exchange.packet;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.ancestra.evolutive.exchange.ExchangeClient;
import org.ancestra.evolutive.kernel.Main;
import org.ancestra.evolutive.object.Server;

public class PacketHandler {
	
	public static void parser(ExchangeClient client, String packet) {
		try { 			
			switch(packet.charAt(0)) {
			case 'F' : // Free places
				int freePlaces = Integer.parseInt(packet.substring(1));
				client.getServer().setFreePlaces(freePlaces);
				break;
				
			case 'S' : // Server				
				switch(packet.charAt(1)) {
				case 'H' : // Host
					Server server = client.getServer();
					String[] s = packet.substring(2).split("\\;");
					server.setIp(s[0]);
					server.setPort(Integer.parseInt(s[1]));
					server.setState(1);
					client.send("SHK");
					break;
					
				case 'K' : // Key
					s = packet.substring(2).split("\\;");
					int id = Integer.parseInt(s[0]);
					String key = s[1];
					freePlaces = Integer.parseInt(s[2]);
					
					server = Server.get(id);
					
					if(!server.getKey().equals(key)) {
						client.send("SKR");
						client.kick();
					}
					
					server.setClient(client);
					client.setServer(server);
					server.setFreePlaces(freePlaces);
					client.send("SKK");
					break;
					
				case 'S': // Statut
					if(client.getServer() == null)
						return;
					
					int statut = Integer.parseInt(packet.substring(2));
					client.getServer().setState(statut);
					break;
				}
				break;
				
			case 'M': // Migration
				switch(packet.charAt(1)) {		
				case 'P':// Players
					int id = Integer.parseInt(packet.substring(2));
					Map<Server, ArrayList<Integer>> map = Main.database.getPlayerData().loadAllPlayersByAccountId(client.getServer().getId(), id);
					
					for(Entry<Server, ArrayList<Integer>> entry : map.entrySet()) {
						String players = "";
						for(Integer i : entry.getValue())
							players += (players.isEmpty() ? String.valueOf(i) : "," + i);
						
						entry.getKey().send("MG" + id + "|" + client.getServer().getId() + "|" + players);
					}
					break;
					
				case 'T': // Take
					String[] split = packet.substring(2).split("\\|");
					
					String account = split[0], players = packet.substring(packet.indexOf("|" , packet.indexOf("|") + 1) + 1);
					Server server = Server.get(Integer.parseInt(split[1]));

					if(server == null)
						return;
					
					server.send("MF" + account + "|" + client.getServer().getId() + "|" + players);	
					break;
					
				case 'D': // Delete
					split = packet.substring(2).split("\\|");
					server = Server.get(Integer.parseInt(split[1]));
					
					if(server == null)
						return;
					
					server.send("MD" + split[0]);
					break;
					
				case 'O': // Okey
					split = packet.substring(2).split("\\|");
					server = Server.get(Integer.parseInt(split[1]));
					
					if(server == null)
						return;
					
					server.send("MO" + split[0] + "|" + client.getServer().getId());
					break;
				}
				break;
				
			default : 
				client.send("Packet undefined\"" + packet + "\"");
				client.kick();
				break;
			}
		} catch(Exception e) {
			e.printStackTrace();
			client.kick();
		}
	}
}
