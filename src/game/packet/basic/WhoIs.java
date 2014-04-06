package game.packet.basic;

import client.Player;

import common.SocketManager;
import common.World;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("BW")
public class WhoIs implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		Player T = World.data.getPersoByName(packet);
		if(T == null) 
			return;
		SocketManager.GAME_SEND_BWK(client.getPlayer(), T.get_compte().get_pseudo()+"|1|"+T.get_name()+"|-1");
	
	}
}