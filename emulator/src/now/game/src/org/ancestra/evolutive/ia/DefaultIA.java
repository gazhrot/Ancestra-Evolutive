package org.ancestra.evolutive.ia;

import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.fight.Fight;

/**
 * Created by Guillaume on 29/08/2014.
 * Hope you'll like it!
 */
public class DefaultIA implements NewIA {
    private Fight fight;
    private Fighter fighter;
    private boolean stop;

    @Override
    public void execute() {
        if(fight.getCurFighter() != fighter || fighter.isDead()) return;



    }
}
