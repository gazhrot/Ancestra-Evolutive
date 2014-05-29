package org.ancestra.evolutive.game.packet.dialog;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.npc.Npc;
import org.ancestra.evolutive.entity.npc.NpcAnswer;
import org.ancestra.evolutive.entity.npc.NpcQuestion;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("DR")
public class Answer implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try {
			String[] infos = packet.substring(2).split("\\|");
			if(client.getPlayer().getIsTalkingWith() == 0)
				return;
			
			Npc npc = client.getPlayer().getCurMap().getNpcs().get(client.getPlayer().getIsTalkingWith());
			
			if( npc == null)
				return;
			
			int qID = Integer.parseInt(infos[0]);
			int rID = Integer.parseInt(infos[1]);
			NpcQuestion quest = World.data.getNpcQuestion(qID);
			NpcAnswer rep = World.data.getNpcAnswer(rID);
			
			if(quest == null || rep == null || !rep.isAnotherDialog()) {
				SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
				client.getPlayer().setIsTalkingWith(0);
			}
			
			rep.apply(client.getPlayer());
		} catch(Exception e)	{
			SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
		}
	}
}