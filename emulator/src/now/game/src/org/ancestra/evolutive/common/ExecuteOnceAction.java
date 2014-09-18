package org.ancestra.evolutive.common;


public abstract class ExecuteOnceAction implements ThreadAction {
    private int ticCount;
 
	public ExecuteOnceAction(int timeDelay){
		ticCount = GlobalThread.fromDelayToTic(timeDelay);
    }

    @Override
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

    @Override
	public abstract void applyAction();
}