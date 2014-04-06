package game.packet.fight;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("fL")
public class GetList implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_FIGHT_LIST_PACKET(client, client.getPlayer().get_curCarte());
	}
}