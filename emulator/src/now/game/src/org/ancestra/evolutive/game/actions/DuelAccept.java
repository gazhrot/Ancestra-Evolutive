package org.ancestra.evolutive.game.actions;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.fight.fight.PVPFight;

/**
 * Created by guillaume on 13/09/14.
 */
public class DuelAccept implements GameActions {
    private final Player player;
    private final String args;
    private final int id;

    public DuelAccept(int id, Player player, String args) {
        this.id = id;
        this.player = player;
        this.args = args;
    }

    @Override
    public boolean start() {
        int id = Integer.parseInt(args);
        Player asker = World.data.getPlayer(id);
        if(asker == null || asker.getMap() != player.getMap() || asker.isAbsent() || asker.isAbsent() ||
                player.getGameActionManager().getStatus() != GameActionManager.Status.WAITING){
            asker.send("GA;903;" + asker.getId() + ";l");
            return false;
        }
        if(((DuelAsk)asker.getGameActionManager().getActionByActionId(900)).getAskedPlayerId() != player.getId()){
            return false;
        }
        asker.getMap().send("GA;901;" + asker.getId() + ";" + player.getId());
        new PVPFight(asker.getMap().getNextFreeId(),asker.getMap(),asker,player);
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
        return id;
    }


    @Override
    public int getActionId(){
        return 901;
    }
}
