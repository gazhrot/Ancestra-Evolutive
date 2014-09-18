package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.CryptManager;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameAction;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.InteractiveObject;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("GK")
public class ActionAck implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
        boolean isOk = packet.charAt(2) == 'K';
        int gameActionId;
        String[] infos = packet.substring(3).split("\\|");
        try	{
            gameActionId = Integer.parseInt(infos[0]);
            client.getPlayer().getGameActionManager().endAction(gameActionId,isOk,infos.length>1?infos[1]:"");
        } catch(Exception e) {
            e.printStackTrace();
        }


	}
}