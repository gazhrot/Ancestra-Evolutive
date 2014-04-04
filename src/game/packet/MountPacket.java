package game.packet;

import objects.Dragodinde;
import objects.Carte.MountPark;
import client.Player;

import common.SocketManager;
import common.World;

import core.Server;
import game.GameClient;
import game.packet.handler.Packet;

public class MountPacket {
	
	@Packet("Rb")
	public static void buy(GameClient client, String packet) {
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

	@Packet("Rd")
	public static void description(GameClient client, String packet) {
		int id = -1;
		try {
			id = Integer.parseInt(packet.substring(2).split("\\|")[0]);
		} catch(Exception e) {}
		
		if(id == -1)
			return;
		
		Dragodinde dragodinde = World.data.getDragoByID(id);
		
		if(dragodinde == null)
			return;
		
		SocketManager.GAME_SEND_MOUNT_DESCRIPTION_PACKET(client.getPlayer(), dragodinde);
	}
	
	@Packet("Rn")
	public static void setName(GameClient client, String packet) {
		if(client.getPlayer().getMount() == null)
			return;
		
		String name = packet.substring(2);
		client.getPlayer().getMount().setName(name);
		SocketManager.GAME_SEND_Rn_PACKET(client.getPlayer(), name);
	}
	
	@Packet("Rr")
	public static void ride(GameClient client, String packet)
	{
		if(client.getPlayer().get_lvl()<60 || client.getPlayer().getMount() == null || !client.getPlayer().getMount().isMountable() || client.getPlayer()._isGhosts) {
			SocketManager.GAME_SEND_Re_PACKET(client.getPlayer(),"Er", null);
			return;
		}
		
		client.getPlayer().toogleOnMount();
	}
	
	@Packet("Rs")
	public static void sell(GameClient client, String packet) {
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

	@Packet("Rv")
	public static void closeBuySign(GameClient client, String packet) {
		SocketManager.GAME_SEND_R_PACKET(client.getPlayer(), "v");
	}
	
	@Packet("Rx")
	public static void setXpGive(GameClient client, String packet) {
		try	{
			int xp = Integer.parseInt(packet.substring(2));
			
			if(xp <0)
				xp = 0;
			if(xp >90)
				xp = 90;
			
			client.getPlayer().setMountGiveXp(xp);
			SocketManager.GAME_SEND_Rx_PACKET(client.getPlayer());
		} catch(Exception e) {}
	}
}