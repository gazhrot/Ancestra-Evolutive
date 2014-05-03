package fr.edofus.ancestra.evolutive.game.packet.account;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("AS")
public class SetCharacter implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int id = Integer.parseInt(packet.substring(2));
		if(client.getAccount().getPlayers().get(id) != null) {
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