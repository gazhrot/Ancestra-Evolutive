package org.ancestra.evolutive.fight.trap;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.map.Case;

public class Glyphe
{
	private Fighter _caster;
	private Case _cell;
	private byte _size;
	private int _spell;
	private SpellStats _trapSpell;
	private byte _duration;
	private Fight _fight;
	private int _color;
	
	public Glyphe(Fight fight, Fighter caster, Case cell, byte size, SpellStats trapSpell, byte duration, int spell)
	{
		_fight = fight;
		_caster = caster;
		_cell =cell;
		setSpell(spell);
		_size = size;
		_trapSpell = trapSpell;
		_duration = duration;
		_color = Constants.getGlyphColor(spell);
	}

	public Case get_cell() {
		return _cell;
	}

	public byte get_size() {
		return _size;
	}

	public Fighter get_caster() {
		return _caster;
	}
	
	public byte get_duration() {
		return _duration;
	}

	public int decrementDuration()
	{
		_duration--;
		return _duration;
	}
	
	public void onTraped(Fighter target)
	{
		String str = getSpell()+","+_cell.getId()+",0,1,1,"+_caster.getId();
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, 7, 307, target.getId()+"", str);
		_trapSpell.applySpellEffectToFight(_fight,_caster,target.get_fightCell(false),false);
		_fight.verifIfFightEnded();
	}

	public void desapear()
	{
		SocketManager.GAME_SEND_GDZ_PACKET_TO_FIGHT(_fight, 7, "-",_cell.getId(), _size, _color);
		SocketManager.GAME_SEND_GDC_PACKET_TO_FIGHT(_fight, 7, _cell.getId());
	}
	
	public int get_color()
	{
		return _color;
	}

	public int getSpell() {
		return _spell;
	}

	public void setSpell(int _spell) {
		this._spell = _spell;
	}
}