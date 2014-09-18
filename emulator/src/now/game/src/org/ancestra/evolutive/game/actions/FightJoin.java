package org.ancestra.evolutive.game.actions;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.fight.fight.PVPFight;

/**
 * Created by guillaume on 13/09/14.
 */
public class FightJoin implements GameActions {
    private final Player player;
    private final String args;
    private final int id;

    public FightJoin(int id, Player player, String args) {
        this.id = id;
        this.player = player;
        this.args = args;
    }

    @Override
    public boolean start() {
        String[] infos = args.split(";");
        if(infos.length == 1) {
                player.getMap().getFights().get(Integer.parseInt(infos[0])).joinAsSpect(player);
        }
        else {
            int fightId = Integer.parseInt(infos[0]);
            Fight f = player.getMap().getFights().get(fightId);

            if(player.isAway()){
                player.send("GA;903;" + player.getId() + ";o");
                return false;
            }
            f.joinFight(player,f.getTeamByFlagId(Integer.parseInt(infos[1])));
        }
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
        return 903;
    }
}
