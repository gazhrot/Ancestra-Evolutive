package org.ancestra.evolutive.game.actions;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.house.House;

/**
 * Created by guillaume on 13/09/14.
 */
public class HouseAction implements GameActions {
    private final Player player;
    private final String args;
    private final int id;
    private int action;

    public HouseAction(int id, Player player,String args) {
        this.id = id;
        this.player = player;
        this.args = args;
    }

    @Override
    public boolean start() {
        action = Integer.parseInt(args);
        House house = player.getCurHouse();
        if(house != null) {
            switch (action) {
                case 81://V�rouiller maison
                    house.lock(player);
                    break;
                case 97://Acheter maison
                    house.buyIt(player);
                    break;
                case 98://Vendre
                case 108://Modifier prix de vente
                    house.sellIt(player);
                    break;
            }
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
        return 507;
    }
}
