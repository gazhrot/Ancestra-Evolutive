package game.packet.object;

import objects.Objet;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;

import common.SocketManager;
import common.World;

import game.GameClient;

@Packet("Od")
public class Delete implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try	{
			String[] infos = packet.substring(2).split("\\|");
			int guid = Integer.parseInt(infos[0]), qua = 1;
			
			try	{
				qua = Integer.parseInt(infos[1]);
			} catch(Exception e) {}
			
			Objet obj = World.data.getObjet(guid);
			if(obj == null || !client.getPlayer().hasItemGuid(guid) || qua <= 0 || client.getPlayer().get_fight() != null || client.getPlayer().is_away()) {
				SocketManager.GAME_SEND_DELETE_OBJECT_FAILED_PACKET(client);
				return;
			}
			
			int newQua = obj.getQuantity() - qua;
			
			if(newQua <=0) {
				client.getPlayer().removeItem(guid);
				World.data.removeItem(guid);
				World.database.getItemData().delete(obj);
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(client.getPlayer(), guid);
			}else {
				obj.setQuantity(newQua);
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(client.getPlayer(), obj);
			}
			
			SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
			SocketManager.GAME_SEND_Ow_PACKET(client.getPlayer());
		}catch(Exception e) {
			SocketManager.GAME_SEND_DELETE_OBJECT_FAILED_PACKET(client);
		}
	}
}