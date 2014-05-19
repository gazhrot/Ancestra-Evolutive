package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.object.ObjectSet;




public class ItemSetData extends AbstractDAO<ObjectSet>{

	public ItemSetData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(ObjectSet obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(ObjectSet obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(ObjectSet obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ObjectSet load(int id) {
		ObjectSet set = null;
		try {
			ResultSet result = getData("SELECT * FROM itemsets WHERE id = "+id);
			
			if(result.next()) {
				set = new ObjectSet(result.getInt("id"), result
						.getString("items"), result.getString("bonus"));
				World.data.addItemSet(set);
			}
			
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(ItemSetData): "+e.getMessage());
		}
		return set;
	}
}
