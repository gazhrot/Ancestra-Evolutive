package fr.edofus.ancestra.evolutive.game.packet.group;



import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("PR")
public class Decline implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().getInvitation() == 0)
			return;
		
		SocketManager.GAME_SEND_BN(client);		
		Player player = World.data.getPersonnage(client.getPlayer().getInvitation());
		
		if(player != null) 
			SocketManager.GAME_SEND_PR_PACKET(player);
		
		player.setInvitation(0);
		client.getPlayer().setInvitation(0);
	}
}