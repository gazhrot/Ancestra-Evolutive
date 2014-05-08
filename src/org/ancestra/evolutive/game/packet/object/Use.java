package org.ancestra.evolutive.game.packet.object;



import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.ConditionParser;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.objects.Objet;
import org.ancestra.evolutive.objects.Objet.ObjTemplate;
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
		if(World.data.getPersonnage(targetGuid) != null)
			Target = World.data.getPersonnage(targetGuid);
		if(!client.getPlayer().hasItemGuid(guid) || client.getPlayer().get_fight() != null || client.getPlayer().is_away())
			return;
		if(Target != null && (Target.get_fight() != null || Target.is_away()))
			return;
		
		Objet obj = World.data.getObjet(guid);
		
		if(obj == null) 
			return;
		
		ObjTemplate T = obj.getTemplate();
		
		if(!obj.getTemplate().getConditions().equalsIgnoreCase("") && !ConditionParser.validConditions(client.getPlayer(),obj.getTemplate().getConditions())) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "119|43");
			return;
		}
		
		T.applyAction(client.getPlayer(), Target, guid, cellID);
	}
}