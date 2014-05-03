package fr.edofus.ancestra.evolutive.game.packet.basic;



import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("BW")
public class WhoIs implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		Player player = World.data.getPersoByName(packet);
		if(player == null) 
			return;
		SocketManager.GAME_SEND_BWK(client.getPlayer(), player.getAccount().getPseudo()+"|1|"+player.get_name()+"|"+player.get_curCarte().getSubArea().get_area().get_id());
	}
}