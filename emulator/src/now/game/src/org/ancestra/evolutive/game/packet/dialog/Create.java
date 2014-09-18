package org.ancestra.evolutive.game.packet.dialog;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.creature.npc.Npc;
import org.ancestra.evolutive.entity.creature.npc.NpcQuestion;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.game.actions.GameActionManager;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("DC")
public class Create implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try	{
			int id = Integer.parseInt(packet.substring(2).split((char) 0x0A + "")[0]);
			
			Npc npc = client.getPlayer().getMap().getNpcs().get(id);
			
			if(npc == null)
				return;
			
			SocketManager.GAME_SEND_DCK_PACKET(client, id);
			int questionId = npc.getTemplate().getInitQuestion();
			NpcQuestion question = World.data.getNpcQuestion(questionId);
			
			if(question == null) {
				client.send("DV");
				return;
			}
			
			SocketManager.GAME_SEND_QUESTION_PACKET(client, question.parseToDQPacket(client.getPlayer()));
			client.getPlayer().setIsTalkingWith(id);
            client.getPlayer().getGameActionManager().setStatus(GameActionManager.Status.DIALOG);
		} catch(NumberFormatException e) {}
	}
}