package org.ancestra.evolutive.fight.team;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.entity.creature.Fightable;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.map.Case;

import java.util.ArrayList;

/**
 * Created by Guillaume on 27/08/2014.
 * Hope you'll like it!
 */
public class AlignedPlayerTeam extends PlayerTeam {
    private final Alignement alignement;
    public AlignedPlayerTeam(int id, ArrayList<Case> startCells, Fight fight, Fightable initiateur) {
        super(id, startCells, fight, initiateur);
        this.alignement = ((Player)initiateur).getAlignement();
    }

    public Alignement getAlignement() {
        return alignement;
    }

    @Override
    public boolean canJoin(Player player){
        return player.getAlignement() == alignement && super.canJoin(player);
    }
}
