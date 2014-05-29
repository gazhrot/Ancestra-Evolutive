package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.map.InteractiveObject.InteractiveObjectTemplate;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class InteractiveObjectTemplateData extends AbstractDAO<InteractiveObjectTemplate>{

	public InteractiveObjectTemplateData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("Interactive Object factory");
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
			Result result = getData("SELECT * FROM interactive_objects_data WHERE id = "+id);
			
			if(result.resultSet.next()) {
				template = new InteractiveObjectTemplate(result.resultSet.getInt("id"), result.resultSet
						.getInt("respawn"), result.resultSet.getInt("duration"), result.resultSet
						.getInt("unknow"), result.resultSet.getInt("walkable") == 1);
				World.data.addInteractiveObjectTemplate(template);
			}
			
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(InteractiveObjectTemplateData): "+e.getMessage());
		}
		return template;
	}

}
