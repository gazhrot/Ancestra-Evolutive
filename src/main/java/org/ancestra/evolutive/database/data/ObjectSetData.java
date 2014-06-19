package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.object.ObjectSet;
import org.slf4j.LoggerFactory;

public class ObjectSetData extends AbstractDAO<ObjectSet>{

	public ObjectSetData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.ObjectSet");
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
			Result result = getData("SELECT * FROM itemsets WHERE id = "+id);
			
			if(result.resultSet.next()) {
				set = new ObjectSet(result.resultSet.getInt("id"), result.resultSet
						.getString("items"), result.resultSet.getString("bonus"));
				World.data.addItemSet(set);
			}
			
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(ObjectSetData): "+e.getMessage());
		}
		return set;
	}
}
