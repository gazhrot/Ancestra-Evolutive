package game.packet;

import tool.command.CommandParser;
import client.Player;

import common.Commands;
import common.SocketManager;
import common.World;

import core.Log;
import core.Server;
import game.GameClient;
import game.packet.handler.Packet;

public class BasicPacket {
	
	@Packet("BA")
	public static void console(GameClient client, String packet) {
		if(client.getCommand() == null) 
			client.setCommand(new Commands(client.getPlayer()));
		client.getCommand().consoleCommand(packet);
	}
	
	@Packet("BD")
	public static void sendDate(GameClient client, String packet) {
		SocketManager.GAME_SEND_SERVER_DATE(client);
		SocketManager.GAME_SEND_SERVER_HOUR(client);
	}
	
	@Packet("BM")
	public static void chatMessage(GameClient client, String packet) {
		if(client.getPlayer().isMuted()) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1124;"+client.getPlayer().get_compte()._muteTimer.getInitialDelay());
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
				if(!client.getPlayer().get_canaux().contains(packet.charAt(2)+""))
					return;
				msg = packet.split("\\|",2)[1];
				//Commandes joueurs
				if(msg.charAt(0) == '.') {
					String line = msg.substring(1, msg.length()-1);
					CommandParser.parse(line.toLowerCase(), client.getPlayer());
					return;
				}
				if(client.getPlayer().get_fight() == null)
					SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(client.getPlayer().get_curCarte(), "", client.getPlayer().get_GUID(), client.getPlayer().get_name(), msg);
				else
					SocketManager.GAME_SEND_cMK_PACKET_TO_FIGHT(client.getPlayer().get_fight(), 7, "", client.getPlayer().get_GUID(), client.getPlayer().get_name(), msg);
			break;
			case '#'://Canal Equipe
				if(!client.getPlayer().get_canaux().contains(packet.charAt(2)+""))
					return;
				if(client.getPlayer().get_fight() != null) {
					msg = packet.split("\\|",2)[1];
					int team = client.getPlayer().get_fight().getTeamID(client.getPlayer().get_GUID());
					if(team == -1)
						return;
					SocketManager.GAME_SEND_cMK_PACKET_TO_FIGHT(client.getPlayer().get_fight(), team, "#", client.getPlayer().get_GUID(), client.getPlayer().get_name(), msg);
				}
			break;
			case '$'://Canal groupe
				if(!client.getPlayer().get_canaux().contains(packet.charAt(2)+""))
					return;
				if(client.getPlayer().getGroup() == null)
					break;
				msg = packet.split("\\|",2)[1];
				SocketManager.GAME_SEND_cMK_PACKET_TO_GROUP(client.getPlayer().getGroup(), "$", client.getPlayer().get_GUID(), client.getPlayer().get_name(), msg);
			break;
			
			case ':'://Canal commerce
				if(!client.getPlayer().get_canaux().contains(packet.charAt(2)+""))return;
				long l;
				if((l = System.currentTimeMillis() - client.timeLastTradeMsg) < Server.config.getFloodTime()) {
					l = (Server.config.getFloodTime()  - l)/1000;
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "0115;"+((int)Math.ceil(l)+1));
					return;
				}
				client.timeLastTradeMsg = System.currentTimeMillis();
				msg = packet.split("\\|",2)[1];
				SocketManager.GAME_SEND_cMK_PACKET_TO_ALL(":", client.getPlayer().get_GUID(), client.getPlayer().get_name(), msg);
			break;
			case '@'://Canal Admin
				if(client.getPlayer().get_compte().get_gmLvl() ==0)return;
				msg = packet.split("\\|",2)[1];
				SocketManager.GAME_SEND_cMK_PACKET_TO_ADMIN("@", client.getPlayer().get_GUID(), client.getPlayer().get_name(), msg);
			break;
			case '?'://Canal recrutement
				if(!client.getPlayer().get_canaux().contains(packet.charAt(2)+""))return;
				long j;
				if((j = System.currentTimeMillis() - client.timeLastRecrutmentMsg) < Server.config.getFloodTime()) {
					j = (Server.config.getFloodTime()  - j)/1000;
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "0115;"+((int)Math.ceil(j)+1));
					return;
				}
				client.timeLastRecrutmentMsg = System.currentTimeMillis();
				msg = packet.split("\\|",2)[1];
				SocketManager.GAME_SEND_cMK_PACKET_TO_ALL("?", client.getPlayer().get_GUID(), client.getPlayer().get_name(), msg);
			break;
			case '%'://Canal guilde
				if(!client.getPlayer().get_canaux().contains(packet.charAt(2)+""))
					return;
				if(client.getPlayer().get_guild() == null)
					return;
				msg = packet.split("\\|",2)[1];
				SocketManager.GAME_SEND_cMK_PACKET_TO_GUILD(client.getPlayer().get_guild(), "%", client.getPlayer().get_GUID(), client.getPlayer().get_name(), msg);
			break;
			case 0xC2://Canal 
			break;
			case '!'://Alignement
				if(!client.getPlayer().get_canaux().contains(packet.charAt(2)+""))
					return;
				if(client.getPlayer().get_align() == 0) 
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
				SocketManager.GAME_SEND_cMK_PACKET_TO_ALIGN("!", client.getPlayer().get_GUID(), client.getPlayer().get_name(), msg, client.getPlayer());
			break;
			default:
				String nom = packet.substring(2).split("\\|")[0];
				msg = packet.split("\\|",2)[1];
				if(nom.length() <= 1) {
					Log.addToLog("ChatHandler: Chanel non gere : "+nom);
				}else {
					Player target = World.data.getPersoByName(nom);
					if(target == null) {
						SocketManager.GAME_SEND_CHAT_ERROR_PACKET(client, nom);
						return;
					}
					if(target.get_compte() == null) {
						SocketManager.GAME_SEND_CHAT_ERROR_PACKET(client, nom);
						return;
					}
					if(target.get_compte().getGameClient() == null) {
						SocketManager.GAME_SEND_CHAT_ERROR_PACKET(client, nom);
						return;
					}
					if(target.get_compte().isEnemyWith(client.getPlayer().get_compte().get_GUID()) == true || !target.isDispo(client.getPlayer())) {
						SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "114;"+target.get_name());
						return;
					}
					SocketManager.GAME_SEND_cMK_PACKET(target, "F", client.getPlayer().get_GUID(), client.getPlayer().get_name(), msg);
					SocketManager.GAME_SEND_cMK_PACKET(client.getPlayer(), "T", target.get_GUID(), target.get_name(), msg);
				}
			break;
		}
	}
	
	@Packet("BW")
	public static void infosMessage(GameClient client, String packet) {
		packet = packet.substring(2);
		Player T = World.data.getPersoByName(packet);
		if(T == null) 
			return;
		SocketManager.GAME_SEND_BWK(client.getPlayer(), T.get_compte().get_pseudo()+"|1|"+T.get_name()+"|-1");
	}

	@Packet("BS")
	public static void emoticone(GameClient client, String packet) {
		client.getPlayer().emoticone(packet.substring(2));
	}
	
	@Packet("BY")
	public static void state(GameClient client, String packet) {
		switch(packet.charAt(2))
		{
			case 'A': //Absent
				if(client.getPlayer()._isAbsent) {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "038");
					client.getPlayer()._isAbsent = false;
				}else {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "037");
					client.getPlayer()._isAbsent = true;
				}
			break;
			case 'I': //Invisible
				if(client.getPlayer()._isInvisible)	{
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "051");
					client.getPlayer()._isInvisible = false;
				}else {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "050");
					client.getPlayer()._isInvisible = true;
				}
			break;
		}
	}
}