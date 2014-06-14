package org.ancestra.evolutive.game.packet.exchange;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("EQ")
public class OfflineExchange implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(World.data.isMarchandMap(client.getPlayer().getCurMap().getId())) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "113");
			return;
		}
		if (client.getPlayer().getCurMap().getId() == 33 || client.getPlayer().getCurMap().getId() == 38 || client.getPlayer().getCurMap().getId() == 4601 || client.getPlayer().getCurMap().getId() == 4259 || client.getPlayer().getCurMap().getId() == 8036 || client.getPlayer().getCurMap().getId() == 10301) {
			if (client.getPlayer().getCurMap().getStoreCount() >= 25) {
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;25");
				return;
			}
		}else if(client.getPlayer().getCurMap().getStoreCount() >= 6) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;6");
			return;
		}
		long Apayer2 = client.getPlayer().storeAllBuy() / 1000;
		if(client.getPlayer().getKamas() < Apayer2) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "176");
			return;
		}
		if(Apayer2 < 0) {
		    SocketManager.GAME_SEND_MESSAGE(client.getPlayer(), "Erreur de mode marchand, la somme est n�gatif.", Server.config.getMotdColor());
		    return;
		}
		int orientation = Formulas.getRandomValue(1, 3);
		client.getPlayer().setKamas(client.getPlayer().getKamas() - Apayer2);
		client.getPlayer().setOrientation(orientation);
        Maps map = client.getPlayer().getCurMap();
        client.getPlayer().setSeeSeller(true);
        World.data.addSeller(client.getPlayer().getId(), client.getPlayer().getCurMap().getId());
        client.kick();
        for(Player z : map.getPlayers())
        	if(z != null && z.isOnline())
        		SocketManager.GAME_SEND_MERCHANT_LIST(z, z.getCurMap().getId());
	}
}