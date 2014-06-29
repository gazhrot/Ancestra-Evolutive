package org.ancestra.evolutive.game.packet.basic;

import org.ancestra.evolutive.client.Admin;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.command.CommandParser;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("BA")
public class AutorisedCommand implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		/*if(client.getCommand() == null) 
			client.setCommand(new Admin(client.getPlayer()));
		client.getCommand().consoleCommand(packet);*/
		CommandParser.parse(packet.substring(2), new Admin(client.getPlayer()));
	}
}