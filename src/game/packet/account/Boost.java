package game.packet.account;

import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import game.GameClient;

@Packet("AB")
public class Boost implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		try	{
			int stat = Integer.parseInt(packet.substring(2).split("/u000A")[0]);
			client.getPlayer().boostStat(stat);
		} catch(NumberFormatException e){return;};
	}
}