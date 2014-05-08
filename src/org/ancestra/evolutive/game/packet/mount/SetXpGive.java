package org.ancestra.evolutive.game.packet.mount;


import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


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