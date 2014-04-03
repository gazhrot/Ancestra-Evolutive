package game.packet;

import game.GameClient;

public class ChanelPacket {

	public static void parseChanelPacket(GameClient client, String packet) {
		switch(packet.charAt(1))
		{
			case 'C'://Changement des Canaux
				change(client, packet);
			break;
		}
	}
	
	private static void change(GameClient client, String packet) {
		String chan = packet.charAt(3)+"";
		switch(packet.charAt(2))
		{
			case '+'://Ajout du Canal
				client.getPlayer().addChanel(chan);
			break;
			case '-'://Desactivation du canal
				client.getPlayer().removeChanel(chan);
			break;
		}
		client.getPlayer().save();
	}
}