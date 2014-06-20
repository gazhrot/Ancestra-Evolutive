package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.fight.Fight;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("GI")
public class Information implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getFight() != null) {
			//Only percepteur
			SocketManager.GAME_SEND_MAP_GMS_PACKETS(client.getPlayer().getCurMap(), client.getPlayer());
			SocketManager.GAME_SEND_GDK_PACKET(client);
			return;
		}
		//Enclos
		SocketManager.GAME_SEND_Rp_PACKET(client.getPlayer(), client.getPlayer().getCurMap().getMountPark());
		//Maisons
		House.load(client.getPlayer(), client.getPlayer().getCurMap().getId());
		//Objets sur la carte
		SocketManager.GAME_SEND_MAP_GMS_PACKETS(client.getPlayer().getCurMap(), client.getPlayer());
		SocketManager.GAME_SEND_MAP_MOBS_GMS_PACKETS(client.getPlayer().getAccount().getGameClient(), client.getPlayer().getCurMap());
		SocketManager.GAME_SEND_MAP_NPCS_GMS_PACKETS(client, client.getPlayer().getCurMap());
		SocketManager.GAME_SEND_MAP_PERCO_GMS_PACKETS(client, client.getPlayer().getCurMap());
		SocketManager.GAME_SEND_MAP_OBJECTS_GDS_PACKETS(client, client.getPlayer().getCurMap());
		SocketManager.GAME_SEND_GDK_PACKET(client);
		SocketManager.GAME_SEND_MAP_FIGHT_COUNT(client, client.getPlayer().getCurMap());
		SocketManager.GAME_SEND_MERCHANT_LIST(client.getPlayer(), client.getPlayer().getCurMap().getId());
		//Les drapeau de combats
		Fight.FightStateAddFlag(client.getPlayer().getCurMap(), client.getPlayer());
		//items au sol
		client.getPlayer().getCurMap().sendFloorItems(client.getPlayer());
	}
}