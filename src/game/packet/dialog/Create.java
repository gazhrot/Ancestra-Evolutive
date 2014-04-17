package game.packet.dialog;

import objects.NPC_tmpl.NPC;
import objects.NPC_tmpl.NPC_question;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;

import common.SocketManager;
import common.World;

import game.GameClient;

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