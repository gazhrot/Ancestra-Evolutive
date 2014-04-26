package fr.edofus.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;



import fr.edofus.ancestra.evolutive.core.Console;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.database.AbstractDAO;
import fr.edofus.ancestra.evolutive.objects.IOTemplate;

public class IOTemplateData extends AbstractDAO<IOTemplate>{

	public IOTemplateData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(IOTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(IOTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(IOTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IOTemplate load(int id) {
		IOTemplate template = null;
		try {
			ResultSet result = getData("SELECT * FROM interactive_objects_data WHERE id = "+id);
			
			if(result.next()) {
				template = new IOTemplate(result.getInt("id"), result
						.getInt("respawn"), result.getInt("duration"), result
						.getInt("unknow"), result.getInt("walkable") == 1);
				World.data.addIOTemplate(template);
			}
			
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(IOTemplateData): "+e.getMessage());
		}
		return template;
	}

}
