package org.ancestra.evolutive.game.packet.guild;



import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.objects.House;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("gh")
public class GoToHouse implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		if(client.getPlayer().get_guild() == null)
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		
		if(client.getPlayer().get_fight() != null || client.getPlayer().is_away())return;
		int HouseID = Integer.parseInt(packet);
		House h = World.data.getHouses().get(HouseID);
		if(h == null) return;
		if(client.getPlayer().get_guild().get_id() != h.get_guild_id()) 
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1135");
			return;
		}
		if(!h.canDo(Constants.H_GTELE))
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1136");
			return;
		}
		if (client.getPlayer().hasItemTemplate(8883, 1))
		{
			client.getPlayer().removeByTemplateID(8883,1);
			client.getPlayer().teleport((short)h.get_mapid(), h.get_caseid());
		}else
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1137");
			return;
		}
	}
}