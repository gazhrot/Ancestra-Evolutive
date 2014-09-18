package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.CryptManager;
import org.ancestra.evolutive.common.Pathfinding;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.creature.collector.Collector;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.fight.PVPFight;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.game.GameAction;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.InteractiveObject;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@Packet("GA")
public class SendActions implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
        try	{
            int action = Integer.parseInt(packet.substring(2,5));
            if(client.getPlayer() != null) {
                client.getPlayer().getGameActionManager().createAction(action,packet.substring(5));
            }
        } catch(NumberFormatException e) {return;}


	}
}