package org.ancestra.evolutive.game.packet.mount;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.map.MountPark;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Rs")
public class Sell implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_R_PACKET(client.getPlayer(), "v");//Fermeture du panneau
		int price = Integer.parseInt(packet.substring(2));
		MountPark mountPark = client.getPlayer().getCurMap().getMountPark();
		
		if(!mountPark.getDatas().isEmpty()) {
			SocketManager.GAME_SEND_MESSAGE(client.getPlayer(), "[ENCLO] Impossible de vendre un enclo plein.", Server.config.getMotdColor());
			return;
		}
		if(mountPark.getOwner() == -1) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "194");
			return;
		}
		if(mountPark.getOwner() != client.getPlayer().getUUID()) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "195");
			return;
		}
		
		mountPark.setPrice(price);
		World.database.getMountparkData().update(mountPark);
		client.getPlayer().save();
		
		for(Player z:client.getPlayer().getCurMap().getPlayers())
			SocketManager.GAME_SEND_Rp_PACKET(z, mountPark);
	}
}