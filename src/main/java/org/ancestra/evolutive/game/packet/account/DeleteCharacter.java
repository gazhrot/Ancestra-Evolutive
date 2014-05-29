package org.ancestra.evolutive.game.packet.account;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

import java.util.Map;

@Packet("AD")
public class DeleteCharacter implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		String[] split = packet.substring(2).split("\\|");
		int GUID = Integer.parseInt(split[0]);
		String reponse = split.length>1?split[1]:"";
		Map<Integer, Player> players = client.getAccount().getPlayers();
		
		if(players.containsKey(GUID))
		{
			if(players.get(GUID).getLevel() <20 ||(players.get(GUID).getLevel() >=20 && reponse.equals(client.getAccount().getAnswer())))
			{
				client.getAccount().deletePerso(GUID);
				SocketManager.GAME_SEND_PERSO_LIST(client, players);
			}else {
				SocketManager.GAME_SEND_DELETE_PERSO_FAILED(client);
			}
		}else {
			SocketManager.GAME_SEND_DELETE_PERSO_FAILED(client);
		}
	}
}