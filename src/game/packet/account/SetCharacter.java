package game.packet.account;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;
import common.World;

import game.GameClient;

@Packet("AS")
public class SetCharacter implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int id = Integer.parseInt(packet.substring(2));
		if(client.getAccount().get_persos().get(id) != null) {
			client.getAccount().setGameClient(client);
			client.setPlayer(World.data.getPersonnage(id));
			if(client.getPlayer() != null) { 
				client.getPlayer().OnJoinGame();
				return;
			}
		}
		SocketManager.GAME_SEND_PERSO_SELECTION_FAILED(client);
	}
}