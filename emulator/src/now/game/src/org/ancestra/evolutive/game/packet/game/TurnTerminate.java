package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

/**
 * Created by Guillaume on 05/08/2014.
 * Hope you'll like it!
 */

@Packet("GT")
public class TurnTerminate implements PacketParser {
    @Override
    public void parse(GameClient client, String packet) {
        try {
            client.getPlayer().setReady(true);
            client.getPlayer().getFight().onReadyChange();
        } catch (Exception e) {
            e.printStackTrace();
            client.getPlayer().setReady(false);
        }
    }
}
