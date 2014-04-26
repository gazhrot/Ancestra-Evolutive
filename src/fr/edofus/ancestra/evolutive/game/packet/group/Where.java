package fr.edofus.ancestra.evolutive.game.packet.group;



import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.client.Player.Group;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("PW")
public class Where implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		
		Group group = client.getPlayer().getGroup();
		
		if(group == null)
			return;
		
		String str = "";
		boolean isFirst = true;
		
		for(Player GroupP : client.getPlayer().getGroup().getPersos()) {
			if(!isFirst) 
				str += "|";
			str += GroupP.get_curCarte().getX()+";"+GroupP.get_curCarte().getY()+";"+GroupP.get_curCarte().get_id()+";2;"+GroupP.get_GUID()+";"+GroupP.get_name();
			isFirst = false;
		}
		
		SocketManager.GAME_SEND_IH_PACKET(client.getPlayer(), str);
	}
}