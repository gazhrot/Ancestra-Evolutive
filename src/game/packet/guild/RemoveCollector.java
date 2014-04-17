package game.packet.guild;

import objects.Percepteur;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;

import common.Constants;
import common.SocketManager;
import common.World;

import game.GameClient;

@Packet("gF")
public class RemoveCollector implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		packet = packet.substring(2);
		if(client.getPlayer().get_guild() == null || client.getPlayer().get_fight() != null || client.getPlayer().is_away())
			return;
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_POSPERCO))return;//On peut le retirer si on a le droit de le poser
		byte IDPerco = Byte.parseByte(packet);
		Percepteur perco = World.data.getPerco(IDPerco);
		if(perco == null || perco.get_inFight() > 0) return;
		SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(client.getPlayer().get_curCarte(), IDPerco);
		World.database.getCollectorData().delete(perco);
		perco.DelPerco(perco.getGuid());
		for(Player z : client.getPlayer().get_guild().getMembers())
		{
			if(z.isOnline())
			{
				SocketManager.GAME_SEND_gITM_PACKET(z, Percepteur.parsetoGuild(z.get_guild().get_id()));
				String str = "";
				str += "R"+perco.get_N1()+","+perco.get_N2()+"|";
				str += perco.get_mapID()+"|";
				str += World.data.getCarte((short)perco.get_mapID()).getX()+"|"+World.data.getCarte((short)perco.get_mapID()).getY()+"|"+client.getPlayer().get_name();
				SocketManager.GAME_SEND_gT_PACKET(z, str);
			}
		}
	}
}