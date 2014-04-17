package game.packet.dialog;

import objects.NPC_tmpl.NPC;
import objects.NPC_tmpl.NPC_question;
import objects.NPC_tmpl.NPC_reponse;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;

import common.SocketManager;
import common.World;

import game.GameClient;

@Packet("DR")
public class Answer implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
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
}