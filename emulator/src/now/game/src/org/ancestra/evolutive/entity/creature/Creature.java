package org.ancestra.evolutive.entity.creature;

import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Entity;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.Maps;

public class Creature extends Entity {

    protected Maps map;
    protected Case cell;
    protected Fight fight;
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
     * @param cell cellule de la creature
     */
    public Creature(int id, String name,Case cell) {
        this(id, name, cell.getMap(), cell, 0);
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
        onPositionChange(this.cell,cell);
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
    public void setPosition(Case cell) {
        this.onPositionChange(this.cell,cell);
    }

    /**
     * Retourne la fight actuelle
     * @return
     */
    public Fight getFight() {
        return fight;
    }

    /**
     * Fight actuelle
     * @param fight
     */
    public void setFight(Fight fight) {
        this.fight = fight;
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


    public void removeOnMap(){
        this.map.removeEntity(this);
        this.cell.removeCreature(this);
    }

    /**
     * Effectue le changement de position
     * @param oldCell ancienne case
     * @param newCell nouvelle case
     * @return retourne true si le changement a pu etre effectue
     * false sinon
     */
    protected boolean onPositionChange(Case oldCell, Case newCell) {
        if(newCell == null || newCell.getMap() == null || oldCell == newCell) {
            return false;
        }
        if(newCell.getMap() == oldCell.getMap()) {
            this.cell = newCell;
            return onMoveCell(oldCell,newCell);
        } else {
            oldCell.getMap().removeEntity(this);
            oldCell.removeCreature(this);
            this.cell = newCell;
            this.map = newCell.getMap();
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
