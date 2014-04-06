package game.packet.basic;

import common.Commands;

import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("BA")
public class AutorisedCommand implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getCommand() == null) 
			client.setCommand(new Commands(client.getPlayer()));
		client.getCommand().consoleCommand(packet);
	}
}