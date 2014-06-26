package org.ancestra.evolutive.game.packet.account;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("AL")
public class GetCharacters implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		for(Player player : client.getAccount().getPlayers().values()) {
			if(player.getFight() != null && player.getFight().getFighterByPerso(player) != null)	{	
				client.getAccount().setCurPlayer(player);
				client.setPlayer(player);
				if(player != null) {
					player.onJoinGame();
					return;
				}
			}
		}	
		
		client.send(client.getAccount().getAccountHelper().getPlayersList());
	}
}