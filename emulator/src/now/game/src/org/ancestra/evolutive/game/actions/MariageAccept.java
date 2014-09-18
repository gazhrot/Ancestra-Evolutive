package org.ancestra.evolutive.game.actions;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.house.House;

/**
 * Created by guillaume on 13/09/14.
 */
public class MariageAccept implements GameActions {
    private final Player player;
    private final String args;
    private final int id;

    public MariageAccept(int id, Player player,String args) {
        this.id = id;
        this.player = player;
        this.args = args;
    }

    @Override
    public boolean start() {
        player.setIsOK(Integer.parseInt(args));
        player.say("Oui");
        if(World.data.getMarried(0).getIsOK() > 0 && World.data.getMarried(1).getIsOK() > 0)
            World.data.Wedding(World.data.getMarried(0), World.data.getMarried(1), 1);
        if(World.data.getMarried(0) != null && World.data.getMarried(1) != null)
            World.data.PriestRequest((World.data.getMarried(0)==player?World.data.getMarried(1):World.data.getMarried(0)),
                    (World.data.getMarried(0)==player?World.data.getMarried(1).getMap():World.data.getMarried(0).getMap()), player.getIsTalkingWith());
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
        return 618;
    }
}
