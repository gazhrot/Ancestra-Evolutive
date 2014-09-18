package org.ancestra.evolutive.game.actions;

/**
 * Created by Guillaume on 22/08/2014.
 * Hope you'll like it!
 */
public interface GameActions {
    /**
     * Initialise une action avec un argument
     * @return true si l action attend un GKK
     * false sinon
     */
    boolean start();

    void cancel();

    void onFail(String args);

    void onSuccess(String args);

    int getId();

    int getActionId();
}
