package game.packet.channel;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("cC")
public class SubscribeChannels implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		String chan = String.valueOf(packet.charAt(3));
		switch(packet.charAt(2)) {
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