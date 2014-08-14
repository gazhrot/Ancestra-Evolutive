package org.ancestra.evolutive.common;

public abstract class ScheduledAction implements Action {
	protected final int initialTic;
	protected int ticCount;
	
	public ScheduledAction(int timeDelay) {
        initialTic = ticCount = GlobalThread.fromDelayToTic(timeDelay);
    }

	public boolean actionNeeded(){
		if(ticCount == 0){
			ticCount = initialTic;
			return true;
		}
		else {
			ticCount--;
			return false;
		}
	}
	
	public abstract void applyAction();
	
	
	
}