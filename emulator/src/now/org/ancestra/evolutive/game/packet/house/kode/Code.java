package org.ancestra.evolutive.game.packet.house.kode;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.house.Trunk;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("KK")
public class Code implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		switch(packet.charAt(2))
		{
			case '0'://Envoi du code
				packet = packet.substring(4);
				if(client.getPlayer().getCurTrunk() != null)
					Trunk.open(client.getPlayer(), packet, false);
				else
					House.open(client.getPlayer(), packet, false);
			break;
			case '1'://Changement du code
				packet = packet.substring(4);
				if(client.getPlayer().getCurTrunk() != null)
					Trunk.lock(client.getPlayer(), packet);
				else
				    House.lock(client.getPlayer(), packet);
			break;
		}
	}
}