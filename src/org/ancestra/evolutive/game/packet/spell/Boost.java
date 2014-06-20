package org.ancestra.evolutive.game.packet.spell;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("SB")
public class Boost implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try	{
			int id = Integer.parseInt(packet.substring(2));
			Log.addToLog("Info: "+client.getPlayer().getName()+": Tente BOOST sort id="+id);
			
			if(client.getPlayer().boostSpell(id)) {
				Log.addToLog("Info: "+client.getPlayer().getName()+": OK pour BOOST sort id="+id);
				SocketManager.GAME_SEND_SPELL_UPGRADE_SUCCED(client, id, client.getPlayer().getSortStatBySortIfHas(id).getLevel());
				SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
			}else {
				Log.addToLog("Info: "+client.getPlayer().getName()+": Echec BOOST sort id="+id);
				SocketManager.GAME_SEND_SPELL_UPGRADE_FAILED(client);
				return;
			}
		} catch(NumberFormatException e) {
			SocketManager.GAME_SEND_SPELL_UPGRADE_FAILED(client);
			return;
		}
	}
}