package fr.edofus.ancestra.evolutive.game.packet.group;



import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.client.Player.Group;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("PA")
public class Accept implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().getInvitation() == 0)
			return;
		
		Player player = World.data.getPersonnage(client.getPlayer().getInvitation());
		
		if(player == null)
			return;
		
		Group group = player.getGroup();
		
		if(group == null) {
			group = new Group(player, client.getPlayer());
			SocketManager.GAME_SEND_GROUP_CREATE(client, group);
			SocketManager.GAME_SEND_PL_PACKET(client, group);
			SocketManager.GAME_SEND_GROUP_CREATE(player.getAccount().getGameClient(),group);
			SocketManager.GAME_SEND_PL_PACKET(player.getAccount().getGameClient(), group);
			player.setGroup(group);
			SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(player.getAccount().getGameClient(), group);
		}else {
			SocketManager.GAME_SEND_GROUP_CREATE(client, group);
			SocketManager.GAME_SEND_PL_PACKET(client, group);
			SocketManager.GAME_SEND_PM_ADD_PACKET_TO_GROUP(group, client.getPlayer());
			group.addPerso(client.getPlayer());
		}
		
		client.getPlayer().setGroup(group);
		SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(client, group);
		SocketManager.GAME_SEND_PR_PACKET(player);
	}
}