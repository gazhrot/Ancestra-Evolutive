package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("gb")
public class BoostSpell implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getGuild() == null)
			return;
		
		Guild guild = client.getPlayer().getGuild();
		
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_BOOST))
			return;
		
		int id = Integer.parseInt(packet.substring(2));
		
		if(guild.getSpells().containsKey(id)) {
			if(guild.getCapital() < 5)
				return;
			
			guild.setCapital(guild.getCapital() - 5);
			guild.boostSpell(id);
			World.database.getGuildData().update(guild);
			SocketManager.GAME_SEND_gIB_PACKET(client.getPlayer(), client.getPlayer().getGuild().parseCollector());
		}else {
			Log.addToLog("[ERROR]Sort "+id+" non trouve.");
		}
	}
}