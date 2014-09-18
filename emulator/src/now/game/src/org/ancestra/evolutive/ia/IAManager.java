package org.ancestra.evolutive.ia;

import org.ancestra.evolutive.common.ExecuteOnceAction;
import org.ancestra.evolutive.common.GlobalThread;
import org.ancestra.evolutive.core.Server;

import java.util.ArrayList;

/**
 * Created by Guillaume on 29/08/2014.
 * Hope you'll like it!
 */
public class IAManager {
    private static final ArrayList<IARunnable> threads = new ArrayList<>();
    static {
        if ( Server.config.getIaThreadQuantity() < 1){
            System.err.println("Impossible de creer un serveur sans thread pour l ia");
            System.exit(0);
        }
        for(int i = 0;i <= Server.config.getIaThreadQuantity() ; i++){
            IARunnable iaRunnable = new IARunnable(i);
            Thread thread = new Thread(iaRunnable);
            thread.start();
            threads.add(iaRunnable);
        }
    }

    /**
     * Ajoute un ia a executer dans un temps donner
     * @param ia ia a executer
     * @param delay temps en milliseconde
     */
    public static void addIA(final NewIA ia,int delay){
        GlobalThread.registerAction(new ExecuteOnceAction(delay) {
            @Override
            public void applyAction() {
                addIA(ia);
            }
        });
    }

    /**
     * Execute l ia demander
     * @param ia ia a executer
     */
    public static void addIA(NewIA ia){
        int min = 0;
        IARunnable minThread = null;
        for(IARunnable thread : threads){
            if(thread.getQueueSize() > min)continue;
            min = thread.getQueueSize();
            minThread = thread;
        }
        minThread.addIA(ia);
    }
}
