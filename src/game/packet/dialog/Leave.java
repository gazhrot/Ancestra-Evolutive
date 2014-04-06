package game.packet.dialog;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("DV")
public class Leave implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
		if(client.getPlayer().get_isTalkingWith() != 0)
			client.getPlayer().set_isTalkingWith(0);
	}
}