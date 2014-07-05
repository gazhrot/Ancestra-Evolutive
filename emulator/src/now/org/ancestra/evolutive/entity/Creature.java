package org.ancestra.evolutive.entity;

import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.Maps;

public class Creature extends Entity {
    public enum State {
        IN_FIGHT
    }

    protected Maps map;
    protected Case cell;
    private State state;
    private int orientation;


    /**
     * Creer une creature sur la Map donnee avec l orientation voulue
     * @param id id de la creature
     * @param name nom de la creature
     * @param mapId id de la map
     * @param cellId id de la cellule
     */
    public Creature(int id, String name, int mapId, int cellId, int orientation) {
        this(id, name, World.data.getMap(mapId), World.data.getMap(mapId).getCases().get(cellId), orientation);
    }

    /**
     * Creer une creature sur la Map donnee
     * @param id id de la creature
     * @param name nom de la creature
     * @param mapId id de la map
     * @param cellId id de la cellule
     */
    public Creature(int id, String name, int mapId, int cellId) {
        this(id, name, World.data.getMap(mapId), World.data.getMap(mapId).getCases().get(cellId), 0);
    }

    /**
     * Creer une creature sur la Map donnee
     * @param id id de la creature
     * @param name nom de la creature
     * @param map map de la creature
     * @param cell cellule de la creature
     */
    public Creature(int id, String name, Maps map, Case cell) {
        this(id, name, map, cell, 0);
    }

    /**
     * Creer une creature sur la Map donnee
     * @param id id de la creature
     * @param name nom de la creature
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
     * Change la position de la creature et l y ajoute
     * @param newMapId Identifiant de la map de destination
     * @param cellId Identifiant de la cellule de destination
     */
    public void setPosition(int newMapId,int cellId) {
        setPosition(World.data.getMap(newMapId),World.data.getMap(newMapId).getCases().get(cellId));
    }

    /**
     * Change la position de la creature et l y ajoute
     * @param newMap Identifiant de la map de destination
     * @param cell Identifiant de la nouvelle cellule
     */
    public void setPosition(Maps newMap,Case cell) {
        if(onPositionChange(this.cell,cell)) {
            this.map = newMap;
            this.cell = cell;
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
        if(this.onPositionChange(this.cell,cell)){
            this.cell = cell;
        }
    }

    /**
     * Retourne l etat courant
     * @return etat courant
     */
    public State getState() {
        return this.state;
    }

    /**
     * Change l etat de la creature
     * @param state nouvel etat
     */
    public void setState(State state) {
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
        if(orientation>8){
            this.orientation = 8;
            return;
        }
        if(orientation < 1){
            this.orientation = 1;
            return;
        }
    }



    /**
     * Effectue le changement de position
     * @param oldCell ancienne case
     * @param newCell nouvelle case
     * @return retourne true si le changement a pu etre effectue
     * false sinon
     */
    protected boolean onPositionChange(Case oldCell, Case newCell) {
        if(newCell == null || newCell.getMap() == null) {
            return false;
        }
        if(newCell.getMap() == oldCell.getMap()) {
            return onMoveCell(oldCell,newCell);
        } else {
            oldCell.getMap().removeEntity(this);
            oldCell.removeCreature(this);
            newCell.getMap().addEntity(this);
            newCell.addCreature(this);
            return onMapChange(oldCell, newCell);
        }
    }

    /**
     * Lorsque le mouvement se situe uniquement sur une map
     * @return true ou false si l'action a echoue
     */
    protected boolean onMoveCell(Case oldCell, Case newCell){
        return true;
    }

    /**
     * Lorsque le mouvement se implique un changement de map
     * @return true ou false si l'action a echoue
     */
    protected boolean onMapChange(Case oldCell, Case newCell) {
        return true;
    }

}
