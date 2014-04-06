package game.packet.guild;

import objects.Guild;

import common.Constants;
import common.SocketManager;
import common.World;

import core.Log;
import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("gb")
public class BoostSpell implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_guild() == null)
			return;
		
		Guild guild = client.getPlayer().get_guild();
		
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_BOOST))
			return;
		
		int id = Integer.parseInt(packet.substring(2));
		
		if(guild.getSpells().containsKey(id)) {
			if(guild.get_Capital() < 5)
				return;
			
			guild.set_Capital(guild.get_Capital() - 5);
			guild.boostSpell(id);
			World.database.getGuildData().update(guild);
			SocketManager.GAME_SEND_gIB_PACKET(client.getPlayer(), client.getPlayer().get_guild().parsePercotoGuild());
		}else {
			Log.addToLog("[ERROR]Sort "+id+" non trouve.");
		}
	}
}