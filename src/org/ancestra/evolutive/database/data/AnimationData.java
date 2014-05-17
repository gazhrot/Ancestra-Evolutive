package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.fight.spell.Animation;




public class AnimationData extends AbstractDAO<Animation> {

	public AnimationData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Animation obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Animation obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Animation obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Animation load(int id) {
		Animation animation = null;
		try {
			ResultSet result = getData("SELECT * FROM animations WHERE guid = "+id);
			
			if(result.next()) {
				animation = new Animation(
						result.getInt("guid"),
						result.getInt("id"),
						result.getString("nom"),
						result.getInt("area"),
						result.getInt("action"),
						result.getInt("size"));
				World.data.addAnimation(animation);
			}
			closeResultSet(result);
		} catch(Exception e) {
			Console.instance.writeln("SQL ERROR(AnimationData): "+e.getMessage());
		}
		return animation;
	}
}
