package org.ancestra.evolutive.map.flags;

import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.team.Team;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.Maps;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Guillaume on 07/08/2014.
 * Hope you'll like it!
 * Default flag with no alignement can be player, mob or collector
 */
public class DefaultFightFlag implements Flag {
    private final int id;
    private final Maps map;
    private final String GcMessage;
    private final String prefix;
    private final ConcurrentMap<Integer,String> GtMessage = new ConcurrentHashMap<>();
    private boolean working;

    /**
     * Creer un flag correspondant
     * Il ne prend pas en compte l alignement
     * @param id id du flag
     * @param map map ou le flag doit apparaitre
     * @param cellId id de la cellule ou apparaitra le flag
     * @param team team referente
     */
    public DefaultFightFlag(int id,Maps map, int cellId,Team team){
        this.working = true;
        this.id = id;
        this.map = map;
        this.GcMessage = this.id + ";" + cellId + ";" + team.getTeamType().id + ";-1";//default veut dire qu il n y
        //a pas d alignement
        this.prefix = "Gt" + id;
        for(Fighter fighter : team.getTeam().values()){
            GtMessage.put(fighter.getId(), generateGt(fighter));
        }
    }

    public String getGc(){
        return this.GcMessage;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public String getGt(){
        StringBuilder gt = new StringBuilder(prefix);
        for(String str : GtMessage.values()){
            gt.append("|+").append(str);
        }
        return gt.toString();
    }

    public void onFighterJoin(Fighter fighter){
        String gt = generateGt(fighter);
        GtMessage.put(fighter.getId(),gt);
        if(working)this.map.send(prefix + "|+" + gt);
    }

    public void onFighterDismiss(Fighter fighter){
        if(GtMessage.containsKey(fighter.getId())){
            GtMessage.remove(fighter.getId());
            if(working)this.map.send(prefix + "|-" + fighter.getId());
        }
    }

    public void setWorking(boolean working){
        this.working = working;
    }

    private String generateGt(Fighter fighter){
        return fighter.getId() + ";" + fighter.getName() + ";" + fighter.getLvl();
    }
}
