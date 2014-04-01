package objects.job;

import java.util.concurrent.TimeUnit;

import tool.time.timer.ScheduledHandler;

import common.SocketManager;

import client.Player;

public class JobCraft {

	public ScheduledHandler task;
	public Player perso1;
	private Player perso2;
	public JobAction jobAction;
	private int time = 0;
	private boolean itsOk = true;	
	
	public JobCraft(JobAction JA, Player perso)
	{
		this.jobAction = JA;
		this.perso1 = perso;
		this.task = new ScheduledHandler(1, TimeUnit.SECONDS) {
            public void run() {
    			if(itsOk)
    				jobAction.craft();
    			if(!itsOk)
    				repeat(time, perso2); 
            }
        };
	}
	
	public void setAction(int time, Player perso2)
	{
		this.time = time;
		this.perso2 = perso2;
		this.itsOk = false;
	}
	
	public void repeat(int time, Player P)
	{
		this.jobAction.player = P;
		this.jobAction.isRepeat = true;
		this.jobAction.lastCraft.clear();
		this.jobAction.lastCraft.putAll(this.jobAction.ingredients);
		for(int i = time; i >= 0; i--)
    	{
			this.jobAction.ingredients.clear();
			if (this.jobAction.broke || this.jobAction.broken || P.getCurJobAction() == null || !P.isOnline())
			{
				if(P.getCurJobAction() == null)
					this.jobAction.broken = true;
				if(P.isOnline())
					SocketManager.GAME_SEND_Ea_PACKET(this.jobAction.player, this.jobAction.broken ? "2" : "4");
				this.task.cancel(true);
				break;
			}
			SocketManager.GAME_SEND_EA_PACKET(this.jobAction.player, i + "");
			this.jobAction.ingredients.putAll(this.jobAction.lastCraft);
			this.jobAction.craft();
			try { Thread.sleep(1000); }catch(Exception e) {}
		}
		SocketManager.GAME_SEND_Ea_PACKET(this.jobAction.player, "1");
		if(!this.jobAction.data.isEmpty())
			SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.jobAction.player, 'O', "+", this.jobAction.data);
		this.jobAction.isRepeat = false;
		this.task.cancel(true);
	}
}