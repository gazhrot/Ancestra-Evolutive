package org.ancestra.evolutive.game.packet.basic;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.command.CommandParser;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("BM")
public class SendMessage implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().isMuted()) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1124;"+client.getPlayer().getAccount().getMuteTimer().getInitialDelay());
			return;
		}
		
		String msg = "";
		packet = packet.replace("<", "");
		packet = packet.replace(">", "");
		if(packet.length() == 3)
			return;
		
		switch(packet.charAt(2))
		{
			case '*'://Canal noir
				if(!client.getPlayer().getCanaux().contains(packet.charAt(2)+""))
					return;
				msg = packet.split("\\|",2)[1];
				//Commandes joueurs
				if(msg.charAt(0) == '.') {
					String line = msg.substring(1, msg.length()-1);
					CommandParser.parse(line.toLowerCase(), client.getPlayer());
					return;
				}
				if(client.getPlayer().getFight() == null)
					SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(client.getPlayer().getMap(), "", client.getPlayer().getId(), client.getPlayer().getName(), msg);
				else
					SocketManager.GAME_SEND_cMK_PACKET_TO_FIGHT(client.getPlayer().getFight(), 7, "", client.getPlayer().getId(), client.getPlayer().getName(), msg);
			break;
			case '#'://Canal Equipe
				if(!client.getPlayer().getCanaux().contains(packet.charAt(2)+""))
					return;
				if(client.getPlayer().getFight() != null) {
					msg = packet.split("\\|",2)[1];
					int team = client.getPlayer().getFight().getTeamID(client.getPlayer().getId());
					if(team == -1)
						return;
					SocketManager.GAME_SEND_cMK_PACKET_TO_FIGHT(client.getPlayer().getFight(), team, "#", client.getPlayer().getId(), client.getPlayer().getName(), msg);
				}
			break;
			case '$'://Canal groupe
				if(!client.getPlayer().getCanaux().contains(packet.charAt(2)+""))
					return;
				if(client.getPlayer().getGroup() == null)
					break;
				msg = packet.split("\\|",2)[1];
				SocketManager.GAME_SEND_cMK_PACKET_TO_GROUP(client.getPlayer().getGroup(), "$", client.getPlayer().getId(), client.getPlayer().getName(), msg);
			break;
			
			case ':'://Canal commerce
				if(!client.getPlayer().getCanaux().contains(packet.charAt(2)+""))return;
				long l;
				if((l = System.currentTimeMillis() - client.timeLastTradeMsg) < Server.config.getFloodTime()) {
					l = (Server.config.getFloodTime()  - l)/1000;
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "0115;"+((int)Math.ceil(l)+1));
					return;
				}
				client.timeLastTradeMsg = System.currentTimeMillis();
				msg = packet.split("\\|",2)[1];
				SocketManager.GAME_SEND_cMK_PACKET_TO_ALL(":", client.getPlayer().getId(), client.getPlayer().getName(), msg);
			break;
			case '@'://Canal Admin
				if(client.getPlayer().getAccount().getGmLvl() ==0)return;
				msg = packet.split("\\|",2)[1];
				SocketManager.GAME_SEND_cMK_PACKET_TO_ADMIN("@", client.getPlayer().getId(), client.getPlayer().getName(), msg);
			break;
			case '?'://Canal recrutement
				if(!client.getPlayer().getCanaux().contains(packet.charAt(2)+""))return;
				long j;
				if((j = System.currentTimeMillis() - client.timeLastRecrutmentMsg) < Server.config.getFloodTime()) {
					j = (Server.config.getFloodTime()  - j)/1000;
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "0115;"+((int)Math.ceil(j)+1));
					return;
				}
				client.timeLastRecrutmentMsg = System.currentTimeMillis();
				msg = packet.split("\\|",2)[1];
				SocketManager.GAME_SEND_cMK_PACKET_TO_ALL("?", client.getPlayer().getId(), client.getPlayer().getName(), msg);
			break;
			case '%'://Canal guilde
				if(!client.getPlayer().getCanaux().contains(packet.charAt(2)+""))
					return;
				if(client.getPlayer().getGuild() == null)
					return;
				msg = packet.split("\\|",2)[1];
				SocketManager.GAME_SEND_cMK_PACKET_TO_GUILD(client.getPlayer().getGuild(), "%", client.getPlayer().getId(), client.getPlayer().getName(), msg);
			break;
			case 0xC2://Canal 
			break;
			case '!'://Alignement
				if(!client.getPlayer().getCanaux().contains(packet.charAt(2)+""))
					return;
				if(client.getPlayer().getAlign() == 0) 
					return;
				if(client.getPlayer().getDeshonor() >= 1) {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "183");
					return;
				}
				long k;
				if((k = System.currentTimeMillis() - client.timeLastAlignMsg) < Server.config.getFloodTime()) {
					k = (Server.config.getFloodTime()  - k)/1000;
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "0115;"+((int)Math.ceil(k)+1));
					return;
				}
				client.timeLastAlignMsg = System.currentTimeMillis();
				msg = packet.split("\\|",2)[1];
				SocketManager.GAME_SEND_cMK_PACKET_TO_ALIGN("!", client.getPlayer().getId(), client.getPlayer().getName(), msg, client.getPlayer());
			break;
			default:
				String nom = packet.substring(2).split("\\|")[0];
				msg = packet.split("\\|",2)[1];
				if(nom.length() <= 1) {
					Log.addToLog("ChatHandler: Chanel non gere : "+nom);
				}else {
					Player target = World.data.getPlayerByName(nom);
					if(target == null) {
						SocketManager.GAME_SEND_CHAT_ERROR_PACKET(client, nom);
						return;
					}
					if(target.getAccount() == null) {
						SocketManager.GAME_SEND_CHAT_ERROR_PACKET(client, nom);
						return;
					}
					if(target.getAccount().getGameClient() == null) {
						SocketManager.GAME_SEND_CHAT_ERROR_PACKET(client, nom);
						return;
					}
					if(target.getAccount().isEnemyWith(client.getPlayer().getAccount().getUUID()) == true || !target.isDispo(client.getPlayer())) {
						SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "114;"+target.getName());
						return;
					}
					SocketManager.GAME_SEND_cMK_PACKET(target, "F", client.getPlayer().getId(), client.getPlayer().getName(), msg);
					SocketManager.GAME_SEND_cMK_PACKET(client.getPlayer(), "T", target.getId(), target.getName(), msg);
				}
			break;
		}
	}
}