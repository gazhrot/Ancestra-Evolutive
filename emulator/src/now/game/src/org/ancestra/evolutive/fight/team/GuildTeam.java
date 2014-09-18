package org.ancestra.evolutive.fight.team;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.entity.creature.collector.Collector;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.flags.Flag;

import java.util.ArrayList;

/**
 * Created by Guillaume on 27/08/2014.
 * Hope you'll like it!
 */
public class GuildTeam extends Team {
    private final Collector collecteur;
    public GuildTeam(int id, ArrayList<Case> startCells, Fight fight,Collector collecteur) {
        super(id, startCells, fight);
        this.collecteur = collecteur;
    }

    @Override
    public boolean isRestrictedToGroup() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean areSpectatorAllowed() {
        return false;
    }

    @Override
    public boolean canJoin(Player player) {
        return player.getGuild() == collecteur.getGuild();
    }

    @Override
    public TeamType getTeamType() {
        return null;
    }

    @Override
    public Flag getFlag() {
        return null;
    }

    @Override
    public String getGAMessage() {
        return null;
    }
}
