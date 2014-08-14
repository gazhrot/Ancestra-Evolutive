package org.ancestra.evolutive.fight.team;

import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.flags.Flag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Guillaume on 03/08/2014.
 * Hope you'll like it!
 */
public abstract class Team {
    public enum TeamType {
        PLAYER(0),
        MOB(1),
        PLAYER_NOJOIN(2),
        COLLECTOR(3);
        public final int id;
        private TeamType(int id){
            this.id = id;
        }
    }

    public final Fight fight;
    private final HashMap<Integer,Fighter> team = new HashMap<>();
    private final int id;
    private final Object locker = new Object();
    public final ArrayList<Case> startCells;

    public Team(int id,ArrayList<Case> startCells,Fight fight){
        this.id = id;
        this.startCells = startCells;
        this.fight = fight;
    }

    /**
     * Permet de savoir si une fais partie ou non des cellules de departs de la team
     * @param cell cellule a comparer
     * @return true si la cellule fait bien partie des cellules de departs de la team,
     * false sinon
     */
    public boolean groupCellContains(int cell){
        for(Case cas : startCells){
            if(cas.getId() == cell)
                return true;
        }
        return false;
    }

    public int getId(){
        return this.id;
    }

    /**
     * Envoie un message a l ensmeble de l equipe
     * @param message message a envoyer
     */
    public void send(String message){
        synchronized (locker){
            for(Fighter fighter : team.values()){
                fighter.send(message);
            }
        }
    }

    /**
     * Ajoute un joueur dans l equipe
     * @param fighter fighter a ajouter
     */
    public void addFighter(Fighter fighter){
        synchronized (locker){
            this.team.put(fighter.getId(), fighter);
            this.getFlag().onFighterJoin(fighter);
        }
    }

    /**
     * Retire un joueur de l equipe
     * @param fighter
     */
    public void removeFighter(Fighter fighter){
        synchronized (locker){
            if(this.team.containsKey(fighter.getId())){
                this.team.remove(fighter.getId());
                this.getFlag().onFighterDismiss(fighter);
            }
        }
    }

    public Map<Integer,Fighter> getTeam(){
        synchronized (locker){
            return this.team;
        }
    }

    /**
     * Permet de savoir si l equipe autorise les memebres exterieur au groupe
     * @return true si seulement le groupe est autorise
     * false sinon
     */
    public abstract boolean isRestrictedToGroup();

    /**
     * Permet de savoir si une team est fermee ou non(impossible de rejoidre)
     * @return true si aucune personne n a le droit de rejoindre
     * false sinon
     */
    public abstract boolean isClosed();

    /**
     * Permet de savoir si l equipe autorise les spectateurs ou non
     * @return true si les spectatuers sont autorises
     * false sinon
     */
    public abstract boolean areSpectatorAllowed();

    public abstract TeamType getTeamType();

    public abstract Flag getFlag();
}


