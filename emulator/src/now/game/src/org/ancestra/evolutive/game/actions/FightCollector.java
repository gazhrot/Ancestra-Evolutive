package org.ancestra.evolutive.game.actions;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.fight.fight.Aggression;

/**
 * Created by guillaume on 13/09/14.
 */
public class FightCollector implements GameActions {
    private final Player player;
    private final String args;
    private final int id;
    private int askedId;

    public FightCollector(int id, Player player, String args) {
        this.id = id;
        this.player = player;
        this.args = args;
    }

    @Override
    public boolean start() {
        if (player == null || player.getFight() != null ) return false;
        int id = Integer.parseInt(args);
        Player target = World.data.getPlayer(id);
        if (target == null || !target.isOnline() || target.getFight() != null
                || target.getMap().getId() != player.getMap().getId()
                || target.getAlignement() == player.getAlignement()
                || player.getMap().getPlaces().equalsIgnoreCase("|")
                || !target.isCanAggro())
            return false;

        player.toggleWings('+');
        player.getMap().send("GA;906;"+player.getId()+";" + id);

        if (target.getAlignement() == Alignement.NEUTRE) {
            player.setDeshonor(player.getDeshonor() + 1);
            player.send("Im084;1");
        }
        new Aggression(player.getMap().getNextFreeId(),player.getMap(),player,target);
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
        return 906;
    }
}
