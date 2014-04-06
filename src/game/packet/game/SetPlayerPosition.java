package game.packet.game;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("Gp")
public class SetPlayerPosition implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null)
			return;
		
		try {
			int cell = Integer.parseInt(packet.substring(2));
			client.getPlayer().get_fight().changePlace(client.getPlayer(), cell);
		} catch(NumberFormatException e) {return;}
	}
}