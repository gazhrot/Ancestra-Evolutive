package objects.job;

import java.util.concurrent.TimeUnit;

import tool.time.timer.ScheduledHandler;

import common.SocketManager;

import client.Player;

public class JobCraft {

	public Player perso1;
	private Player perso2;
	public JobAction jobAction;
	private int time = 0;
	private boolean itsOk = true;	
	
	public JobCraft(JobAction JA, Player perso)	{
		this.jobAction = JA;
		this.perso1 = perso;
		new ScheduledHandler(1, TimeUnit.SECONDS) {
            public void run() {
    			if(itsOk)
    				jobAction.craft();
    			if(!itsOk) {
    				jobAction.player = perso2;
    				jobAction.isRepeat = true;
    				jobAction.lastCraft.clear();
    				jobAction.lastCraft.putAll(jobAction.ingredients);
    				for(int i = time; i >= 0; i--)
    		    	{
    					jobAction.ingredients.clear();
    					if (jobAction.broke || jobAction.broken || jobAction.player.getCurJobAction() == null || !jobAction.player.isOnline())
    					{
    						if(jobAction.player.getCurJobAction() == null)
    							jobAction.broken = true;
    						if(jobAction.player.isOnline())
    							SocketManager.GAME_SEND_Ea_PACKET(jobAction.player, jobAction.broken ? "2" : "4");
    						cancel(true);
    						break;
    					}
    					SocketManager.GAME_SEND_EA_PACKET(jobAction.player, i + "");
    					jobAction.ingredients.putAll(jobAction.lastCraft);
    					jobAction.craft();
    					try { Thread.sleep(1000); }catch(Exception e) {}
    				}
    				SocketManager.GAME_SEND_Ea_PACKET(jobAction.player, "1");
    				if(!jobAction.data.isEmpty())
    					SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(jobAction.player, 'O', "+", jobAction.data);
    				jobAction.isRepeat = false;
    				cancel(true);
    			}
            }
        };
	}
	
	public void setAction(int time, Player perso2) {
		this.time = time;
		this.perso2 = perso2;
		this.itsOk = false;
	}
}