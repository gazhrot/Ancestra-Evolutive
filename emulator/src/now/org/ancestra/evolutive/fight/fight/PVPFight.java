package org.ancestra.evolutive.fight.fight;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.ordreJeu.OrdreJeu;
import org.ancestra.evolutive.fight.ordreJeu.ProspectionBasedOrdreJeu;
import org.ancestra.evolutive.fight.team.PlayerTeam;
import org.ancestra.evolutive.fight.team.Team;
import org.ancestra.evolutive.map.Maps;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.TimeZone;

/**
 * Created by Guillaume on 07/08/2014.
 * Hope you'll like it!
 */
public class PVPFight extends Fight {
    private static final Random r = new Random();
    private final ProspectionBasedOrdreJeu ordreJeu;


    public PVPFight(int id, Maps oldMap,Player initiateur1,Player initiateur2) {
        super(FightType.DEFY, id, oldMap);
        logger = (Logger)LoggerFactory.getLogger("[defi]" + initiateur1.getName() + "vs" + initiateur2.getName());
        initiateur1.send(this.GJKPacket);
        initiateur2.send(this.GJKPacket);
        int random = r.nextInt(2);
        team0 = new PlayerTeam(random,parsePlaces(random),this,initiateur1);
        random = Math.abs(random-1);
        team1 = new PlayerTeam(random,parsePlaces(random),this,initiateur2);
        addPlayer(team0,initiateur1);
        addPlayer(team1,initiateur2);
        this.send("ILF0");//Desactive regeneration
        for(Fighter f : team0.getTeam().values()){
            this.send("GA;950;" + f.getId() + ";" + f.getId() + ",8,0" );
            this.send("GA;950;" + f.getId() + ";" + f.getId() + ",3,0" );
        }
        for(Fighter f : team1.getTeam().values()){
            this.send("GA;950;" + f.getId() + ";" + f.getId() + ",8,0" );
            this.send("GA;950;" + f.getId() + ";" + f.getId() + ",3,0" );
        }
        ordreJeu = new ProspectionBasedOrdreJeu(team0.getTeam().values(),team1.getTeam().values());
        this.send(this.getMap().getGmMessage());

    }

    @Override
    protected void onFighterLoose(Fighter looser) {
        ((Player)looser.getFightable()).refreshMapAfterFight();
    }

    @Override
    protected void onFighterWin(Fighter winner) {
        ((Player)winner.getFightable()).refreshMapAfterFight();
    }

    @Override
    protected String getGE(Team winner, Team looser) {
        final StringBuilder packet = new StringBuilder("GE");
        packet.append(System.currentTimeMillis()-startTime).append("|")
                .append(0).append("|0|");//initiateur

        for(Fighter f : winner.getTeam().values()){
            if(f.getPDV() == 0 || f.hasLeft()){
                packet.append("2;").append(f.getId()).append(";").append(f.getName()).append(";").append(f.getLvl()).append(";1").append(";").append(f.xpString(";")).append(";;;;|");
            }
            else{
                packet.append("2;").append(f.getId()).append(";").append(f.getName()).append(";").append(f.getLvl()).append(";0").append(";").append(f.xpString(";")).append(";;;;|");
            }
        }
        for(Fighter f : looser.getTeam().values()){
            packet.append("0;").append(f.getId()).append(";").append(f.getName()).append(";").append(f.getLvl()).append(";1").append(";").append(f.xpString(";")).append(";;;;|");
        }
        return packet.toString();
    }

    @Override
    public OrdreJeu getOrdreJeu() {
        return ordreJeu;
    }

    @Override
    public boolean canJoinTeam(Player fighter, Team team) {
        return true;
    }

    @Override
    public String getFightInfos() {
        StringBuilder infos = new StringBuilder();
        infos.append(this.getId()).append(";");
        long time = startTime + TimeZone.getDefault().getRawOffset();
        infos.append((startTime == 0?"-1":time)).append(";");

        infos.append(team0.getTeamType().id).append(",")
                .append("0").append(",")//Car on se fiche de l'alignement du joueur
                .append(team0.getTeam().size()).append(";");

        infos.append(team1.getTeamType().id).append(",")
                .append("0").append(",")//Car on se fiche de l'alignement du joueur 2, on est en pvp..
                .append(team1.getTeam().size()).append(";");
        return infos.toString();
    }
}
