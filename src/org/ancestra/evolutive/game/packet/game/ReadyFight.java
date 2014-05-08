package org.ancestra.evolutive.game.packet.game;


import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("GR")
public class ReadyFight implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_fight() == null)
			return;
		if(client.getPlayer().get_fight().get_state() != Constants.FIGHT_STATE_PLACE)
			return;
		
		client.getPlayer().set_ready(packet.substring(2).equalsIgnoreCase("1"));
		client.getPlayer().get_fight().verifIfAllReady();
		SocketManager.GAME_SEND_FIGHT_PLAYER_READY_TO_FIGHT(client.getPlayer().get_fight(),3,client.getPlayer().get_GUID(),packet.substring(2).equalsIgnoreCase("1"));
	}
}