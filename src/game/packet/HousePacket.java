package game.packet;

import objects.House;
import game.GameClient;

public class HousePacket {

	public static void parseHousePacket(GameClient client, String packet) {
		switch(packet.charAt(1))
		{
			case 'B'://Acheter la maison
				House.HouseAchat(client.getPlayer());
			break;
			case 'G'://Maison de guilde
				packet = packet.substring(2);
				House.parseHG(client.getPlayer(), (packet.isEmpty()?null:packet));
			break;
			case 'Q'://Quitter/Expulser de la maison
				packet = packet.substring(2);
				House.Leave(client.getPlayer(), packet);
			break;
			case 'S'://Modification du prix de vente
				packet = packet.substring(2);
				House.SellPrice(client.getPlayer(), packet);
			break;
			case 'V'://Fermer fenetre d'achat
				House.closeBuy(client.getPlayer());
			break;
		}
	}
}