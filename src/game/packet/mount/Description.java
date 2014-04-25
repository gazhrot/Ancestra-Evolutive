package game.packet.mount;

import objects.Dragodinde;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;

import common.SocketManager;
import core.World;

import game.GameClient;

@Packet("Rd")
public class Description implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
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
}