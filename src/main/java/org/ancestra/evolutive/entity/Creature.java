package org.ancestra.evolutive.entity;

import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.Maps;

public class Creature extends Entity {
    public enum STATE {
        IN_FIGHT
    }

    private Maps map;
    private Case cell;
    private STATE state;
    private int orientation;


    /**
     * Cr?er une cr?ature sur la Map donn?e avec l orientation voulue
     * @param id id de la cr?ature
     * @param name nom de la cr?ature
     * @param mapId id de la map
     * @param cellId id de la cellule
     */
    public Creature(int id, String name, int mapId, int cellId,int orientation) {
        this(id, name,World.data.getMap(mapId),World.data.getMap(mapId).getCases().get(cellId),orientation);
    }

    /**
     * Cr?er une cr?ature sur la Map donn?e
     * @param id id de la cr?ature
     * @param name nom de la cr?ature
     * @param mapId id de la map
     * @param cellId id de la cellule
     */
    public Creature(int id, String name, int mapId, int cellId) {
        this(id, name,World.data.getMap(mapId),World.data.getMap(mapId).getCases().get(cellId),0);
    }

    /**
     * Cr?er une cr?ature sur la Map donnee
     * @param id id de la cr?ature
     * @param name nom de la cr?ature
     * @param map map de la creature
     * @param cell cellule de la creature
     */
    public Creature(int id, String name, Maps map, Case cell) {
        this(id, name, map, cell, 0);
    }

    /**
     * Cr?er une cr?ature sur la Map donnee
     * @param id id de la cr?ature
     * @param name nom de la cr?ature
     * @param map map de la creature
     * @param cell cellule de la creature
     * @param orientation orientation initiale
     */
    public Creature(int id, String name, Maps map, Case cell,int orientation) {
        super(id, name);
        this.map = map;
        this.cell = (cell != null) ? cell : map.getCases().get(map.getRandomFreeCell());
        this.orientation = orientation%8;
    }

    /**
     * Retourne la map actuelle de la creature
     * @return map actuelle de la creature
     */
    public Maps getMap() {
        return map;
    }

    /**
     * Change la map de la creature et l y ajoute
     * @param newMapId Identifiant de la map de destination
     */
    public void setMap(short newMapId) {
        setMap(World.data.getMap(newMapId));
    }

    /**
     * Change la map de la creature et l y ajoute
     * @param newMap map de destination
     */
    public void setMap(Maps newMap) {
        if(onMapChange(map,newMap)) {
            this.map = newMap;
        }
    }

    /**
     * Renvoie la cellule actuelle de la creature
     * @return cellule actuelle
     */
    public Case getCell() {
        return cell;
    }

    /**
     * Change la position de la creature
     * @param cell cellule finale de la creature
     */
    public void setCell(Case cell) {
        this.cell = cell;
    }

    /**
     * Retourne l etat courant
     * @return etat courant
     */
    public STATE getState() {
        return this.state;
    }

    /**
     * Change l etat de la creature
     * @param state nouvel etat
     */
    public void setState(STATE state) {
        this.state = state;
    }

    /**
     * Retourne l orientation courante de la creature
     * @return orientation courante
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Change l orientation du personnage
     * Verifie que celle ci est bien inferieur a 7, sinon on prend le modulo
     * @param orientation
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation%8;
    }

    /**
     * Envoie un message a la creature
     * @param message
     */
    public void send(String message) {
        return;
    }

    /**
     * Effectue le changement de map
     * @param newMap
     * @return retourne true si le changement a pu etre effectue
     * false sinon
     */
    private boolean onMapChange(Maps oldMap, Maps newMap) {
        return true;
    }
}
