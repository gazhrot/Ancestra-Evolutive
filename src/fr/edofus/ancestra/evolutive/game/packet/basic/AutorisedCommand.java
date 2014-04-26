package fr.edofus.ancestra.evolutive.game.packet.basic;


import fr.edofus.ancestra.evolutive.common.Commands;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("BA")
public class AutorisedCommand implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getCommand() == null) 
			client.setCommand(new Commands(client.getPlayer()));
		client.getCommand().consoleCommand(packet);
	}
}