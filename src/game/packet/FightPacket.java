package game.packet;

import common.SocketManager;

import game.GameClient;
import game.packet.handler.Packet;

public class FightPacket {
	
	@Packet("fD")
	public static void details(GameClient client, String packet) {
		int key = -1;
		try {
			key = Integer.parseInt(packet.substring(2).replace(((int)0x0)+"", ""));
		} catch(Exception e) {}
		
		if(key == -1)
			return;
		
		SocketManager.GAME_SEND_FIGHT_DETAILS(client, client.getPlayer().get_curCarte().get_fights().get(key));
	}
	
	@Packet("fH")
	public static void help(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null)
			return;
		client.getPlayer().get_fight().toggleHelp(client.getPlayer().get_GUID());
	}
	
	@Packet("fL")
	public static void list(GameClient client, String packet) {
		SocketManager.GAME_SEND_FIGHT_LIST_PACKET(client, client.getPlayer().get_curCarte());
	}
	
	@Packet("fN")
	public static void blockAll(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null)
			return;
		client.getPlayer().get_fight().toggleLockTeam(client.getPlayer().get_GUID());
	}
	
	@Packet("fP")
	public static void blockGroup(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null || client.getPlayer().getGroup() == null)
			return;
		client.getPlayer().get_fight().toggleOnlyGroup(client.getPlayer().get_GUID());
	}
	
	@Packet("fS")
	public static void blockSpec(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null)
			return;
		client.getPlayer().get_fight().toggleLockSpec(client.getPlayer().get_GUID());
	}
}