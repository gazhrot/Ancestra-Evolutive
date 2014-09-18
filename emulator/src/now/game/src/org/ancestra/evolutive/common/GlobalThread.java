package org.ancestra.evolutive.common;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

public class GlobalThread implements Runnable {
	private final static Logger logger = (Logger) LoggerFactory.getLogger("globalThread");
	private final static CopyOnWriteArrayList<ThreadAction> actions = new CopyOnWriteArrayList<>();
    public final static int TIC_DELAY = 50;

	static{
		new Thread(new GlobalThread())
            .start();
	}
	
	private GlobalThread(){}
	
	public static int fromDelayToTic(int delay){
		return ((int)delay/TIC_DELAY)+1;//Le +1 permet d etre sur de ne pas declencher en avance
	}
	
	public static void registerAction(ThreadAction action){
		actions.add(action);
        logger.debug("Ajout d une action");
	}
	
	public static void unregisterAction(ThreadAction action){
		if(actions.contains(action)){
			actions.remove(action);
		}
	}
	
	@Override
	public void run(){
        logger.info("Le thread vient d etre lance");
        while(true) {
            for (ThreadAction action : actions) {
                try {
                    if (action.actionNeeded()) {
                        action.applyAction();
                    }
                } catch (Exception r) {
                    logger.error("Can t execute action", r);
                }
            }
            try {
                Thread.sleep(TIC_DELAY);
            } catch (Exception ignored) {
            }
        }
	}
}