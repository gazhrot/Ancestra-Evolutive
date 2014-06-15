package org.ancestra.evolutive.game.packet.group;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Group;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("PW")
public class Where implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		
		Group group = client.getPlayer().getGroup();
		
		if(group == null)
			return;
		
		String str = "";
		boolean isFirst = true;
		
		for(Player GroupP : client.getPlayer().getGroup().getPlayers()) {
			if(!isFirst) 
				str += "|";
			str += GroupP.getMap().getX()+";"+GroupP.getMap().getY()+";"+GroupP.getMap().getId()+";2;"+GroupP.getId()+";"+GroupP.getName();
			isFirst = false;
		}
		
		SocketManager.GAME_SEND_IH_PACKET(client.getPlayer(), str);
	}
}