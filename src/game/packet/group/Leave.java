package game.packet.group;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;
import client.Player.Group;

import common.SocketManager;
import core.World;

import game.GameClient;

@Packet("PV")
public class Leave implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		
		Group group = client.getPlayer().getGroup();
		
		if(group == null)
			return;
		
		if(packet.length() == 2) {//Si aucun guid est sp�cifi�, alors c'est que le joueur quitte
			group.leave(client.getPlayer());
			SocketManager.GAME_SEND_PV_PACKET(client, "");
			SocketManager.GAME_SEND_IH_PACKET(client.getPlayer(), "");
		}else 
		if(group.isChief(client.getPlayer().get_GUID())) {//Sinon, c'est qu'il kick un joueur du groupe
			int guid = -1;
			
			try {
				guid = Integer.parseInt(packet.substring(2));
			} catch(NumberFormatException e) {return;}
			
			if(guid == -1)
				return;
			
			Player player = World.data.getPersonnage(guid);
			
			group.leave(player);
			SocketManager.GAME_SEND_PV_PACKET(player.get_compte().getGameClient(), String.valueOf(client.getPlayer().get_GUID()));
			SocketManager.GAME_SEND_IH_PACKET(player, "");
		}
	}
}