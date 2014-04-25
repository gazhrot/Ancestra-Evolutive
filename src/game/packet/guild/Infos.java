package game.packet.guild;

import objects.House;
import objects.Percepteur;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;

import common.SocketManager;
import core.World;

import game.GameClient;

@Packet("gI")
public class Infos implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		switch(packet.charAt(2)) {
		case 'B'://Perco
			SocketManager.GAME_SEND_gIB_PACKET(client.getPlayer(), client.getPlayer().get_guild().parsePercotoGuild());
		break;
		case 'F'://Enclos
			SocketManager.GAME_SEND_gIF_PACKET(client.getPlayer(), World.data.parseMPtoGuild(client.getPlayer().get_guild().get_id()));
		break;
		case 'G'://General
			SocketManager.GAME_SEND_gIG_PACKET(client.getPlayer(), client.getPlayer().get_guild());
		break;
		case 'H'://House
			SocketManager.GAME_SEND_gIH_PACKET(client.getPlayer(), House.parseHouseToGuild(client.getPlayer()));
		break;
		case 'M'://Members
			SocketManager.GAME_SEND_gIM_PACKET(client.getPlayer(), client.getPlayer().get_guild(),'+');
		break;
		case 'T'://Perco
			SocketManager.GAME_SEND_gITM_PACKET(client.getPlayer(), Percepteur.parsetoGuild(client.getPlayer().get_guild().get_id()));
			Percepteur.parseAttaque(client.getPlayer(), client.getPlayer().get_guild().get_id());
			Percepteur.parseDefense(client.getPlayer(), client.getPlayer().get_guild().get_id());
		break;
	}
	}
}