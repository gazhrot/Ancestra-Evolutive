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
		int playerId = Integer.parseInt(split[0]);
		String answer = split.length>1?split[1]:"";
		if(client.getAccount().deletePlayer(playerId,answer)) {
            client.send(client.getAccount().getAccountHelper().getPlayersList());
        }
        else {
            client.send("ADE");
        }
	}
}