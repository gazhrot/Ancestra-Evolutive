package fr.edofus.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;



import fr.edofus.ancestra.evolutive.core.Console;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.database.AbstractDAO;
import fr.edofus.ancestra.evolutive.objects.Animations;

public class AnimationData extends AbstractDAO<Animations> {

	public AnimationData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Animations obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Animations obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Animations obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Animations load(int id) {
		Animations animation = null;
		try {
			ResultSet result = getData("SELECT * FROM animations WHERE guid = "+id);
			
			if(result.next()) {
				animation = new Animations(
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
