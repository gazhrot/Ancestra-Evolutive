package fr.edofus.ancestra.evolutive.game.packet.house.kode;

import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.House;
import fr.edofus.ancestra.evolutive.objects.Trunk;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("KK")
public class Code implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		switch(packet.charAt(2))
		{
			case '0'://Envoi du code
				packet = packet.substring(4);
				if(client.getPlayer().getInTrunk() != null)
					Trunk.OpenTrunk(client.getPlayer(), packet, false);
				else
					House.OpenHouse(client.getPlayer(), packet, false);
			break;
			case '1'://Changement du code
				packet = packet.substring(4);
				if(client.getPlayer().getInTrunk() != null)
					Trunk.LockTrunk(client.getPlayer(), packet);
				else
				    House.LockHouse(client.getPlayer(), packet);
			break;
		}
	}
}