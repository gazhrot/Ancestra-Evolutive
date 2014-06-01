package org.ancestra.evolutive.tool.time.timer;


import org.ancestra.evolutive.core.World;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public abstract class ScheduledHandler implements Runnable {
    //world scheduler
    ScheduledExecutorService scheduler = World.data.getScheduler();
    //future task
    private ScheduledFuture<?> scheduled;

    //simple schedule
    public ScheduledHandler(int time, TimeUnit unit) {
        this.scheduled = scheduler.schedule(this, time, unit);
    }

    //schedule with fixed delay
    public ScheduledHandler(long first, long second, TimeUnit unit) {
        this.scheduled = scheduler.scheduleWithFixedDelay(this, first, second, unit);
    }

    public void cancel(boolean b) {
        this.scheduled.cancel(b);
    }

    @Override
	public abstract void run();
}