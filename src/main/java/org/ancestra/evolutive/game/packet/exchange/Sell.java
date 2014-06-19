package org.ancestra.evolutive.game.packet.exchange;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("ES")
public class Sell implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try {
			String[] infos = packet.substring(2).split("\\|");
			int guid = Integer.parseInt(infos[0]);
			int qua = Integer.parseInt(infos[1]);
			
			if(!client.getPlayer().hasItemGuid(guid)) {
				SocketManager.GAME_SEND_SELL_ERROR_PACKET(client);
				return;
			} else 
			if(World.data.getObject(guid) == null || World.data.getObject(guid).getQuantity() < qua) {
				return;
			}
			
			client.getPlayer().sellItem(guid, qua);
		} catch(Exception e) {
			SocketManager.GAME_SEND_SELL_ERROR_PACKET(client);
		}
	}
}