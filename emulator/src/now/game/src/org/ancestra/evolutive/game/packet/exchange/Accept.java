package org.ancestra.evolutive.game.packet.exchange;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;
import org.ancestra.evolutive.util.exchange.PlayerExchange;

@Packet("EA")
public class Accept implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getIsTradingWith() == 0)
			return;
		
		Player target = World.data.getPlayer(client.getPlayer().getIsTradingWith());
		
		if(target == null)
			return;
		
		SocketManager.GAME_SEND_EXCHANGE_CONFIRM_OK(client, 1);
		SocketManager.GAME_SEND_EXCHANGE_CONFIRM_OK(target.getAccount().getGameClient(), 1);
		
		PlayerExchange echg = new PlayerExchange(target, client.getPlayer());
		client.getPlayer().setCurExchange(echg);
		client.getPlayer().setIsTradingWith(target.getId());
		target.setCurExchange(echg);
		target.setIsTradingWith(client.getPlayer().getId());
	}
}