package org.ancestra.evolutive.game.packet.dialog;

import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.creature.npc.Npc;
import org.ancestra.evolutive.entity.creature.npc.NpcAnswer;
import org.ancestra.evolutive.entity.creature.npc.NpcQuestion;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.game.actions.GameActionManager;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("DR")
public class Answer implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try {
			String[] infos = packet.substring(2).split("\\|");
			
			if(client.getPlayer().getGameActionManager().getStatus() != GameActionManager.Status.DIALOG)
				return;
			
			Npc npc = client.getPlayer().getMap().getNpcs().get(client.getPlayer().getIsTalkingWith());
			
			if(npc == null)
				return;
			
			int questionId = Integer.parseInt(infos[0]);
			int answerId = Integer.parseInt(infos[1]);
			NpcQuestion question = World.data.getNpcQuestion(questionId);
			NpcAnswer answer = World.data.getNpcAnswer(answerId);
			
			if(question == null || answer == null || !answer.isAnotherDialog()) {
				client.send("DV");
				client.getPlayer().setIsTalkingWith(0);
                client.getPlayer().getGameActionManager().setStatus(GameActionManager.Status.WAITING);
                return;
			}
			
			answer.apply(client.getPlayer());
		} catch(Exception e) {
			client.send("DV");
            client.getPlayer().setIsTalkingWith(0);
            client.getPlayer().getGameActionManager().setStatus(GameActionManager.Status.WAITING);
        }
	}
}