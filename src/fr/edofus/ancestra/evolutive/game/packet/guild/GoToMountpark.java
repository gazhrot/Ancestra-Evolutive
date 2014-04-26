package fr.edofus.ancestra.evolutive.game.packet.guild;



import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.Carte.MountPark;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("gf")
public class GoToMountpark implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		if(client.getPlayer().get_guild() == null)
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		
		if(client.getPlayer().get_fight() != null || client.getPlayer().is_away())return;
		short MapID = Short.parseShort(packet);
		MountPark MP = World.data.getCarte(MapID).getMountPark();
		if(MP.get_guild().get_id() != client.getPlayer().get_guild().get_id())
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		int CellID = World.data.getEncloCellIdByMapId(MapID);
		if (client.getPlayer().hasItemTemplate(9035, 1))
		{
			client.getPlayer().removeByTemplateID(9035,1);
			client.getPlayer().teleport(MapID, CellID);
		}else
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1159");
			return;
		}
	}
}