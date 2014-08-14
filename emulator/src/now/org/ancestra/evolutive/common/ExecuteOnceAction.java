package org.ancestra.evolutive.common;


public abstract class ExecuteOnceAction implements Action {
 private int ticCount;
 
	public ExecuteOnceAction(int timeDelay){
		ticCount = GlobalThread.fromDelayToTic(timeDelay);
    }
	
	public boolean actionNeeded(){
		if(ticCount==0){
			GlobalThread.unregisterAction(this);
			return true;
		}
		else {
            ticCount--;
			return false;
		}
	}
	
	public abstract void applyAction();
	
	
}