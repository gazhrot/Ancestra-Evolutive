package game.packet.mount;

import objects.Carte.MountPark;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;

import common.SocketManager;

import core.Server;
import core.World;
import game.GameClient;

@Packet("Rs")
public class Sell implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_R_PACKET(client.getPlayer(), "v");//Fermeture du panneau
		int price = Integer.parseInt(packet.substring(2));
		MountPark mountPark = client.getPlayer().get_curCarte().getMountPark();
		
		if(!mountPark.getData().isEmpty()) {
			SocketManager.GAME_SEND_MESSAGE(client.getPlayer(), "[ENCLO] Impossible de vendre un enclo plein.", Server.config.getMotdColor());
			return;
		}
		if(mountPark.get_owner() == -1) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "194");
			return;
		}
		if(mountPark.get_owner() != client.getPlayer().get_GUID()) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "195");
			return;
		}
		
		mountPark.set_price(price);
		World.database.getMountparkData().update(mountPark);
		client.getPlayer().save();
		
		for(Player z:client.getPlayer().get_curCarte().getPersos())
			SocketManager.GAME_SEND_Rp_PACKET(z, mountPark);
	}
}