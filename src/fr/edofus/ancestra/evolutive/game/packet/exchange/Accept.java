package fr.edofus.ancestra.evolutive.game.packet.exchange;



import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.Exchange;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("EA")
public class Accept implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_isTradingWith() == 0)
			return;
		
		Player target = World.data.getPersonnage(client.getPlayer().get_isTradingWith());
		
		if(target == null)
			return;
		
		SocketManager.GAME_SEND_EXCHANGE_CONFIRM_OK(client, 1);
		SocketManager.GAME_SEND_EXCHANGE_CONFIRM_OK(target.get_compte().getGameClient(), 1);
		
		Exchange echg = new Exchange(target, client.getPlayer());
		client.getPlayer().setCurExchange(echg);
		client.getPlayer().set_isTradingWith(target.get_GUID());
		target.setCurExchange(echg);
		target.set_isTradingWith(client.getPlayer().get_GUID());
	}
}