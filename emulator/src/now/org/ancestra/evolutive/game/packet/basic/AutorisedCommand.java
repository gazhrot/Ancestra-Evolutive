package org.ancestra.evolutive.game.packet.basic;


import org.ancestra.evolutive.common.Commands;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("BA")
public class AutorisedCommand implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getCommand() == null) 
			client.setCommand(new Commands(client.getPlayer()));
		client.getCommand().consoleCommand(packet);
	}
}