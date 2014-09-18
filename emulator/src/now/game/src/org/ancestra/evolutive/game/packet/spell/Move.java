package org.ancestra.evolutive.game.packet.spell;

import org.ancestra.evolutive.common.CryptManager;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("SM")
public class Move implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try	{
			int id = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			int pos = Integer.parseInt(packet.substring(2).split("\\|")[1]);
			SpellStats spell = client.getPlayer().getSortStatBySortIfHas(id);
			
			if(spell != null)
				client.getPlayer().setSpellPlace(id, CryptManager.getHashedValueByInt(pos));
				
			client.send("BN");
		} catch(Exception e) {}
	}
}