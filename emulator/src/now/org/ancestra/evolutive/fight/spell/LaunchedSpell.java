package org.ancestra.evolutive.fight.spell;

import org.ancestra.evolutive.fight.Fighter;

public class LaunchedSpell {
	
	private Fighter target = null;
	private int spellId = 0;
	private int cooldown = 0;
	
	public LaunchedSpell(Fighter target, SpellStats SS) {
		this.target = target;
		this.spellId = SS.getSpellID();
		this.cooldown = SS.getCoolDown();
	}
	
	public Fighter getTarget() {
		return this.target;
	}
	
	public int getSpellId() {
		return this.spellId;
	}
	
	public int getCooldown() {
		return this.cooldown;
	}
	
	public void refreshCooldown() {
		this.cooldown--;
	}	
	
	public static boolean cooldownGood(Fighter fighter, int id) {
		for(LaunchedSpell S : fighter.getLaunchedSorts())
			if(S.spellId == id && S.getCooldown() > 0)
				return false;
		return true;
	}
	
	public static int getNbLaunch(Fighter fighter, int id) {
		int nb = 0;
		for(LaunchedSpell S : fighter.getLaunchedSorts())
			if(S.spellId == id)
				nb++;
		return nb;
	}
	
	public static int getNbLaunchTarget(Fighter fighter, Fighter target, int id) {
		int nb = 0;
		for(LaunchedSpell S : fighter.getLaunchedSorts())
		{
			if(S.target == null || target == null)
				continue;
			if(S.spellId == id && S.target.getGUID() == target.getGUID())
				nb++;
		}
		return nb;
	}
}