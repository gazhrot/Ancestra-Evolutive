package game.packet.mount;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import game.GameClient;

@Packet("Rx")
public class SetXpGive implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
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