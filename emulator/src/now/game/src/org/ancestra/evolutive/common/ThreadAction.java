package org.ancestra.evolutive.common;

public interface ThreadAction extends Action {
	public void applyAction();
    public boolean actionNeeded();
}