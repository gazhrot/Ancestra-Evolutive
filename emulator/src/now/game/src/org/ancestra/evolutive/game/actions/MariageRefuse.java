package org.ancestra.evolutive.game.actions;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;

/**
 * Created by guillaume on 13/09/14.
 */
public class MariageRefuse implements GameActions {
    private final Player player;
    private final String args;
    private final int id;

    public MariageRefuse(int id, Player player,String args) {
        this.id = id;
        this.player = player;
        this.args = args;
    }

    @Override
    public boolean start() {
        player.setIsOK(0);
        player.say("Non");
        World.data.Wedding(World.data.getMarried(0), World.data.getMarried(1), 0);
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
        return 619;
    }
}
