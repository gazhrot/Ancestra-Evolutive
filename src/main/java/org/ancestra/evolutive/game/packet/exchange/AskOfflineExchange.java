package org.ancestra.evolutive.game.packet.exchange;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Eq")
public class AskOfflineExchange implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getIsTradingWith() > 0 || client.getPlayer().getFight() != null || client.getPlayer().isAway())
			return;
        if(client.getPlayer().parseStoreItemsList().isEmpty()) {
        	SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "123");
        	return;
        }
        if(World.data.isMarchandMap(client.getPlayer().getMap().getId())) {
        	SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "113");
        	return;
        }
        if (client.getPlayer().getMap().getId() == 33 || client.getPlayer().getMap().getId() == 38 || client.getPlayer().getMap().getId() == 4601 || client.getPlayer().getMap().getId() == 8036 || client.getPlayer().getMap().getId() == 10301) {
			if (client.getPlayer().getMap().getStoreCount() >= 25) {
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;25");
				return;
			}
        }else if(client.getPlayer().getMap().getStoreCount() >= 6) {
        	SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;6");
			return;
        }
        //Calcul et envoie du packet pour la taxe
        long Apayer = client.getPlayer().storeAllBuy() / 1000;
        if(Apayer < 0) {
	       	SocketManager.GAME_SEND_MESSAGE(client.getPlayer(), "Erreur de mode marchand, la somme est n�gatif.", Server.config.getMotdColor());
	       	return;
	    }
        SocketManager.GAME_SEND_Eq_PACKET(client.getPlayer(), Apayer);
	}
}