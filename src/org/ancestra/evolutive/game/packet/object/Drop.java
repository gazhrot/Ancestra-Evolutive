package org.ancestra.evolutive.game.packet.object;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("OD")
public class Drop implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int guid = -1, qua = -1;
		
		try	{
			guid = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			qua = Integer.parseInt(packet.split("\\|")[1]);
		} catch(Exception e) {}
	
		if(guid == -1 || qua <= 0 || !client.getPlayer().hasItemGuid(guid) || client.getPlayer().getFight() != null || client.getPlayer().isAway())
			return;
		
		Object obj = World.data.getObjet(guid);
		client.getPlayer().setCurCell(client.getPlayer().getCurCell());
		int cellPosition = Constants.getNearCellidUnused(client.getPlayer());
		
		if(cellPosition < 0) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1145");
			return;
		}
		if(obj.getPosition() != Constants.ITEM_POS_NO_EQUIPED) {
			obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
			SocketManager.GAME_SEND_OBJET_MOVE_PACKET(client.getPlayer(),obj);
			if(obj.getPosition() == Constants.ITEM_POS_ARME || obj.getPosition() == Constants.ITEM_POS_COIFFE ||
				obj.getPosition() == Constants.ITEM_POS_FAMILIER || obj.getPosition() == Constants.ITEM_POS_CAPE ||
				obj.getPosition() == Constants.ITEM_POS_BOUCLIER || obj.getPosition() == Constants.ITEM_POS_NO_EQUIPED)
				SocketManager.GAME_SEND_ON_EQUIP_ITEM(client.getPlayer().getCurMap(), client.getPlayer());
		}
		if(qua >= obj.getQuantity()) {
			client.getPlayer().removeItem(guid);
			client.getPlayer().getCurMap().getCases().get(client.getPlayer().getCurCell().getId()+cellPosition).setObject(obj);
			obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
			SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(client.getPlayer(), guid);
		}else {
			obj.setQuantity(obj.getQuantity() - qua);
			Object obj2 = Object.getClone(obj, qua);
			obj2.setPosition(Constants.ITEM_POS_NO_EQUIPED);
			client.getPlayer().getCurMap().getCases().get(client.getPlayer().getCurCell().getId()+cellPosition).setObject(obj2);
			SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(client.getPlayer(), obj);
		}
		
		SocketManager.GAME_SEND_Ow_PACKET(client.getPlayer());
		SocketManager.GAME_SEND_GDO_PACKET_TO_MAP(client.getPlayer().getCurMap(),'+',client.getPlayer().getCurMap().getCases().get(client.getPlayer().getCurCell().getId()+cellPosition).getId(),obj.getTemplate().getId(),0);
		SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
	}
}