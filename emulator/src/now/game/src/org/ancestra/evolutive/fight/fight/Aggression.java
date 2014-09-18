package org.ancestra.evolutive.fight.fight;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.ordreJeu.OrdreJeu;
import org.ancestra.evolutive.fight.team.AlignedPlayerTeam;
import org.ancestra.evolutive.fight.team.Team;
import org.ancestra.evolutive.map.Maps;

import java.util.TimeZone;

/**
 * Created by Guillaume on 06/08/2014.
 * Hope you'll like it!
 */
public class Aggression extends PVPFight {

    public Aggression(int id, Maps oldMap, Player initiateur1, Player initiateur2) {
        super(id, oldMap, initiateur1, initiateur2);
    }

    @Override
    protected void onFighterLoose(Fighter looser) {
        super.onFighterLoose(looser);
    }

    @Override
    protected void onFighterWin(Fighter winner) {

    }

    @Override
    public String getFightInfos() {
        StringBuilder infos = new StringBuilder();
        infos.append(this.getId()).append(";");
        long time = startTime + TimeZone.getDefault().getRawOffset();
        infos.append((startTime == 0?"-1":time)).append(";");

        infos.append(team0.getTeamType().id).append(",")
                .append(((AlignedPlayerTeam)team0).getAlignement()).append(",")
                .append(team0.getTeam().size()).append(";");

        infos.append(team1.getTeamType().id).append(",")
                .append(((AlignedPlayerTeam)team1).getAlignement()).append(",")
                .append(team1.getTeam().size()).append(";");
        return infos.toString();
    }
}
