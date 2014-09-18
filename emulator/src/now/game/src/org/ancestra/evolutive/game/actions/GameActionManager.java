package org.ancestra.evolutive.game.actions;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Action;
import org.ancestra.evolutive.game.GameAction;

import java.util.ArrayList;

/**
 * Created by Guillaume on 22/08/2014.
 * Hope you'll like it!
 */
public class GameActionManager {
    public enum Status {
        WAITING,
        MOVING,
        ATTACKING,
        DEFYING,
        ANIMATION,
        EXCHANGING,
        AWAY,
        GHOST,
        CRAFTING,
        DIALOG
    }
    private final ArrayList<GameActions> currentActions = new ArrayList<>();
    private final Player player;
    private final Logger logger;
    private Status status;
    private boolean isAway;

    /**
     * Permet de realiser et d enregistrer les game
     * @param player joueurs qui devra les effectuer
     */
    public GameActionManager(Player player){
        this.player = player;
        this.logger = player.getLogger();
        this.status = Status.WAITING;
        this.isAway = false;
    }

    /**
     * Retourne le status actuel
     * @return status actuel
     */
    public Status getStatus() {
        if(!isAway)
            return status;
        else
            return status != Status.WAITING?status:Status.AWAY;

    }

    /**
     * Permet de modifier le status
     * @param status
     */
    public void setStatus(Status status) {
        this.status = status;
        logger.debug("Changement du status pour {}",status.name());
    }

    /**
     * Permet de modifier l etat du joueur.
     * Le status en sera affecte
     */
    public void setAway(boolean away){
        this.isAway = away;
    }

    /**
     * Permet d'obtenir une action par son id d action
     * Si il y en a plusieurs seule la première sera retournee
     * @param actionId
     */
    public GameActions getActionByActionId(int actionId){
        for(GameActions ga : currentActions){
            if(ga.getActionId() == actionId)
                return ga;
        }
        return null;
    }

    /**
     * Creer une gameaction sans argument
     * @param actionId id du game action
     */
    public void createAction(int actionId){
        createAction(actionId,"");
    }

    /**
     * Creer une gameAction avec argument
     * @param actionId id du game action
     * @param args argument
     */
    public synchronized void createAction(int actionId,String args){
        GameActions gameAction;
        int id = nextActionId();
        switch (actionId){
            case 1 :
                gameAction = new Deplacement(id,player,args);
                break;
            case 300 :
                gameAction = new SpellAttack(id,player,args);
                break;
            case 303 :
                gameAction = new WeaponAttack(id,player,args);
                break;
            case 500 :
                gameAction = new MapAction(id,player,args);
                break;
            case 507 :
                gameAction = new HouseAction(id,player,args);
                break;
            case 618 :
                gameAction = new MariageAccept(id,player,args);
                break;
            case 619 :
                gameAction = new MariageRefuse(id,player,args);
                break;
            case 900 :
                gameAction = new DuelAsk(id,player,args);
                break;
            case 901 :
                gameAction = new DuelAccept(id,player,args);
                break;
            case 902 :
                gameAction = new DuelRefuse(id,player,args);
                break;
            case 903 :
                gameAction = new FightJoin(id,player,args);
                break;
            case 906 :
                gameAction = new FightCollector(id,player,args);
                break;
            case 909 :
                gameAction = new FightAggression(id,player,args);
                break;
            default:
                logger.error("L action id {} a ete demandee mais n existe pas", actionId);
                return;
        }
        addAction(gameAction);
    }

    /**
     * Ajoute une fonction et l execute si elle est la premiere dans la liste
     * @param gameAction action a ajouter
     */
    public void addAction(GameActions gameAction){
        currentActions.add(gameAction);
        if(currentActions.size() == 1 )
            startAction(gameAction);
    }

    /**
     * Debute l action et regarde si elle doit etre conservee ou non
     * @param gameActions action a debuter
     */
    protected void startAction(GameActions gameActions) {
        if (!gameActions.start())
            currentActions.remove(gameActions);
    }

    /**
     * termine une action sans argument
     * @param actionId id de l action
     * @param success si l action a reussi ou non
     */
    public void endAction(int actionId,boolean success){
        endAction(actionId,success,"");
    }

    /**
     * termine une action avec argument
     * @param actionId id de l action
     * @param success si l action a reussi ou non
     * @param args si il y a un argument de fin
     */
    public void endAction(int actionId,boolean success,String args){
        GameActions gameAction = currentActions.get(actionId);
        if(gameAction != null) {
            if (success) {
                gameAction.onSuccess(args);
                if (currentActions.size() > currentActions.indexOf(gameAction) + 1) {
                    startAction(currentActions.get(currentActions.indexOf(gameAction) + 1));
                }
                if(currentActions.contains(gameAction))currentActions.remove(gameAction);
            } else {
                gameAction.onFail(args);
                resetActions();
            }
        }
    }

    /**
     * Arrete toutes les actions en cours et change le status a WAITING
     */
    public void resetActions(){
        for (GameActions actions : currentActions){
            actions.cancel();
        }
        currentActions.clear();
        setStatus(Status.WAITING);
    }

    /**
     * Permet d obtenir un id d action libre
     * @return
     */
    public int nextActionId(){
        int min=0;
        for(GameActions action : currentActions){
            if(action.getId()>min)
                min = action.getId()+1;
        }
        return min;
    }
}
