package org.ancestra.evolutive.game.actions;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.fight.spell.SpellStats;

/**
 * Created by Guillaume on 29/08/2014.
 * Hope you'll like it!
 */
public class SpellAttack implements GameActions {
    private final Player player;
    private final Logger logger;
    private final int id;
    private final String args;
    private int usedPa;

    SpellAttack(int id,Player player,String args){
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
        String[] splt = args.split(";");
        int spellID = Integer.parseInt(splt[0]);
        int caseID = Integer.parseInt(splt[1]);

        SpellStats SS = player.getSortStatBySortIfHas(spellID);
        if(SS == null){
            player.send("GA;0");
            return false;
        }
        this.player.getFight().send("GAS" + this.player.getId());
        this.usedPa = SS.getPACost();
        this.player.getFight().tryCastSpell(player.getFighter(),SS,caseID,getId());
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
        return 300;
    }
}
