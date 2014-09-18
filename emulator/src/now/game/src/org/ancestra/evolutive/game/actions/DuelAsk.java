package org.ancestra.evolutive.game.actions;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;

/**
 * Created by guillaume on 13/09/14.
 */
public class DuelAsk implements GameActions{
    private final Player player;
    private final String args;
    private final int id;
    private int askedId;

    public DuelAsk(int id, Player player, String args) {
        this.id = id;
        this.player = player;
        this.args = args;
    }

    @Override
    public boolean start() {
        if(player.getMap().getPlaces().equalsIgnoreCase("|")){
            player.send("GA;903;" + player.getId() + ";p");
            return false;
        }
        if(player.isAway() || player.getFight() != null) {
            player.send("GA;903;" + player.getId() + ";o");
            return false;
        }
        askedId = Integer.parseInt(args);
        Player Target = World.data.getPlayer(askedId);
        if(Target == null)
            return false;
        if(Target.isAway() || Target.getFight() != null) {
            player.send("GA;903;" + player.getId() + ";z");
            return false;
        }
        if(Target.getMap() != player.getMap()){
            player.send("GA;903;" + player.getId() + ";p");
            return false;
        }
        player.getGameActionManager().setStatus(GameActionManager.Status.DEFYING);
        player.getMap().send("GA;" + getActionId() + ";" + player.getId() + ";" + askedId);
        return true;
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
        return 900;
    }

    public int getAskedPlayerId(){
        return askedId;
    }
}
