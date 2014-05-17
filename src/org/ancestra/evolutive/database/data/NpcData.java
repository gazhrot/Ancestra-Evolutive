package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.npc.Npc;
import org.ancestra.evolutive.map.Maps;

public class NpcData extends AbstractDAO<Npc>{

	public NpcData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Npc obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean create(int mapid, int npcid, int cellid, int orientation) {
		String baseQuery = "INSERT INTO `npcs`" + " VALUES (?,?,?,?);";
		try {
			PreparedStatement statement = connection.prepareStatement(baseQuery);
			statement.setInt(1, mapid);
			statement.setInt(2, npcid);
			statement.setInt(3, cellid);
			statement.setInt(4, orientation);

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(NpcData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public boolean delete(Npc obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean delete(int mapid, int cellid) {
		String baseQuery = "DELETE FROM npcs WHERE mapid = "+mapid+" AND cellid = "+cellid;
		execute(baseQuery);
		return true;
	}

	@Override
	public boolean update(Npc obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Npc load(int id) {
		Npc npc = null;
		try {
			ResultSet result = getData("SELECT * FROM npcs WHERE mapid = "+id);
			while(result.next()) {
				Maps map = World.data.getCarte(result.getShort("mapid"));
				
				if (map == null)
					return null;
				
				npc = map.addNpc(result.getInt("npcid"), result.getInt("cellid"),
						result.getInt("orientation"));
			}
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(NpcData): "+e.getMessage());
		}
		return npc;
	}
}
