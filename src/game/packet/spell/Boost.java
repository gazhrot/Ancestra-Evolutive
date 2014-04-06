package game.packet.spell;

import common.SocketManager;

import core.Log;
import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("SB")
public class Boost implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try	{
			int id = Integer.parseInt(packet.substring(2));
			Log.addToLog("Info: "+client.getPlayer().get_name()+": Tente BOOST sort id="+id);
			
			if(client.getPlayer().boostSpell(id)) {
				Log.addToLog("Info: "+client.getPlayer().get_name()+": OK pour BOOST sort id="+id);
				SocketManager.GAME_SEND_SPELL_UPGRADE_SUCCED(client, id, client.getPlayer().getSortStatBySortIfHas(id).getLevel());
				SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
			}else {
				Log.addToLog("Info: "+client.getPlayer().get_name()+": Echec BOOST sort id="+id);
				SocketManager.GAME_SEND_SPELL_UPGRADE_FAILED(client);
				return;
			}
		} catch(NumberFormatException e) {
			SocketManager.GAME_SEND_SPELL_UPGRADE_FAILED(client);
			return;
		}
	}
}