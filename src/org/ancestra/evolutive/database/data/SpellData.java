package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.fight.spell.Spell;
import org.ancestra.evolutive.fight.spell.SpellStats;

public class SpellData extends AbstractDAO<Spell>{

	public SpellData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Spell obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Spell obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Spell obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Spell load(int id) {
		Spell sort = null;
		try {
			String query = "SELECT * FROM sorts WHERE id = "+id; //TODO
			ResultSet result = getData(query);
			if(result.next()) {
				sort = new Spell(id, result.getInt("sprite"),
						result.getString("spriteInfos"),
						result.getString("effectTarget"));
				SpellStats l1 = sort.parseSpellStats(id, 1, result.getString("lvl1"));
				SpellStats l2 = sort.parseSpellStats(id, 2, result.getString("lvl2"));
				SpellStats l3 = sort.parseSpellStats(id, 3, result.getString("lvl3"));
				SpellStats l4 = sort.parseSpellStats(id, 4, result.getString("lvl4"));
				SpellStats l5 = null;
				if (!result.getString("lvl5").equalsIgnoreCase("-1"))
					l5 = sort.parseSpellStats(id, 5, result.getString("lvl5"));
				SpellStats l6 = null;
				if (!result.getString("lvl6").equalsIgnoreCase("-1"))
					l6 = sort.parseSpellStats(id, 6, result.getString("lvl6"));
				sort.addSpellStats(1, l1);
				sort.addSpellStats(2, l2);
				sort.addSpellStats(3, l3);
				sort.addSpellStats(4, l4);
				sort.addSpellStats(5, l5);
				sort.addSpellStats(6, l6);
				World.data.addSort(sort);
			}
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(SpellData): "+e.getMessage());
		}
		return sort;
	}
}
