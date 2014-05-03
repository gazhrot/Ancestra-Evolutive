package fr.edofus.ancestra.evolutive.game.packet.group;



import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("PI")
public class Invite implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		
		String name = packet.substring(2);
		Player target = World.data.getPersoByName(name);
		
		if(target == null)
			return;
		if(!target.isOnline()) {
			SocketManager.GAME_SEND_GROUP_INVITATION_ERROR(client, "n"+name);
			return;
		}
		if(target.getGroup() != null) {
			SocketManager.GAME_SEND_GROUP_INVITATION_ERROR(client, "a"+name);
			return;
		}
		if(client.getPlayer().getGroup() != null && client.getPlayer().getGroup().getPersosNumber() == 8) {
			SocketManager.GAME_SEND_GROUP_INVITATION_ERROR(client, "f");
			return;
		}
		
		target.setInvitation(client.getPlayer().get_GUID());	
		client.getPlayer().setInvitation(target.get_GUID());
		SocketManager.GAME_SEND_GROUP_INVITATION(client, client.getPlayer().get_name(),name);
		SocketManager.GAME_SEND_GROUP_INVITATION(target.getAccount().getGameClient(), client.getPlayer().get_name(),name);
	}
}