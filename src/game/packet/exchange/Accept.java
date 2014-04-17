package game.packet.exchange;

import objects.Exchange;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;

import common.SocketManager;
import common.World;

import game.GameClient;

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