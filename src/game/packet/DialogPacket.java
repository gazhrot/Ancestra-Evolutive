package game.packet;

import objects.NPC_tmpl.NPC;
import objects.NPC_tmpl.NPC_question;
import objects.NPC_tmpl.NPC_reponse;

import common.SocketManager;
import common.World;

import game.GameClient;
import game.packet.handler.Packet;

public class DialogPacket {
	
	@Packet("DC")
	public static void start(GameClient client, String packet) {
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
	
	@Packet("DR")
	public static void answer(GameClient client, String packet) {
		try {
			String[] infos = packet.substring(2).split("\\|");
			if(client.getPlayer().get_isTalkingWith() == 0)
				return;
			
			NPC npc = client.getPlayer().get_curCarte().getNPC(client.getPlayer().get_isTalkingWith());
			
			if( npc == null)
				return;
			
			int qID = Integer.parseInt(infos[0]);
			int rID = Integer.parseInt(infos[1]);
			NPC_question quest = World.data.getNPCQuestion(qID);
			NPC_reponse rep = World.data.getNPCreponse(rID);
			
			if(quest == null || rep == null || !rep.isAnotherDialog()) {
				SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
				client.getPlayer().set_isTalkingWith(0);
			}
			
			rep.apply(client.getPlayer());
		} catch(Exception e)	{
			SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
		}
	}

	@Packet("DV")
	public static void end(GameClient client, String packet) {
		SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
		if(client.getPlayer().get_isTalkingWith() != 0)
			client.getPlayer().set_isTalkingWith(0);
	}
}