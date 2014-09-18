package org.ancestra.evolutive.game.actions;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Player;

/**
 * Created by Guillaume on 30/08/2014.
 * Hope you'll like it!
 */
public class WeaponAttack implements GameActions {
    private final Player player;
    private final Logger logger;
    private final int id;
    private final String args;
    private int usedPa;

    WeaponAttack(int id,Player player,String args){
        this.id = id;
        this.player = player;
        this.logger = player.getLogger();
        this.args = args;
    }

    @Override
    public boolean start() {
        if(player.getGameActionManager().getStatus() != GameActionManager.Status.WAITING ||
                player.getFight().getCurFighter() != player.getFighter()){
            player.send("GA;0");
            return false;
        }
        player.getFight().tryCaC(player,Integer.parseInt(args));
        this.player.getGameActionManager().setStatus(GameActionManager.Status.ATTACKING);
        return true;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void onFail(String args) {
        player.send("BN");
        player.getGameActionManager().setStatus(GameActionManager.Status.WAITING);
    }

    @Override
    public void onSuccess(String args) {
        player.send("BN");
        player.getGameActionManager().setStatus(GameActionManager.Status.WAITING);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getActionId(){
        return 303;
    }
}
