package fr.edofus.ancestra.evolutive.game.packet.guild;



import fr.edofus.ancestra.evolutive.common.Constants;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.Log;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.Guild;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

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