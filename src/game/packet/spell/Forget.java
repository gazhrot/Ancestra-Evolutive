package game.packet.spell;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import core.Log;
import core.Server;
import game.GameClient;

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