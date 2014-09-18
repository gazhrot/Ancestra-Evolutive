package org.ancestra.evolutive.map.flags;

import org.ancestra.evolutive.fight.Fighter;

/**
 * Created by Guillaume on 08/08/2014.
 * Hope you'll like it!
 */
public interface Flag {
    public String getGt();
    public String getGc();
    public void setWorking(boolean working);
    public void onFighterJoin(Fighter fighter);
    public void onFighterDismiss(Fighter fighter);
    public int getId();
}
