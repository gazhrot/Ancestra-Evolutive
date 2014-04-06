package game.packet.exchange;

import common.SocketManager;
import common.World;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

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
			if(World.data.getObjet(guid) == null || World.data.getObjet(guid).getQuantity() < qua) {
				return;
			}
			
			client.getPlayer().sellItem(guid, qua);
		} catch(Exception e) {
			SocketManager.GAME_SEND_SELL_ERROR_PACKET(client);
		}
	}
}