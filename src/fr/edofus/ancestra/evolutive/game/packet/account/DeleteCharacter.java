package fr.edofus.ancestra.evolutive.game.packet.account;


import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("AD")
public class DeleteCharacter implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		String[] split = packet.substring(2).split("\\|");
		int GUID = Integer.parseInt(split[0]);
		String reponse = split.length>1?split[1]:"";
		
		if(client.getAccount().get_persos().containsKey(GUID))
		{
			if(client.getAccount().get_persos().get(GUID).get_lvl() <20 ||(client.getAccount().get_persos().get(GUID).get_lvl() >=20 && reponse.equals(client.getAccount().get_reponse())))
			{
				client.getAccount().deletePerso(GUID);
				SocketManager.GAME_SEND_PERSO_LIST(client, client.getAccount().get_persos());
			}else {
				SocketManager.GAME_SEND_DELETE_PERSO_FAILED(client);
			}
		}else {
			SocketManager.GAME_SEND_DELETE_PERSO_FAILED(client);
		}
	}
}