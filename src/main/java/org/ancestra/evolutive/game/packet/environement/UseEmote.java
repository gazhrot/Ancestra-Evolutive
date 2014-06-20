package org.ancestra.evolutive.game.packet.environement;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("eU")
public class UseEmote implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int emote = -1;
		
		try	{
			emote = Integer.parseInt(packet.substring(2));
		} catch(Exception e) {}
		
		if(emote == -1 || client.getPlayer() == null)
			return;
		if(client.getPlayer().getFight() != null)
			return;

        boolean activated = client.getPlayer().setEmoteActive(emote);
		
		SocketManager.GAME_SEND_eUK_PACKET_TO_MAP(client.getPlayer().getMap(), client.getPlayer().getId(), activated?emote:0);
	}
}