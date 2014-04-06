package game.packet.account;

import common.SocketManager;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("AL")
public class GetCharacters implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_PERSO_LIST(client, client.getAccount().get_persos());	
	}
}