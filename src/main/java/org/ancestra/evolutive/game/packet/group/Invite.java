package org.ancestra.evolutive.game.packet.group;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

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
		if(client.getPlayer().getGroup() != null && client.getPlayer().getGroup().getPlayers().size() == 8) {
			SocketManager.GAME_SEND_GROUP_INVITATION_ERROR(client, "f");
			return;
		}
		
		target.setInviting(client.getPlayer().getId());
		client.getPlayer().setInviting(target.getId());
		SocketManager.GAME_SEND_GROUP_INVITATION(client, client.getPlayer().getName(),name);
		SocketManager.GAME_SEND_GROUP_INVITATION(target.getAccount().getGameClient(), client.getPlayer().getName(),name);
	}
}