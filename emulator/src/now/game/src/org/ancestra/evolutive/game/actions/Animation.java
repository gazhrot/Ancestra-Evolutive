package org.ancestra.evolutive.game.actions;

import org.ancestra.evolutive.client.Player;

/**
 * Created by guillaume on 13/09/14.
 */
public class Animation implements GameActions {
    private final Player player;
    private final int id;

    public Animation(int id, Player player) {
        this.id = id;
        this.player = player;
    }

    @Override
    public boolean start() {
        player.getGameActionManager().setStatus(GameActionManager.Status.ANIMATION);
        player.getFight().send("GAF" + id + "|" + player.getId());
        return true;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void onFail(String args) {
        player.getGameActionManager().setStatus(GameActionManager.Status.WAITING);
    }

    @Override
    public void onSuccess(String args) {
        player.getGameActionManager().setStatus(GameActionManager.Status.WAITING);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getActionId() {
        return 0;
    }
}
