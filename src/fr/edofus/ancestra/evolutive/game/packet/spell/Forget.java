package fr.edofus.ancestra.evolutive.game.packet.spell;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.Log;
import fr.edofus.ancestra.evolutive.core.Server;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("SF")
public class Forget implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(!client.getPlayer().isForgetingSpell())
			return;
		
		int id = Integer.parseInt(packet.substring(2));
		
		if(Server.config.isDebug()) 
			Log.addToLog("Info: "+client.getPlayer().get_name()+": Tente Oublie sort id="+id);	
		if(client.getPlayer().forgetSpell(id)) {
			if(Server.config.isDebug()) 
				Log.addToLog("Info: "+client.getPlayer().get_name()+": OK pour Oublie sort id="+id);
			SocketManager.GAME_SEND_SPELL_UPGRADE_SUCCED(client, id, client.getPlayer().getSortStatBySortIfHas(id).getLevel());
			SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
			client.getPlayer().setisForgetingSpell(false);
		}
	}
}