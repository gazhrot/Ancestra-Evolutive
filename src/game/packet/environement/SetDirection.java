package game.packet.environement;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("eD")
public class SetDirection implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try	{
			if(client.getPlayer().get_fight() != null)
				return;
			
			int dir = Integer.parseInt(packet.substring(2));
			client.getPlayer().set_orientation(dir);
			SocketManager.GAME_SEND_eD_PACKET_TO_MAP(client.getPlayer().get_curCarte(),client.getPlayer().get_GUID(),dir);
		} catch(NumberFormatException e) {return;}
	}
}