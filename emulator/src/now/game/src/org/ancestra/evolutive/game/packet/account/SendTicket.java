package org.ancestra.evolutive.game.packet.account;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("AT")
public class SendTicket implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int guid = Integer.parseInt(packet.substring(2));
		client.setAccount(Server.config.getGameServer().getWaitingCompte(guid));
		
		if(client.getAccount() != null) {
			String ip = Constants.getIp(client.getSession().getRemoteAddress().toString());

			client.getAccount().setGameClient(client);
			client.getAccount().setCurIp(ip);
			client.getAccount().setLogged(true);
			
			World.database.getAccountData().update(client.getAccount());
			Server.config.getGameServer().delWaitingCompte(client.getAccount());
			client.send("ATK0");
		} else {
			client.send("ATE");
		}
	}
}