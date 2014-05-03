package fr.edofus.ancestra.evolutive.game.packet.enemy;



import fr.edofus.ancestra.evolutive.client.Account;
import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("iD")
public class RemoveEnemy implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		
		int guid = -1;
		
		switch(packet.charAt(2))
		{
			case '%'://Nom de perso
				packet = packet.substring(3);
				Player player = World.data.getPersoByName(packet);
				if(player == null) {
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = player.getAccID();
				
			break;
			case '*'://Pseudo
				packet = packet.substring(3);
				Account account = World.data.getCompteByPseudo(packet);
				if(account== null)	{
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = account.getUUID();
			break;
			default:
				packet = packet.substring(2);
				player = World.data.getPersoByName(packet);
				if(player == null?true:!player.isOnline()) {
					SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
					return;
				}
				guid = player.getAccount().getUUID();
			break;
		}
		if(guid == -1 || !client.getAccount().isEnemyWith(guid)) {
			SocketManager.GAME_SEND_FD_PACKET(client.getPlayer(), "Ef");
			return;
		}
		client.getAccount().removeEnemy(guid);
	}
}