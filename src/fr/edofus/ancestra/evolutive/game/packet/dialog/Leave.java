package fr.edofus.ancestra.evolutive.game.packet.dialog;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("DV")
public class Leave implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
		if(client.getPlayer().get_isTalkingWith() != 0)
			client.getPlayer().set_isTalkingWith(0);
	}
}