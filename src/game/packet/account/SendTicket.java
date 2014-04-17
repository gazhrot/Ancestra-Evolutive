package game.packet.account;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.Constants;
import common.SocketManager;
import common.World;

import core.Server;
import game.GameClient;

@Packet("AT")
public class SendTicket implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int guid = Integer.parseInt(packet.substring(2));
		client.setAccount(Server.config.getGameServer().getWaitingCompte(guid));
		
		if(client.getAccount() != null && client.getAccount().get_curPerso() == null) {
			String ip = Constants.getIp(client.getSession().getRemoteAddress().toString());
			client.getAccount().setRealmThread(null);
			client.getAccount().setGameClient(client);
			client.getAccount().setCurIP(ip);
			client.getAccount().setLogged(true);
			
			World.database.getAccountData().update(client.getAccount());
			Server.config.getGameServer().delWaitingCompte(client.getAccount());
			SocketManager.GAME_SEND_ATTRIBUTE_SUCCESS(client);
		} else {
			SocketManager.GAME_SEND_ATTRIBUTE_FAILED(client);
		}
	}
}