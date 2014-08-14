package org.ancestra.evolutive.entity.monster;


import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.IA;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Creature;
import org.ancestra.evolutive.entity.Fightable;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.map.Case;

import java.util.Map;

public class Mob extends Fightable{
    private final MobGrade grade;
    private final Stats stats;

    public Mob(int id,Case cell,MobGrade grade){
        super(id, grade.getName(), cell);
        this.grade = grade;
        this.stats = new Stats(grade.getStats());
        helper = new MobHelper(this);
    }


    @Override
    public Fighter.FighterType getFighterType() {
        return Fighter.FighterType.CREATURE;
    }

    @Override
    public int getGFX() {
        return grade.getTemplate().getGfx();
    }

    @Override
    public int getMaxPdv() {
        return grade.getMaxPdv();
    }

    @Override
    public int getPdv() {
        return grade.getMaxPdv();
    }

    @Override
    public int getInitiative(){
        return this.grade.getInitiative();
    }

    @Override
    public void setPdv(int pdv) {
    }

    @Override
    public Stats getStats() {
        return stats;
    }

    @Override
    public int getIa(){
        return this.grade.getTemplate().getIa();
    }

    @Override
    public int getLevel() {
        return grade.getLevel();
    }

    @Override
    public Map<Integer, SpellStats> getSpells(){
        return this.grade.getSpells();
    }

    @Override
    public void onStartTurn(Fighter fighter) {
        if(fighter == this.getFighter()) {
            IA ia = new IA(this.getFighter(),this.getFight());
            World.data.getWorker().execute(ia);
        }
    }

    public MobGrade getGrade(){
        return this.grade;
    }
}
