package game.packet.basic;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.Commands;

import game.GameClient;

@Packet("BA")
public class AutorisedCommand implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getCommand() == null) 
			client.setCommand(new Commands(client.getPlayer()));
		client.getCommand().consoleCommand(packet);
	}
}