package game.packet.house.kode;

import objects.House;
import objects.Trunk;
import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

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