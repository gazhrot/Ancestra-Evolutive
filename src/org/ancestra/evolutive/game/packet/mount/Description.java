package org.ancestra.evolutive.game.packet.mount;



import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.objects.Dragodinde;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


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