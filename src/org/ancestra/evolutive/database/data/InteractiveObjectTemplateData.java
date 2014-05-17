package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.map.InteractiveObject.InteractiveObjectTemplate;

public class InteractiveObjectTemplateData extends AbstractDAO<InteractiveObjectTemplate>{

	public InteractiveObjectTemplateData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(InteractiveObjectTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(InteractiveObjectTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(InteractiveObjectTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InteractiveObjectTemplate load(int id) {
		InteractiveObjectTemplate template = null;
		try {
			ResultSet result = getData("SELECT * FROM interactive_objects_data WHERE id = "+id);
			
			if(result.next()) {
				template = new InteractiveObjectTemplate(result.getInt("id"), result
						.getInt("respawn"), result.getInt("duration"), result
						.getInt("unknow"), result.getInt("walkable") == 1);
				World.data.addInteractiveObjectTemplate(template);
			}
			
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(InteractiveObjectTemplateData): "+e.getMessage());
		}
		return template;
	}

}
