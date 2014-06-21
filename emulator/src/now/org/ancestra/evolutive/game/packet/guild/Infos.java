package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.collector.Collector;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("gI")
public class Infos implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		switch(packet.charAt(2)) {
		case 'B'://Perco
			SocketManager.GAME_SEND_gIB_PACKET(client.getPlayer(), client.getPlayer().getGuild().parseCollector());
		break;
		case 'F'://Enclos
			SocketManager.GAME_SEND_gIF_PACKET(client.getPlayer(), World.data.parseMPtoGuild(client.getPlayer().getGuild().getId()));
		break;
		case 'G'://General
			SocketManager.GAME_SEND_gIG_PACKET(client.getPlayer(), client.getPlayer().getGuild());
		break;
		case 'H'://House
			SocketManager.GAME_SEND_gIH_PACKET(client.getPlayer(), House.parseHouseToGuild(client.getPlayer()));
		break;
		case 'M'://Members
			SocketManager.GAME_SEND_gIM_PACKET(client.getPlayer(), client.getPlayer().getGuild(),'+');
		break;
		case 'T'://Perco
			SocketManager.GAME_SEND_gITM_PACKET(client.getPlayer(), Collector.parsetoGuild(client.getPlayer().getGuild().getId()));
			Collector.parseAttaque(client.getPlayer(), client.getPlayer().getGuild().getId());
			Collector.parseDefense(client.getPlayer(), client.getPlayer().getGuild().getId());
		break;
	}
	}
}