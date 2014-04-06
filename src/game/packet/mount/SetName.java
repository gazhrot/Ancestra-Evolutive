package game.packet.mount;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("Rn")
public class SetName implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getMount() == null)
			return;
		
		String name = packet.substring(2);
		client.getPlayer().getMount().setName(name);
		SocketManager.GAME_SEND_Rn_PACKET(client.getPlayer(), name);
	}
}