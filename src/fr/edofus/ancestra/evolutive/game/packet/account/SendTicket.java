package fr.edofus.ancestra.evolutive.game.packet.account;


import fr.edofus.ancestra.evolutive.common.Constants;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.Server;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

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