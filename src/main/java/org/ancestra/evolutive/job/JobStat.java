package org.ancestra.evolutive.job;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameAction;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.InteractiveObject;

import java.util.ArrayList;

public class JobStat {
	
	private int id;
	private Job template;
	private int lvl;
	private long xp;
	private ArrayList<JobAction> posActions = new ArrayList<JobAction>();
	private boolean isCheap = false;
	private boolean freeOnFails = false;
	private boolean noRessource = false;
	private JobAction curAction;
	private int maxCase = 2;
	private int slotsPublic;
    private int position;
    private boolean isPossible;
	
	public JobStat(int id,Job tp,int lvl,long xp)
	{
		this.id = id;
		this.template = tp;
		this.lvl = lvl;
		this.xp = xp;
		this.posActions = JobConstant.getPosActionsToJob(tp.getId(),lvl);
	}
	
	public int getId() {
		return this.id;
	}	

	public Job getTemplate() {
		return this.template;
	}
	
	public int get_lvl() {
		return this.lvl;
	}
	
	public long getXp()	{
		return this.xp;
	}
	
	public boolean isCheap() {
		return this.isCheap;
	}

	public void setIsCheap(boolean isCheap) {
		this.isCheap = isCheap;
	}
	
	public boolean isFreeOnFails() {
		return this.freeOnFails;
	}

	public void setFreeOnFails(boolean freeOnFails) {
		this.freeOnFails = freeOnFails;
	}

	public boolean isNoRessource() {
		return this.noRessource;
	}

	public void setNoRessource(boolean noRessource) {
		this.noRessource = noRessource;
	}
	
	public int getMaxcase() {
		return this.maxCase;
	}

	public void setSlotsPublic(int slots) {
    	this.slotsPublic = slots;
    }

    public int getSlotsPublic() {
    	return this.slotsPublic;
    }
	
    public int getPosition() {
    	return this.position;
    }
    
	public void setIsPossibleToActiveBook(boolean isPossible) {
		this.isPossible = isPossible;
	}
	
	public boolean getIsPossibleToActiveBook() {
		return this.isPossible;
	}
	
	public void startAction(int id, Player P, InteractiveObject IO, GameAction GA, Case cell) {
		for(JobAction JA : this.posActions)	{
			if(JA.getId() == id) {
				this.curAction = JA;
				JA.startAction(P, IO, GA, cell, this);
				return;
			}
		}
	}
	
	public void endAction(int id, Player P, InteractiveObject IO, GameAction GA, Case cell)
	{
		if(this.curAction == null) {
			SocketManager.GAME_SEND_MESSAGE(P, "Erreur action en cours null.", Server.config.getMotdColor());
			return;
		}
		this.curAction.endAction(P,IO,GA,cell);
		addXp(P,this.curAction.getXpWin()*Server.config.getRateXpJob());
		this.curAction = null;
		ArrayList<JobStat> list = new ArrayList<JobStat>();
		list.add(this);
		SocketManager.GAME_SEND_JX_PACKET(P, list);
	}
	
	public void addXp(Player P,long xp)
	{
		if(this.lvl >99)
			return;
		int exLvl = this.lvl;
		this.xp += xp;
		
		//Si l'xp d�passe le pallier du niveau suivant
		while(this.xp >= World.data.getExpLevel(this.lvl+1).metier && this.lvl <100)
			levelUp(P,false);	
		//s'il y a eu Up
		if(this.lvl > exLvl && P.isOnline())
		{
			//on cr�er la listes des JobStats a envoyer (Seulement celle ci)
			ArrayList<JobStat> list = new ArrayList<JobStat>();
			list.add(this);
			//on envoie le packet
			SocketManager.GAME_SEND_JS_PACKET(P, list);
			SocketManager.GAME_SEND_JN_PACKET(P,this.template.getId(),this.lvl);
			SocketManager.GAME_SEND_STATS_PACKET(P);
			SocketManager.GAME_SEND_Ow_PACKET(P);
			SocketManager.GAME_SEND_JO_PACKET(P, list);
		}
	}
	
	public String getXpString(String s)
	{
		StringBuilder str = new StringBuilder();
		str.append(World.data.getExpLevel(this.lvl).metier).append(s);
		str.append(this.xp).append(s);
		str.append(World.data.getExpLevel((this.lvl<100?this.lvl+1:this.lvl)).metier);
		return str.toString();
	}
	
	public void levelUp(Player P,boolean send)
	{
		this.lvl++;
		this.posActions = JobConstant.getPosActionsToJob(this.template.getId(),this.lvl);
		
		if(send)
		{
			//on cr�er la listes des JobStats a envoyer (Seulement celle ci)
			ArrayList<JobStat> list = new ArrayList<JobStat>();
			list.add(this);
			SocketManager.GAME_SEND_JS_PACKET(P, list);
			SocketManager.GAME_SEND_STATS_PACKET(P);
			SocketManager.GAME_SEND_Ow_PACKET(P);
			SocketManager.GAME_SEND_JN_PACKET(P,this.template.getId(),this.lvl);
			SocketManager.GAME_SEND_JO_PACKET(P, list);
		}
	}
	
	public String parseJS()	{
		StringBuilder str = new StringBuilder();
		str.append("|").append(this.template.getId()).append(";");
		boolean first = true;
		for(JobAction JA : this.posActions)
		{
			if(!first)
				str.append(",");
			else
				first = false;
			str.append(JA.getId()).append("~").append(JA.getMin()).append("~");
			if(JA.isCraft())
				str.append("0~0~").append(JA.getChance());
			else 
				str.append(JA.getMax()).append("~0~").append(JA.getTime());
		}
		return str.toString();
	}
	
	public int getOptBinValue()
    {
    	int nbr = 0;
    	nbr += (this.isCheap ? 1 : 0);
    	nbr += (this.freeOnFails ? 2 : 0);
    	nbr += (this.noRessource ? 4 : 0);
    	return nbr;
    }
	
	public void setOptBinValue(int bin) 
    {
    	this.isCheap = false;
    	this.freeOnFails = false;
    	this.noRessource = false;
    	this.noRessource = (bin & 4) == 4;
		this.freeOnFails = (bin & 2) == 2;
		this.isCheap = (bin & 1) == 1;
	}
	
	public boolean isValidMapAction(int id)
	{
		for(JobAction JA : this.posActions)
			if(JA.getId() == id) 
				return true;
		return false;
	}
	
	public void set_o(String[] pp)
	{
		System.out.println("pp2 :" + pp[1]);
		System.out.println("pp3 :" + pp[2]);
		setOptBinValue(Integer.parseInt(pp[1]));
		int cm = JobConstant.getTotalCaseByJobLevel(this.lvl);
		if(cm <= Integer.parseInt(pp[2]))
			this.maxCase = cm;
		this.maxCase = Integer.parseInt(pp[2]);
	}
}
