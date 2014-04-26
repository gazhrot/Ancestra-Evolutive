package fr.edofus.ancestra.evolutive.game.packet.environement;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.Console;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("eU")
public class UseEmote implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		int emote = -1;
		
		try	{
			emote = Integer.parseInt(packet.substring(2));
		} catch(Exception e) {}
		
		if(emote == -1 || client.getPlayer() == null)
			return;
		if(client.getPlayer().get_fight() != null)
			return;
		
		switch(emote) {//effets speciaux des emotes
			case 19://s'allonger 
			case 1:// s'asseoir
				client.getPlayer().setSitted(!client.getPlayer().isSitted());
			break;
		}
		
		if(client.getPlayer().emoteActive() == emote)
			client.getPlayer().setEmoteActive(0);
		else 
			client.getPlayer().setEmoteActive(emote);
		
		Console.instance.println("Set Emote "+client.getPlayer().emoteActive());
		Console.instance.println("Is sitted "+client.getPlayer().isSitted());
		
		SocketManager.GAME_SEND_eUK_PACKET_TO_MAP(client.getPlayer().get_curCarte(), client.getPlayer().get_GUID(), client.getPlayer().emoteActive());
	}
}