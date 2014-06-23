package org.ancestra.evolutive.util.exchange;

import java.util.ArrayList;

import org.ancestra.evolutive.common.Couple;
import org.ancestra.evolutive.entity.Creature;

public abstract class Exchange {
	
	public Exchanger exchanger1;
	public Exchanger exchanger2;
	
	public Exchange(Creature creature1, Creature creature2) {
		this.exchanger1 = new Exchanger(creature1);
		this.exchanger2 = new Exchanger(creature2);
	}
	
	public synchronized void cancel() {
		doCancel();
	}
	
	protected abstract void doCancel();
	
	public synchronized void apply() {
		doApply();
	}
	
	protected abstract void doApply();
		
	public synchronized void toogleOk(int id) {
		doToogleOk(id);
	}
	
	protected abstract void doToogleOk(int id);
	
	public synchronized void addObject(int idObject, int quantity, int idPlayer) {
		doAddObject(idObject, quantity, idPlayer);
	}
	
	protected abstract void doAddObject(int idObject, int quantity, int idPlayer);
	
	public synchronized void removeObject(int idObject, int quantity, int idPlayer) {
		doRemoveObject(idObject, quantity, idPlayer);
	}
	
	protected abstract void doRemoveObject(int idObject, int quantity, int idPlayer);
	
	public synchronized void editKamas(int idPlayer, long kamas) {
		doEditKamas(idPlayer, kamas);
	}
	
	protected abstract void doEditKamas(int idPlayer, long kamas);
	
	
	
	public synchronized Couple<Integer, Integer> getCoupleInList(ArrayList<Couple<Integer, Integer>> objects, int guid) {
		for(Couple<Integer, Integer> couple : objects)
			if(couple.first == guid)
				return couple;
		return null;
	}
	
	public synchronized int getObjectQuantity(Creature creature, int idObject) {
		ArrayList<Couple<Integer, Integer>> objects;
		
		if(exchanger1.getCreature() == creature)
			objects = exchanger1.getObjects();
		else
			objects = exchanger2.getObjects();
		
		for(Couple<Integer, Integer> couple : objects)
			if(couple.first == idObject)
				return couple.second;		
		return 0;
	}
	
	public static class Exchanger {
		
		private Creature creature;
		private long kamas = 0;
		private boolean ok;
		private ArrayList<Couple<Integer, Integer>> objects = new ArrayList<>();
		
		public Exchanger(Creature creature) {
			this.creature = creature;
		}
		
		public Creature getCreature() {
			return creature;
		}
		
		public synchronized long getKamas() {
			return kamas;
		}
		
		public synchronized void setKamas(long kamas) {
			this.kamas = kamas;
		}
		
		public synchronized boolean isOk() {
			return ok;
		}
		
		public synchronized void setOk(boolean ok) {
			this.ok = ok;
			
		}
		
		public synchronized ArrayList<Couple<Integer, Integer>> getObjects() {
			return objects;
		}
	}
}