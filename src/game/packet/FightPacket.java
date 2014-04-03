package game.packet;

import common.SocketManager;

import game.GameClient;

public class FightPacket {

	public static void parseFightPacket(GameClient client, String packet) {
		try	{
			switch(packet.charAt(1))
			{
				case 'D'://D�tails d'un combat (liste des combats)
					int key = -1;
					try {
						key = Integer.parseInt(packet.substring(2).replace(((int)0x0)+"", ""));
					} catch(Exception e) {}
					
					if(key == -1)
						return;
					
					SocketManager.GAME_SEND_FIGHT_DETAILS(client, client.getPlayer().get_curCarte().get_fights().get(key));
				break;
				case 'H'://Aide
					if(client.getPlayer().get_fight() == null)
						return;
					client.getPlayer().get_fight().toggleHelp(client.getPlayer().get_GUID());
				break;
				case 'L'://Lister les combats
					SocketManager.GAME_SEND_FIGHT_LIST_PACKET(client, client.getPlayer().get_curCarte());
				break;
				case 'N'://Bloquer le combat
					if(client.getPlayer().get_fight() == null)
						return;
					client.getPlayer().get_fight().toggleLockTeam(client.getPlayer().get_GUID());
				break;
				case 'P'://Seulement le groupe
					if(client.getPlayer().get_fight() == null || client.getPlayer().getGroup() == null)
						return;
					client.getPlayer().get_fight().toggleOnlyGroup(client.getPlayer().get_GUID());
				break;
				case 'S'://Bloquer les specs
					if(client.getPlayer().get_fight() == null)
						return;
					client.getPlayer().get_fight().toggleLockSpec(client.getPlayer().get_GUID());
				break;
			}
		} catch(Exception e) {e.printStackTrace();}
	}
}