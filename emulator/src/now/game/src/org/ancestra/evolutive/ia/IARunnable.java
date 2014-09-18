package org.ancestra.evolutive.ia;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * Created by Guillaume on 29/08/2014.
 * Hope you'll like it!
 */
class IARunnable implements Runnable{
    private final Logger logger;
    private final static LinkedList<NewIA> actions = new LinkedList<>();
    private final int id;
    private final Object locker = new Object();

    private NewIA currentIA;

    IARunnable(int id){
        this.id = id;
        this.logger = (Logger) LoggerFactory.getLogger("IA" + id);
        logger.debug("Debut de l ia");
    }

    void addIA(NewIA ia){
        synchronized (locker){
            actions.add(ia);
            logger.debug("Ajout d une action");
            locker.notify();
        }
    }

    int getQueueSize(){
        return actions.size();
    }

    @Override
    public void run(){
        while (true) {
            try {
                synchronized (locker){
                    while((currentIA = actions.poll()) == null) {
                        locker.wait();
                    }
                    currentIA.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
