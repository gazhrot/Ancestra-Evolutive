package game.packet.fight;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("fL")
public class GetList implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_FIGHT_LIST_PACKET(client, client.getPlayer().get_curCarte());
	}
}