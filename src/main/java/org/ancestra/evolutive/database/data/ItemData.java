package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.object.Objet;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;




public class ItemData extends AbstractDAO<Objet>{

	public ItemData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.Item");
	}

	@Override
	public boolean create(Objet obj) {
		try {
			String baseQuery = "INSERT INTO `items` VALUES(?,?,?,?,?);";

			PreparedStatement statement = getPreparedStatement(baseQuery);

			statement.setInt(1, obj.getGuid());
			statement.setInt(2, obj.getTemplate().getID());
			statement.setInt(3, obj.getQuantity());
			statement.setInt(4, obj.getPosition());
			statement.setString(5, obj.parseToSave());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(ItemData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public boolean delete(Objet obj) {
		String baseQuery = "DELETE FROM items WHERE guid = "+obj.getGuid();
		execute(baseQuery);
		return true;
	}

	@Override
	public boolean update(Objet obj) {
		try {
			String baseQuery = "REPLACE INTO `items` VALUES(?,?,?,?,?);";

			PreparedStatement statement = getPreparedStatement(baseQuery);

			statement.setInt(1, obj.getGuid());
			statement.setInt(2, obj.getTemplate().getID());
			statement.setInt(3, obj.getQuantity());
			statement.setInt(4, obj.getPosition());
			statement.setString(5, obj.parseToSave());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(ItemData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public Objet load(int id) {
		Objet item = null;
		try {
			Result result = getData("SELECT * FROM items WHERE guid = "+id);
			ResultSet RS = result.resultSet;
			if(RS.next()) {
				int guid = RS.getInt("guid");
				int tempID = RS.getInt("template");
				int qua = RS.getInt("qua");
				int pos = RS.getInt("pos");
				String stats = RS.getString("stats");
				item = new Objet(guid, tempID, qua, pos, stats);
				
				World.data.addObjet(item, false);
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(ItemData): "+e.getMessage());
		}
		return item;
	}
	
	public void load(String items) {
		try {
			String req = "SELECT * FROM items WHERE guid IN (" + items + ")";
			Result result = getData(req);
			
			while (result.resultSet.next()) {
				int guid = result.resultSet.getInt("guid");
				int tempID = result.resultSet.getInt("template");
				int qua = result.resultSet.getInt("qua");
				int pos = result.resultSet.getInt("pos");
				
				String stats = result.resultSet.getString("stats");
				
				World.data.addObjet(World.data.newObjet(guid, tempID, qua, pos, stats),false);
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(ItemData): "+e.getMessage());
		}
	}
	
	public int nextId() {
		int guid = -1;
		
		try {
			String query = "SELECT MAX(guid) AS max FROM items;";
			Result result = getData(query);
			
			while(result.resultSet.next())
				guid = result.resultSet.getInt("max")+1;
			
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(ItemData): "+e.getMessage());
		}
		return guid;
	}
}
