package org.ancestra.evolutive.game.packet.fight;

import org.ancestra.evolutive.fight.team.PlayerTeam;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("fS")
public class BlockSpectators implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
        ((PlayerTeam)client.getPlayer().getFighter().getTeam()).switchSpectatorAllowed();
	}
}