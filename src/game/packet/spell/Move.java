package game.packet.spell;

import objects.Sort.SortStats;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;

import common.CryptManager;
import common.SocketManager;

import game.GameClient;

@Packet("SM")
public class Move implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
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