package org.ancestra.evolutive.game.packet.object;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.ConditionParser;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.object.ObjectTemplate;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("OU")
public class Use implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int guid = -1, targetGuid = -1;
		short cellID = -1;
		Player Target = null;
		
		try {
			String[] infos = packet.substring(2).split("\\|");
			guid = Integer.parseInt(infos[0]);
			try {
				targetGuid = Integer.parseInt(infos[1]);
			} catch(Exception e) {targetGuid = -1;}
			try {
				cellID = Short.parseShort(infos[2]);
			} catch(Exception e) {cellID = -1;}
		} catch(Exception e) {return;}
		
		//Si le joueur n'a pas l'objet
		if(World.data.getPlayer(targetGuid) != null)
			Target = World.data.getPlayer(targetGuid);
		if(!client.getPlayer().hasItemGuid(guid) || client.getPlayer().getFight() != null || client.getPlayer().isAway())
			return;
		if(Target != null && (Target.getFight() != null || Target.isAway()))
			return;
		
		Object obj = World.data.getObject(guid);
		
		if(obj == null) 
			return;
		
		ObjectTemplate T = obj.getTemplate();
		
		if(!obj.getTemplate().getConditions().equalsIgnoreCase("") && !ConditionParser.validConditions(client.getPlayer(),obj.getTemplate().getConditions())) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "119|43");
			return;
		}
		
		T.applyAction(client.getPlayer(), Target, guid, cellID);
	}
}