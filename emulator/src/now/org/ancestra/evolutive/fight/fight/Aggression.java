package org.ancestra.evolutive.fight.fight;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.ordreJeu.OrdreJeu;
import org.ancestra.evolutive.fight.team.Team;
import org.ancestra.evolutive.map.Maps;

/**
 * Created by Guillaume on 06/08/2014.
 * Hope you'll like it!
 */
public class Aggression extends Fight {
    public Aggression(int id, Maps oldMap) {
        super(FightType.PVP, id, oldMap);
    }

    @Override
    protected void onFighterLoose(Fighter looser) {

    }

    @Override
    protected void onFighterWin(Fighter winner) {

    }

    @Override
    protected String getGE(Team winner, Team looser) {
        return null;
    }

    @Override
    public OrdreJeu getOrdreJeu() {
        return null;
    }

    @Override
    public boolean canJoinTeam(Player fighter, Team team) {
        if(fighter.getAlignement() != ((Player)team.getTeam().get(0).getFightable()).getAlignement())
            return false;
        return true;
    }

    @Override
    public String getFightInfos() {
        return null;
    }
}
