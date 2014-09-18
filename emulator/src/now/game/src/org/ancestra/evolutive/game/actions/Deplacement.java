package org.ancestra.evolutive.game.actions;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.CryptManager;
import org.ancestra.evolutive.common.Pathfinding;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.InteractiveObject;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Guillaume on 22/08/2014.
 * Hope you'll like it!
 */
public class Deplacement implements GameActions {
    private final int id;
    private final Player player;
    private String args;
    private String finalPath;
    private String initialPath;
    private int step;

    public Deplacement(int id,Player player,String args) {
        this.player = player;
        this.id = id;
        this.args = args;
    }

    @Override
    public boolean start() {
        player.setEmoteActive(0);
        this.finalPath = this.initialPath = args;
        if(player.getPodUsed() > player.getMaxPod()){
            player.send("Im112");
            player.send("GA;0");
            return false;
        }
        if(player.getGameActionManager().getStatus() != GameActionManager.Status.WAITING){
            player.send("GA;0");
            return false;
        }
        AtomicReference<String> pathRef = new AtomicReference<>(args);
        if(player.getFight() == null) {
            int result = Pathfinding.isValidPath(player.getMap(), player.getCell().getId(), pathRef, null);
            if (result == 0) {
                player.send("GA;0");
                return false;
            }
            if (result == -1000) {
                player.getLogger().info("cheat? Tentative de  deplacement avec un path invalide");
                args = CryptManager.getHashedValueByInt(player.getOrientation())
                        + CryptManager.cellID_To_Code(player.getCell().getId());
            } else {
                args = pathRef.get();
            }
            player.getMap().send("GA" + id + ";" + getActionId() + ";" + player.getId()
                    + ";" + "a" + CryptManager.cellID_To_Code(player.getCell().getId()) + args);
            this.finalPath = args;
            player.getGameActionManager().setStatus(GameActionManager.Status.MOVING);
            return true;
        } else {
            step = player.getFight().fighterDeplace(player.getFighter(),pathRef,id);
            this.finalPath = pathRef.get();
            if(step == -1){
                player.send("GA;0");
                return false;
            }
            return true;
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public void onFail(String args) {
        int newCellID = Integer.parseInt(args);
        player.setPosition(player.getMap().getCases().get(newCellID));
        player.setOrientation(CryptManager.getFinalOrientation(initialPath));
        player.send("BN");
    }

    @Override
    public void onSuccess(String args) {
        Case newCell = player.getMap().getCases().get(CryptManager.getFinalCaseId(finalPath));
        player.setPosition(newCell);
        player.setOrientation(CryptManager.getFinalOrientation(finalPath));
        if(player.getFight() == null){
            Case targetCell = player.getMap().getCases().get(CryptManager.getFinalCaseId(initialPath));
            InteractiveObject IO = targetCell.getInteractiveObject();
            if(IO != null) {
                IO.getActionIO(player, targetCell);
                IO.getSignIO(player, targetCell.getId());
            }
            player.getGameActionManager().setStatus(GameActionManager.Status.WAITING);
            player.getMap().onPlayerArriveOnCell(player,newCell.getId());
        }
        else {
            player.getFight().onGK(player, step);
            player.getGameActionManager().addAction(
                    new Animation(player.getGameActionManager().nextActionId(),player));
        }
    }

    @Override
    public int getId() {
        return this.id;
    }


    @Override
    public int getActionId(){
        return 1;
    }
}
