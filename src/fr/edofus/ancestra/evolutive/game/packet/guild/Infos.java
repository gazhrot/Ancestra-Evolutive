package fr.edofus.ancestra.evolutive.game.packet.guild;



import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.House;
import fr.edofus.ancestra.evolutive.objects.Percepteur;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

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