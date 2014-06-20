package org.ancestra.evolutive.game.packet.dialog;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.npc.Npc;
import org.ancestra.evolutive.entity.npc.NpcQuestion;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("DC")
public class Create implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try	{
			int npcID = Integer.parseInt(packet.substring(2).split((char)0x0A+"")[0]);
			
			Npc npc = client.getPlayer().getCurMap().getNpcs().get(npcID);
			
			if( npc == null)
				return;
			
			SocketManager.GAME_SEND_DCK_PACKET(client,npcID);
			int qID = npc.getTemplate().getInitQuestion();
			NpcQuestion quest = World.data.getNpcQuestion(qID);
			
			if(quest == null) {
				SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
				return;
			}
			
			SocketManager.GAME_SEND_QUESTION_PACKET(client,quest.parseToDQPacket(client.getPlayer()));
			client.getPlayer().setIsTalkingWith(npcID);
		} catch(NumberFormatException e) {}
	}
}