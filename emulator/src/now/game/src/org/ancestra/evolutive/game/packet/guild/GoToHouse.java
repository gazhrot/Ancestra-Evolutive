package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.enums.HouseRight;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("gh")
public class GoToHouse implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		
		if(client.getPlayer().getGuild() == null) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		
		if(client.getPlayer().getFight() != null || client.getPlayer().isAway())
			return;
		
		House house = World.data.getHouse(Integer.parseInt(packet));
		
		if(house == null) 
			return;
		if(client.getPlayer().getGuild().getId() != house.getGuildId()) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		
		if(!house.canDo(HouseRight.G_TELEPORT.getId())) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1136");
			return;
		}
		
		if (client.getPlayer().hasItemTemplate(8883, 1)) {
			client.getPlayer().removeByTemplateID(8883,1);
			client.getPlayer().setPosition((short) house.getToMapid(), house.getToCellid());
		} else {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1137");
			return;
		}
	}
}