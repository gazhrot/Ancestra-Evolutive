package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.map.MountPark;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("gf")
public class GoToMountpark implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		
		if(client.getPlayer().getGuild() == null) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		
		if(client.getPlayer().getFight() != null || client.getPlayer().isAway())
			return;
		
		short MapID = Short.parseShort(packet);
		MountPark MP = World.data.getMap(MapID).getMountPark();
		
		if(MP.getGuild().getId() != client.getPlayer().getGuild().getId()) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		
		int CellID = World.data.getEncloCellIdByMapId(MapID);
		
		if (client.getPlayer().hasItemTemplate(9035, 1)) {
			client.getPlayer().removeByTemplateID(9035,1);
			client.getPlayer().teleport(MapID, CellID);
		} else {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1159");
			return;
		}
	}
}