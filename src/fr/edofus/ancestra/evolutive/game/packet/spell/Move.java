package fr.edofus.ancestra.evolutive.game.packet.spell;



import fr.edofus.ancestra.evolutive.common.CryptManager;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.Sort.SortStats;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

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