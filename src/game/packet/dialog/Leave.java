package game.packet.dialog;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("DV")
public class Leave implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
		if(client.getPlayer().get_isTalkingWith() != 0)
			client.getPlayer().set_isTalkingWith(0);
	}
}