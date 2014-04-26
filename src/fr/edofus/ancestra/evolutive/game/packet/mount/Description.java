package fr.edofus.ancestra.evolutive.game.packet.mount;



import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.Dragodinde;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

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