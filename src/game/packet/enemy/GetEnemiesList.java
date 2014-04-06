package game.packet.enemy;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("iL")
public class GetEnemiesList implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_ENEMY_LIST(client.getPlayer());
	}
}