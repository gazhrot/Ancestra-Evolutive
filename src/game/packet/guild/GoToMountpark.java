package game.packet.guild;

import objects.Carte.MountPark;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;

import common.SocketManager;
import core.World;

import game.GameClient;

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