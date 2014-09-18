package org.ancestra.evolutive.game.packet.account;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;
import org.ancestra.evolutive.util.Migration;

@Packet("AL")
public class GetCharacters implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		for(Player player : client.getAccount().getPlayers().values()) {
			if(player.getFight() != null && player.getFighter() != null)	{
				client.getAccount().setCurPlayer(player);
				client.setPlayer(player);
				if(player != null) {
					player.onJoinGame();
					return;
				}
			}
		}
		
		if(packet.length() == 2) {
			String servers = World.database.getPlayerData().haveOtherPlayer(client.getAccount().getUUID());
			if(World.database.getAccountData().canMigrate(client.getAccount().getUUID()) && !servers.isEmpty()) {
				new Migration(client.getAccount().getUUID(), servers);
				Server.config.getExchangeClient().send("MP" + client.getAccount().getUUID());
				return;
			} 	
		}
		
		client.send(client.getAccount().getAccountHelper().getPlayersList());
	}
}