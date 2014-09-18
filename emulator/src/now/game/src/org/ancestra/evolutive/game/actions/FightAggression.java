package org.ancestra.evolutive.game.actions;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.creature.collector.Collector;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.fight.fight.Aggression;
import org.ancestra.evolutive.fight.fight.PVTFight;

/**
 * Created by guillaume on 13/09/14.
 */
public class FightAggression implements GameActions {
    private final Player player;
    private final String args;
    private final int id;
    private int askedId;

    public FightAggression(int id, Player player, String args) {
        this.id = id;
        this.player = player;
        this.args = args;
    }

    @Override
    public boolean start() {
        if(player == null || player.getFight() != null)return false;
        if(player.getIsTalkingWith() != 0 ||
                player.getIsTradingWith() != 0 ||
                player.getCurJobAction() != null ||
                player.getCurExchange() != null ||
                player.isAway())
            return false;

        int id = Integer.parseInt(args);
        Collector target = World.data.getPerco(id);
        if(target == null || target.get_inFight() > 0) return false;
        if(target.get_Exchange()) {
            player.send("IM1180");
            return false;
        }
        player.getMap().send("GA;909;" + player.getId() + ";" + id);
        //new PVTFight()
        return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void onFail(String args) {

    }

    @Override
    public void onSuccess(String args) {

    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getActionId() {
        return 909;
    }
}
