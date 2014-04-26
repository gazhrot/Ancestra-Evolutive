package fr.edofus.ancestra.evolutive.game.packet.exchange;



import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.common.Formulas;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.Server;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.Carte;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("EQ")
public class OfflineExchange implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(World.data.isMarchandMap(client.getPlayer().get_curCarte().get_id())) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "113");
			return;
		}
		if (client.getPlayer().get_curCarte().get_id() == 33 || client.getPlayer().get_curCarte().get_id() == 38 || client.getPlayer().get_curCarte().get_id() == 4601 || client.getPlayer().get_curCarte().get_id() == 4259 || client.getPlayer().get_curCarte().get_id() == 8036 || client.getPlayer().get_curCarte().get_id() == 10301) {
			if (client.getPlayer().get_curCarte().getStoreCount() >= 25) {
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;25");
				return;
			}
		}else if(client.getPlayer().get_curCarte().getStoreCount() >= 6) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;6");
			return;
		}
		long Apayer2 = client.getPlayer().storeAllBuy() / 1000;
		if(client.getPlayer().get_kamas() < Apayer2) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "176");
			return;
		}
		if(Apayer2 < 0) {
		    SocketManager.GAME_SEND_MESSAGE(client.getPlayer(), "Erreur de mode marchand, la somme est n�gatif.", Server.config.getMotdColor());
		    return;
		}
		int orientation = Formulas.getRandomValue(1, 3);
		client.getPlayer().set_kamas(client.getPlayer().get_kamas() - Apayer2);
		client.getPlayer().set_orientation(orientation);
        Carte map = client.getPlayer().get_curCarte();
        client.getPlayer().set_showSeller(true);
        World.data.addSeller(client.getPlayer().get_GUID(), client.getPlayer().get_curCarte().get_id());
        client.kick();
        for(Player z : map.getPersos())
        	if(z != null && z.isOnline())
        		SocketManager.GAME_SEND_MERCHANT_LIST(z, z.get_curCarte().get_id());
	}
}