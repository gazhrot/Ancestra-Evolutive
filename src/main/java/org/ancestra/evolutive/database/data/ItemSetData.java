package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.object.ItemSet;
import org.slf4j.LoggerFactory;

public class ItemSetData extends AbstractDAO<ItemSet>{

	public ItemSetData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.ItemSet");
	}

	@Override
	public boolean create(ItemSet obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(ItemSet obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(ItemSet obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemSet load(int id) {
		ItemSet set = null;
		try {
			Result result = getData("SELECT * FROM itemsets WHERE id = "+id);
			
			if(result.resultSet.next()) {
				set = new ItemSet(result.resultSet.getInt("id"), result.resultSet
						.getString("items"), result.resultSet.getString("bonus"));
				World.data.addItemSet(set);
			}
			
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(ItemSetData): "+e.getMessage());
		}
		return set;
	}
}
