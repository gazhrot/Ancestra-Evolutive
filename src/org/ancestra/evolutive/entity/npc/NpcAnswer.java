package org.ancestra.evolutive.entity.npc;

import java.util.ArrayList;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.other.Action;

public class NpcAnswer
{
	private int id;
	private ArrayList<Action> actions = new ArrayList<Action>();
	
	public NpcAnswer(int id) {
		this.id = id;
	}
		
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Action> getActions() {
		return actions;
	}

	public void setActions(ArrayList<Action> actions) {
		this.actions = actions;
	}

	public void addAction(Action action1) {
		ArrayList<Action> c = new ArrayList<Action>();
		c.addAll(this.getActions());
		
		for(Action action2: c)
			if(action2.getId() == action1.getId())
				this.getActions().remove(action2);
		
		this.getActions().add(action1);
	}
	
	public void apply(Player player) {
		for(Action action: this.getActions())
			action.apply(player, null, -1, -1);
	}
	
	public boolean isAnotherDialog() {
		for(Action curAct : this.getActions())
			if(curAct.getId() == 1) //1 = Discours NPC
				return true;		
		return false;
	}
}