package game.packet.exchange;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;

import core.Server;
import core.World;
import game.GameClient;

@Packet("Eq")
public class AskOfflineExchange implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_isTradingWith() > 0 || client.getPlayer().get_fight() != null || client.getPlayer().is_away())
			return;
        if(client.getPlayer().parseStoreItemsList().isEmpty()) {
        	SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "123");
        	return;
        }
        if(World.data.isMarchandMap(client.getPlayer().get_curCarte().get_id())) {
        	SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "113");
        	return;
        }
        if (client.getPlayer().get_curCarte().get_id() == 33 || client.getPlayer().get_curCarte().get_id() == 38 || client.getPlayer().get_curCarte().get_id() == 4601 || client.getPlayer().get_curCarte().get_id() == 8036 || client.getPlayer().get_curCarte().get_id() == 10301) {
			if (client.getPlayer().get_curCarte().getStoreCount() >= 25) {
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;25");
				return;
			}
        }else if(client.getPlayer().get_curCarte().getStoreCount() >= 6) {
        	SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;6");
			return;
        }
        //Calcul et envoie du packet pour la taxe
        long Apayer = client.getPlayer().storeAllBuy() / 1000;
        if(Apayer < 0) {
	       	SocketManager.GAME_SEND_MESSAGE(client.getPlayer(), "Erreur de mode marchand, la somme est négatif.", Server.config.getMotdColor());
	       	return;
	    }
        SocketManager.GAME_SEND_Eq_PACKET(client.getPlayer(), Apayer);
	}
}