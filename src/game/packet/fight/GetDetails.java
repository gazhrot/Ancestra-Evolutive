package game.packet.fight;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("fD")
public class GetDetails implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int key = -1;
		try {
			key = Integer.parseInt(packet.substring(2).replace(((int)0x0)+"", ""));
		} catch(Exception e) {}
		
		if(key == -1)
			return;
		
		SocketManager.GAME_SEND_FIGHT_DETAILS(client, client.getPlayer().get_curCarte().get_fights().get(key));
	}
}