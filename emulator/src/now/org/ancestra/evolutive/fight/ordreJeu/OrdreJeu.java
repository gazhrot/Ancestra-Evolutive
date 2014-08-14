package org.ancestra.evolutive.fight.ordreJeu;

import org.ancestra.evolutive.fight.Fighter;

/**
 * Created by Guillaume on 06/08/2014.
 * Hope you'll like it!
 */
public interface OrdreJeu {
    /**
     * Renvoie le prochain joueur
     * @return prochain fighter qui debute son tour
     */
    public Fighter getNextFighter();

    /**
     * Ajoute un fighter apres un autre
     * @param toAdd fighter a ajouter
     * @param previousFighter fighter qui doit precede
     */
    public void addFighter(Fighter toAdd,Fighter previousFighter);

    /**
     * Ajoute un fighter
     * @param toAdd fighter a ajouter
     */
    public void addFighter(Fighter toAdd);

    /**
     * Retire un objet de la liste
     * @param toRemove
     */
    public void removeFighter(Fighter toRemove);

    /**
     * genere le paquet GTL contenant l'ordre de jeu des joueurs
     * @return paquet GTL
     */
    public String generateGTLPaquet();

    /**
     * initie l ordre de jeu
     */
    public void init();

}
