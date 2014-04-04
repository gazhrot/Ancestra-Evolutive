package game.packet;

import game.GameClient;
import game.packet.handler.Packet;

public class ChanelPacket {
	
	@Packet("cC")
	public static void change(GameClient client, String packet) {
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