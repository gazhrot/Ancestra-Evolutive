package org.ancestra.evolutive.game.packet.mount;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.map.MountPark;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Rb")
public class Buy implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_R_PACKET(client.getPlayer(), "v");//Fermeture du panneau
		MountPark mountPark = client.getPlayer().getCurMap().getMountPark();
		Player seller = World.data.getPersonnage(mountPark.getOwner());
		
		if(mountPark.getOwner() == -1) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "196");
			return;
		}
		if(mountPark.getPrice() == 0) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "197");
			return;
		}
		
		if(client.getPlayer().getGuild() == null) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		if(client.getPlayer().getGuildMember().getRank() != 1) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "198"); 
			return;
		}
		
		byte enclosMax = (byte)Math.floor(client.getPlayer().getGuild().getLevel()/10);
		byte TotalEncloGuild = (byte)World.data.totalMPGuild(client.getPlayer().getGuild().getId());
		
		if(TotalEncloGuild >= enclosMax) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1103");
			return;
		}
		if(client.getPlayer().getKamas() < mountPark.getPrice()) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "182");
			return;
		}
		
		long NewKamas = client.getPlayer().getKamas() - mountPark.getPrice();
		client.getPlayer().setKamas(NewKamas);
		
		if(seller != null) {
			seller.setBankKamas(seller.getBankKamas() + mountPark.getPrice());
			if(seller.isOnline())
				SocketManager.GAME_SEND_MESSAGE(client.getPlayer(), "Un enclo a été vendu à "+mountPark.getPrice()+".", Server.config.getMotdColor());
		}
		
		mountPark.setPrice(0);//On vide le prix
		mountPark.setOwner(client.getPlayer().getUUID());
		mountPark.setGuild(client.getPlayer().getGuild());
		World.database.getMountparkData().update(mountPark);
		client.getPlayer().save();
		//On rafraichit l'enclo
		for(Player z: client.getPlayer().getCurMap().getPlayers())
			SocketManager.GAME_SEND_Rp_PACKET(z, mountPark);
	}
}