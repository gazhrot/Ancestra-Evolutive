package org.ancestra.evolutive.game.actions;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Action;
/**
 * Created by Guillaume on 30/08/2014.
 * Hope you'll like it!
 */
public class MapAction implements GameActions {
    private final Player player;
    private final String args;
    private final int id;
    private int cell;
    private int action;

    public MapAction(int id, Player player,String args) {
        this.id = id;
        this.player = player;
        this.args = args;
    }

    @Override
    public boolean start() {
        try {
            cell = Integer.parseInt(args.split(";")[0]);
            action = Integer.parseInt(args.split(";")[1]);
            return player.getMap().getCases().get(cell).startAction(player,action,id,cell);
        } catch(Exception e) {
            return false;
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public void onFail(String args) {
        player.setCurJobAction(null);
    }

    @Override
    public void onSuccess(String args) {
        player.finishActionOnCell(cell,action);
    }

    @Override
    public int getId() {
        return id;
    }


    @Override
    public int getActionId(){
        return 500;
    }
}
