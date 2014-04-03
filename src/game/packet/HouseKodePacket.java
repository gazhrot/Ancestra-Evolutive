package game.packet;

import objects.House;
import objects.Trunk;
import game.GameClient;

public class HouseKodePacket {

	public static void parseHouseKodePacket(GameClient client, String packet) {
		switch(packet.charAt(1))
		{
			case 'V'://Fermer fenetre du code
				House.closeCode(client.getPlayer());
			break;
			case 'K'://Envoi du code
				code(client, packet);
			break;
		}
	}
	
	private static void code(GameClient client, String packet) {
		switch(packet.charAt(2))
		{
			case '0'://Envoi du code
				packet = packet.substring(4);
				if(client.getPlayer().getInTrunk() != null)
					Trunk.OpenTrunk(client.getPlayer(), packet, false);
				else
					House.OpenHouse(client.getPlayer(), packet, false);
			break;
			case '1'://Changement du code
				packet = packet.substring(4);
				if(client.getPlayer().getInTrunk() != null)
					Trunk.LockTrunk(client.getPlayer(), packet);
				else
				    House.LockHouse(client.getPlayer(), packet);
			break;
		}
	}
}