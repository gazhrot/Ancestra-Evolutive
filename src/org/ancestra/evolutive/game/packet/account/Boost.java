package org.ancestra.evolutive.game.packet.account;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;


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