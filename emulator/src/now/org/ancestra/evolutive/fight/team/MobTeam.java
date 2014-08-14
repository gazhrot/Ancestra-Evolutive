package org.ancestra.evolutive.fight.team;

import org.ancestra.evolutive.entity.monster.MobGroup;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.flags.DefaultFightFlag;
import org.ancestra.evolutive.map.flags.Flag;

import java.util.ArrayList;

/**
 * Created by Guillaume on 03/08/2014.
 * Hope you'll like it!
 */
public class MobTeam extends Team{
    private final Flag flag;

    public MobTeam(int id, ArrayList<Case> startCells,MobGroup mobGroup, Fight fight) {
        super(id, startCells,fight);
        this.flag = new DefaultFightFlag(mobGroup.getMap().getNextFreeId(),mobGroup.getMap(),
                mobGroup.getCell().getId(),this);
    }

    @Override
    public boolean isRestrictedToGroup() {
        return true;
    }

    @Override
    public boolean isClosed() {
        return true;
    }

    @Override
    public boolean areSpectatorAllowed() {
        return true;
    }

    @Override
    public TeamType getTeamType() {
        return TeamType.MOB;
    }

    @Override
    public Flag getFlag() {
        return flag;
    }
}
