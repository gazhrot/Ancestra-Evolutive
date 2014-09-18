package org.ancestra.evolutive.fight.ordreJeu;

import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;

import java.util.*;

/**
 * Created by Guillaume on 05/08/2014.
 * Hope you'll like it!
 */
public class ProspectionBasedOrdreJeu implements OrdreJeu {
    private final ArrayList<Fighter> ordre = new ArrayList<>();
    private int currentIndex;
    private Object lock = new Object();

    public ProspectionBasedOrdreJeu(Collection<Fighter> fighter1, Collection<Fighter> fighter2){
        this.ordre.addAll(fighter1);
        this.ordre.addAll(fighter2);
        this.currentIndex=0;
    }


    public void init(){
        Collections.sort(ordre, new Comparator<Fighter>() {
            @Override
            public int compare(Fighter fighter, Fighter fighter2) {
                return fighter2.getInitiative() - fighter.getInitiative();
            }
        });
        currentIndex = -1;
    }

    @Override
    public Fighter getNextFighter(){
        synchronized (lock){
            do {
                currentIndex++;
                if(currentIndex >= ordre.size())currentIndex = 0;
            } while (ordre.get(currentIndex).isDead());
        }
        return ordre.get(currentIndex);
    }

    public void addFighter(Fighter toAdd){
        synchronized (lock) {
            if(!this.ordre.contains(toAdd))
                this.ordre.add(currentIndex+1,toAdd);
        }
    }

    @Override
    public void addFighter(Fighter toAdd,Fighter previousFighter){
        synchronized (lock){
            ordre.add(ordre.indexOf(previousFighter)+1,toAdd);
        }
    }

    @Override
    public void removeFighter(Fighter toRemove){
        synchronized (lock){
            if(this.ordre.contains(toRemove)) {
                if(this.ordre.indexOf(toRemove) >= this.currentIndex)
                    currentIndex--;
                this.ordre.remove(toRemove);

            }
        }
    }

    @Override
    public String generateGTLPaquet(){
        StringBuilder paquet = new StringBuilder("GTL");
        for(Fighter f : ordre){
            paquet.append("|").append(f.getId());
        }
        return paquet.toString();
    }
}
