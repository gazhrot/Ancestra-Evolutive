package game.packet;

import game.GameClient;
import common.Constants;
import common.SocketManager;

public class PacketHandler {
	
	public static void parsePacket(GameClient client, String packet) { 
		if(!verify(client, packet))
			return;
		
		switch(packet.charAt(0))
		{
			case 'A':
				AccountPacket.parseAccountPacket(client, packet);
				break;
			case 'B':
				BasicPacket.parseBasicPacket(client, packet);
				break;
			case 'c':
				ChanelPacket.parseChanelPacket(client, packet);
				break;
			case 'D':
				DialogPacket.parseDialogPacket(client, packet);
				break;
			case 'E':
				ExchangePacket.parseExchangePacket(client, packet);
				break;
			case 'e':
				EnvironementPacket.parseEnvironementPacket(client, packet);
				break;
			case 'F':
				FriendPacket.parseFriendPacket(client, packet);
				break;
			case 'f':
				FightPacket.parseFightPacket(client, packet);
				break;
			case 'G':
				GamePacket.parseGamePacket(client, packet);
				break;
			case 'g':
				GuildPacket.parseGuildPacket(client, packet);
				break;
			case 'h':
				HousePacket.parseHousePacket(client, packet);
				break;
			case 'i':
				EnemyPacket.parseEnemyPacket(client, packet);
				break;
			case 'K':
				HouseKodePacket.parseHouseKodePacket(client, packet);
				break;
			case 'O':
				ObjectPacket.parseObjectPacket(client, packet);
				break;
			case 'P':
				GroupPacket.parseGroupPacket(client, packet);
				break;
			case 'R':
				MountPacket.parseMountPacket(client, packet);
				break;
			case 'S':
				SpellPacket.parseSpellPacket(client, packet);
				break;
			case 'W':
				WaypointPacket.parseWaypointPacket(client, packet);
				break;
		}
	}

	private static boolean verify(GameClient client, String packet) {
		if (!client.getFilter().authorizes(Constants.getIp(client.getSession().getRemoteAddress().toString())))
			client.kick();
		
		if(client.getPlayer() != null)
			client.getPlayer().refreshLastPacketTime();
		
		if(packet.length() > 3 && packet.substring(0,4).equalsIgnoreCase("ping"))	{
			SocketManager.GAME_SEND_PONG(client);
			return false;
		}
		if(packet.length() > 4 && packet.substring(0,5).equalsIgnoreCase("qping")) {
			SocketManager.GAME_SEND_QPONG(client);
			return false;
		}
		return true;
	}
}