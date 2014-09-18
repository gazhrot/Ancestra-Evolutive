package org.ancestra.evolutive.fight.team;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Group;
import org.ancestra.evolutive.entity.creature.Fightable;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.flags.DefaultFightFlag;
import org.ancestra.evolutive.map.flags.Flag;

import java.util.ArrayList;

public class PlayerTeam extends Team {
    public final Fightable initiateur;
    private final Flag flag;
    private boolean restrictToGroup;
    private boolean closed;
    private boolean askForHelp;
    private boolean spectatorAllowed;


    public PlayerTeam(int id, ArrayList<Case> startCells,Fight fight,Fightable initiateur) {
        super(id, startCells,fight);
        this.initiateur = initiateur;
        this.restrictToGroup = false;
        this.closed = false;
        this.askForHelp = false;
        this.spectatorAllowed = false;
        this.flag = new DefaultFightFlag(initiateur.getMap().getNextFreeId(),initiateur.getMap(),
                initiateur.getCell().getId()+2
        ,this);
    }

    public void switchClosed(){
        this.closed = !this.closed;
        fight.getOldMap().send("Go" + (closed ? "+A" : "-A") + initiateur.getId());
        this.send(closed?"Im095":"Im096");
    }

    public void switchRestrictToGroup(){
        this.restrictToGroup = !this.restrictToGroup;
        fight.getOldMap().send("Go" + (restrictToGroup ? "+P" : "-P") + initiateur.getId());
        this.send(restrictToGroup?"Im093":"Im094");
    }

    public void switchAskForHelp(){
        this.askForHelp = !askForHelp;
        fight.getOldMap().send("Go" + (askForHelp?"+H":"-H") + initiateur.getId());
        this.send(askForHelp?"Im0103":"Im0104");
    }

    public void switchSpectatorAllowed(){
        this.spectatorAllowed = !spectatorAllowed;
        fight.onSpectatorBehaviourChange(this);
    }

    public void showCase(int id,int cellId){
        this.send("Gf" + id + "|" + cellId);
    }

    public boolean isInitiateur(Fighter fighter){
        return isInitiateur(fighter.getFightable());
    }

    public boolean isInitiateur(Fightable fightable){
        return initiateur == fightable;
    }

    @Override
    public boolean isRestrictedToGroup() {
        return restrictToGroup;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public boolean areSpectatorAllowed() {
        return spectatorAllowed;
    }

    @Override
    public boolean canJoin(Player player) {
        if(isClosed()) return false;
        if(isRestrictedToGroup()){
            Group g = ((Player)getTeam().get(player.getId()).getFightable()).getGroup();
            if(g == null || !g.getPlayers().contains(player)){
                    return false;
            }
        }
        return true;
    }

    @Override
    public TeamType getTeamType(){
        return TeamType.PLAYER;
    }

    @Override
    public Flag getFlag() {
        return flag;
    }

    @Override
    public String getGAMessage(){
        StringBuilder ga = new StringBuilder();
        for(Fighter fighter : getTeam().values()){
            ga.append("GA;950;").append(fighter.getId()).append(";")
                    .append(fighter.getId()).append(",8,0").append((char)0x00);
            ga.append("GA;950;").append(fighter.getId()).append(";")
                    .append(fighter.getId()).append(",3,0").append((char)0x00);
        }
        return ga.toString();
    }
}
