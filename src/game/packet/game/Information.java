package game.packet.game;

import objects.Fight;
import objects.House;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;

import common.SocketManager;

import game.GameClient;

@Packet("GI")
public class Information implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() != null) {
			//Only percepteur
			SocketManager.GAME_SEND_MAP_GMS_PACKETS(client.getPlayer().get_curCarte(), client.getPlayer());
			SocketManager.GAME_SEND_GDK_PACKET(client);
			return;
		}
		//Enclos
		SocketManager.GAME_SEND_Rp_PACKET(client.getPlayer(), client.getPlayer().get_curCarte().getMountPark());
		//Maisons
		House.LoadHouse(client.getPlayer(), client.getPlayer().get_curCarte().get_id());
		//Objets sur la carte
		SocketManager.GAME_SEND_MAP_GMS_PACKETS(client.getPlayer().get_curCarte(), client.getPlayer());
		SocketManager.GAME_SEND_MAP_MOBS_GMS_PACKETS(client.getPlayer().get_compte().getGameClient(), client.getPlayer().get_curCarte());
		SocketManager.GAME_SEND_MAP_NPCS_GMS_PACKETS(client, client.getPlayer().get_curCarte());
		SocketManager.GAME_SEND_MAP_PERCO_GMS_PACKETS(client, client.getPlayer().get_curCarte());
		SocketManager.GAME_SEND_MAP_OBJECTS_GDS_PACKETS(client, client.getPlayer().get_curCarte());
		SocketManager.GAME_SEND_GDK_PACKET(client);
		SocketManager.GAME_SEND_MAP_FIGHT_COUNT(client, client.getPlayer().get_curCarte());
		SocketManager.GAME_SEND_MERCHANT_LIST(client.getPlayer(), client.getPlayer().get_curCarte().get_id());
		//Les drapeau de combats
		Fight.FightStateAddFlag(client.getPlayer().get_curCarte(), client.getPlayer());
		//items au sol
		client.getPlayer().get_curCarte().sendFloorItems(client.getPlayer());
	}
}