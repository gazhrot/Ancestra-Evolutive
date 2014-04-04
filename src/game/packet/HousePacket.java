package game.packet;

import objects.House;
import game.GameClient;
import game.packet.handler.Packet;

public class HousePacket {
	
	@Packet("hB")
	public static void buy(GameClient client, String packet) {
		House.HouseAchat(client.getPlayer());
	}

	@Packet("hG")
	public static void guild(GameClient client, String packet) {
		packet = packet.substring(2);
		House.parseHG(client.getPlayer(), (packet.isEmpty()?null:packet));
	}

	@Packet("hQ")
	public static void leave(GameClient client, String packet) {
		packet = packet.substring(2);
		House.Leave(client.getPlayer(), packet);
	}

	@Packet("hS")
	public static void price(GameClient client, String packet) {
		packet = packet.substring(2);
		House.SellPrice(client.getPlayer(), packet);
	}

	@Packet("hV")
	public static void close(GameClient client, String packet) {
		House.closeBuy(client.getPlayer());
	}
}