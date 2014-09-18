package org.ancestra.evolutive.entity.creature;

import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.map.Case;

import java.util.Map;

public abstract class Fightable extends Creature{
    private Fighter fighter;

    public Fightable(int id, String name, Case cell) {
        super(id, name, cell);
    }

    public Fightable(int id, String name, int Mapid,int cell) {
        super(id, name, Mapid,cell);
    }

    public void setCell(Case cell){
        this.cell = cell;
    }

    public boolean isReady(){
        return true;
    }

    public Fighter getFighter(){
        return this.fighter;
    }

    public void setFighter(Fighter fighter){
        this.fighter = fighter;
    }

    public abstract Fighter.FighterType getFighterType();

    public abstract int getGFX();

    public abstract int getMaxPdv();

    public abstract int getPdv();

    public abstract void setPdv(int pdv);

    public abstract Stats getStats();

    public abstract int getLevel();

    public abstract int getIa();

    public abstract int getInitiative();

    public abstract Map<Integer, SpellStats> getSpells();

    public abstract void onStartTurn(Fighter fighter);

}
