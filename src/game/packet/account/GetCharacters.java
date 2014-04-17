package game.packet.account;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("AL")
public class GetCharacters implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_PERSO_LIST(client, client.getAccount().get_persos());	
	}
}