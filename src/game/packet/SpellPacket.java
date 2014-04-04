package game.packet;

import objects.Sort.SortStats;

import common.CryptManager;
import common.SocketManager;

import core.Log;
import core.Server;
import game.GameClient;
import game.packet.handler.Packet;

public class SpellPacket {
	
	@Packet("SB")
	public static void boost(GameClient client, String packet) {
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
	
	@Packet("SF")
	public static void forget(GameClient client, String packet) {
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
	
	@Packet("SM")
	public static void add(GameClient client, String packet) {
		try	{
			int id = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			int pos = Integer.parseInt(packet.substring(2).split("\\|")[1]);
			SortStats spell = client.getPlayer().getSortStatBySortIfHas(id);
			
			if(spell != null)
				client.getPlayer().set_SpellPlace(id, CryptManager.getHashedValueByInt(pos));
				
			SocketManager.GAME_SEND_BN(client);
		} catch(Exception e) {}
	}
}