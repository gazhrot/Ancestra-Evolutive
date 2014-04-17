package game.packet.mount;

import objects.Carte.MountPark;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;

import common.SocketManager;
import common.World;

import core.Server;
import game.GameClient;

@Packet("Rb")
public class Buy implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_R_PACKET(client.getPlayer(), "v");//Fermeture du panneau
		MountPark mountPark = client.getPlayer().get_curCarte().getMountPark();
		Player seller = World.data.getPersonnage(mountPark.get_owner());
		
		if(mountPark.get_owner() == -1) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "196");
			return;
		}
		if(mountPark.get_price() == 0) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "197");
			return;
		}
		
		if(client.getPlayer().get_guild() == null) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		if(client.getPlayer().getGuildMember().getRank() != 1) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "198"); 
			return;
		}
		
		byte enclosMax = (byte)Math.floor(client.getPlayer().get_guild().get_lvl()/10);
		byte TotalEncloGuild = (byte)World.data.totalMPGuild(client.getPlayer().get_guild().get_id());
		
		if(TotalEncloGuild >= enclosMax) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1103");
			return;
		}
		if(client.getPlayer().get_kamas() < mountPark.get_price()) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "182");
			return;
		}
		
		long NewKamas = client.getPlayer().get_kamas() - mountPark.get_price();
		client.getPlayer().set_kamas(NewKamas);
		
		if(seller != null) {
			seller.setBankKamas(seller.getBankKamas() + mountPark.get_price());
			if(seller.isOnline())
				SocketManager.GAME_SEND_MESSAGE(client.getPlayer(), "Un enclo a été vendu à "+mountPark.get_price()+".", Server.config.getMotdColor());
		}
		
		mountPark.set_price(0);//On vide le prix
		mountPark.set_owner(client.getPlayer().get_GUID());
		mountPark.set_guild(client.getPlayer().get_guild());
		World.database.getMountparkData().update(mountPark);
		client.getPlayer().save();
		//On rafraichit l'enclo
		for(Player z: client.getPlayer().get_curCarte().getPersos())
			SocketManager.GAME_SEND_Rp_PACKET(z, mountPark);
	}
}