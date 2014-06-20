package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.Collector;
import org.ancestra.evolutive.map.Maps;




public class CollectorData extends AbstractDAO<Collector>{

	public CollectorData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Collector obj) {
		String baseQuery = "INSERT INTO `percepteurs`" +
				" VALUES (?,?,?,?,?,?,?,?,?,?);";
		try {
			PreparedStatement statement = connection.prepareStatement(baseQuery);
			statement.setInt(1, obj.getGuid());
			statement.setInt(2, obj.get_mapID());
			statement.setInt(3, obj.get_cellID());
			statement.setInt(4, obj.getOrientation());
			statement.setInt(5, obj.get_guildID());
			statement.setInt(6, obj.get_N1());
			statement.setInt(7, obj.get_N2());
			statement.setString(8, "");
			statement.setLong(9, 0);
			statement.setLong(10, 0);
			
			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(CollectorData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public boolean delete(Collector obj) {
		String baseQuery = "DELETE FROM percepteurs WHERE guid = "+obj.getGuid();
		execute(baseQuery);
		return true;
	}

	@Override
	public boolean update(Collector obj) {
		String baseQuery = "UPDATE `percepteurs` SET " + "`objets` = ?,"
				+ "`kamas` = ?," + "`xp` = ?" + " WHERE guid = ?;";

		try {
			PreparedStatement statement = connection.prepareStatement(baseQuery);
			statement.setString(1, obj.parseItemPercepteur());
			statement.setLong(2, obj.getKamas());
			statement.setLong(3, obj.getXp());
			statement.setInt(4, obj.getGuid());
			
			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(CollectorData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public Collector load(int id) {
		Collector collector = null;
		try {
			ResultSet result = getData("SELECT * FROM percepteurs WHERE guid = "+id);
			
			if(result.next()) {
				Maps map = World.data.getCarte(result.getShort("mapid"));
				if(map == null) return null;
				
				collector = new Collector(
								result.getInt("guid"),
								result.getShort("mapid"),
								result.getInt("cellid"),
								result.getByte("orientation"),
								result.getInt("guild_id"),
								result.getShort("N1"),
								result.getShort("N2"),
								result.getString("objets"),
								result.getLong("kamas"),
								result.getLong("xp"));
				World.data.addPerco(collector);
			}
			closeResultSet(result);
		} catch(Exception e) {
			Console.instance.writeln("SQL ERROR(CollectorData): "+e.getMessage());
		}
		return collector;
	}
	
	public Collector loadByMap(int id) {
		Collector collector = null;
		try {
			ResultSet result = getData("SELECT * FROM percepteurs WHERE mapid = "+id);
			
			while(result.next()) {
				collector = new Collector(
								result.getInt("guid"),
								result.getShort("mapid"),
								result.getInt("cellid"),
								result.getByte("orientation"),
								result.getInt("guild_id"),
								result.getShort("N1"),
								result.getShort("N2"),
								result.getString("objets"),
								result.getLong("kamas"),
								result.getLong("xp"));
				World.data.addPerco(collector);
			}
			closeResultSet(result);
		} catch(Exception e) {
			Console.instance.writeln("SQL ERROR(CollectorData): "+e.getMessage());
		}
		return collector;
	}
	
	public int nextId() {
		int guid = -1;
		
		try {
			String query = "SELECT MAX(guid) AS max FROM percepteurs;";
			ResultSet result = getData(query);
			
			while(result.next())
				guid = result.getInt("max")+1;
			
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(CollectorData): "+e.getMessage());
		}
		return guid;
	}
}
