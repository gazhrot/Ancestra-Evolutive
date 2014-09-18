package org.ancestra.evolutive.fight.trap;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Pathfinding;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.entity.creature.monster.Mob;
import org.ancestra.evolutive.enums.IdType;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.map.Case;

import java.util.ArrayList;

public class Piege
{
	private Fighter _caster;
	private Case _cell;
	private byte _size;
	private int _spell;
	private SpellStats _trapSpell;
	private Fight _fight;
	private int _color;
	private boolean _isunHide = true;
	private int _teamUnHide = -1;
	
	public Piege(Fight fight, Fighter caster, Case cell, byte size, SpellStats trapSpell, int spell)
	{
		_fight = fight;
		_caster = caster;
		_cell =cell;
		_spell = spell;
		_size = size;
		_trapSpell = trapSpell;
		_color = Constants.getTrapsColor(spell);
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
	
	public void set_isunHide(Fighter f)
	{
		_isunHide = true;
		_teamUnHide = f.getTeam().getId();
	}
	
	public boolean get_isunHide()
	{
		return _isunHide;
	}
	
	public void desappear()
	{
		StringBuilder str = new StringBuilder();
		StringBuilder str2 = new StringBuilder();
		StringBuilder str3 = new StringBuilder();
		StringBuilder str4 = new StringBuilder();
		
		int team = _caster.getTeam().getId()+1;
		str.append("GDZ-").append(_cell.getId()).append(";").append(_size).append(";").append(_color);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, team, 999, _caster.getId()+"", str.toString());
		str2.append("GDC"+_cell.getId());
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, team, 999, _caster.getId()+"", str2.toString());
		if(get_isunHide())
		{
			int team2 = _teamUnHide+1;
			str3.append("GDZ-").append(_cell.getId()).append(";").append(_size).append(";").append(_color);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, team2, 999, _caster.getId()+"", str3.toString());
			str4.append("GDC").append(_cell.getId());
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, team2, 999, _caster.getId()+"", str4.toString());
		}
	}
	
	public void appear(Fighter f)
	{
		StringBuilder str = new StringBuilder();
		StringBuilder str2 = new StringBuilder();
		
		int team = f.getTeam().getId()+1;
		str.append("GDZ+").append(_cell.getId()).append(";").append(_size).append(";").append(_color);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, team, 999, _caster.getId()+"", str.toString());
		str2.append("GDC").append(_cell.getId()).append(";Haaaaaaaaz3005;");
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, team, 999, _caster.getId()+"", str2.toString());
	}
	
	public void onTraped(Fighter target)
	{
		if(target.isDead())return;
		_fight.getTraps().remove(this);
		//On efface le pieges
		desappear();
		//On d�clenche ses effets
		String str = _spell+","+_cell.getId()+",0,1,1,"+_caster.getId();
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(_fight, 7, 307, target.getId()+"", str);
		
		ArrayList<Case> cells = new ArrayList<>();
		cells.add(_cell);
		//on ajoute les cases
		for(int a = 0; a < _size;a++)
		{
			char[] dirs = {'b','d','f','h'};
			ArrayList<Case> cases2 = new ArrayList<Case>();//on �vite les modifications concurrentes
			cases2.addAll(cells);
			for(Case aCell : cases2)
			{
				for(char d : dirs)
				{
					Case cell = _fight.getMap().getCases().get(Pathfinding.GetCaseIDFromDirection(aCell.getId(), d, _fight.getMap(), true));
					if(cell == null)continue;
					if(!cells.contains(cell))
					{
						cells.add(cell);
					}
				}
			}
		}
		Fighter fakeCaster;
		if(_caster.getPersonnage() == null){
            Mob mob = new Mob(_fight.getMap().getNextFreeId(IdType.CREATURE),_caster.getMap().getCases().get(_cell),_caster.getMob().getGrade());
            fakeCaster = new Fighter(_fight,mob,_caster.getTeam());

        }
		else 	fakeCaster = new Fighter(_fight,_caster.getPersonnage(),_caster.getTeam());

		fakeCaster.setFightCell(_cell);
		_trapSpell.applySpellEffectToFight(_fight,fakeCaster,target.get_fightCell(false),cells,false);
		_fight.verifIfFightEnded();
	}
	
	public int get_color()
	{
		return _color;
	}
}