package org.ancestra.evolutive.game.packet.dialog;



import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.objects.NPC_tmpl.NPC;
import org.ancestra.evolutive.objects.NPC_tmpl.NPC_question;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


@Packet("DC")
public class Create implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try	{
			int npcID = Integer.parseInt(packet.substring(2).split((char)0x0A+"")[0]);
			
			NPC npc = client.getPlayer().get_curCarte().getNPC(npcID);
			
			if( npc == null)
				return;
			
			SocketManager.GAME_SEND_DCK_PACKET(client,npcID);
			int qID = npc.get_template().get_initQuestionID();
			NPC_question quest = World.data.getNPCQuestion(qID);
			
			if(quest == null) {
				SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
				return;
			}
			
			SocketManager.GAME_SEND_QUESTION_PACKET(client,quest.parseToDQPacket(client.getPlayer()));
			client.getPlayer().set_isTalkingWith(npcID);
		} catch(NumberFormatException e) {}
	}
}