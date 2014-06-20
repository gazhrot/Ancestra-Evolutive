package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.other.Action;

public class MapData extends AbstractDAO<Maps>{

	public MapData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Maps obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Maps obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Maps obj) {
		try {
			String baseQuery = "UPDATE `maps` SET " + "`places` = ?, "
					+ "`numgroup` = ? " + "WHERE id = ?;";
			PreparedStatement statement = connection.prepareStatement(baseQuery);
			
			statement.setString(1, obj.getPlaces());
			statement.setInt(2, obj.getMaxGroup());
			statement.setInt(3, obj.getId());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MapData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public Maps load(int id) {
		Maps map = null;
		try {
			ResultSet result = getData("SELECT * FROM maps WHERE id = "+id);
			
			if(result.next()) {
				map = new Maps(result.getShort("id"), result
						.getString("date"), result.getByte("width"), result
						.getByte("heigth"), result.getString("key"), result
						.getString("places"), result.getString("mapData"), result
						.getString("cells"), result.getString("monsters"), result
						.getString("mappos"), result.getByte("numgroup"), result
						.getByte("groupmaxsize"));
				World.data.addCarte(map);
				World.database.getCollectorData().loadByMap(id);
				World.database.getHouseData().load(id);
				World.database.getHdvData().load(id);
				World.database.getMountparkData().load(id);
				World.database.getNpcData().load(id);
				World.database.getScriptedCellData().load(id);
				World.database.getTrunkData().load(id);
				loadFightActions(map);
			}
			closeResultSet(result);
			
			result = getData("SELECT * from mobgroups_fix WHERE mapid = "+id);
			
			while (result.next()) {
				if (map == null)
					continue;
				if (map.getCases().get(result.getInt("cellid")) == null)
					continue;
				map.addStaticGroup(result.getInt("cellid"), result.getString("groupData"));
			}
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MapData): "+e.getMessage());
		}
		return map;
	}
	
	public void loadFightActions(Maps map) {
		try {
			ResultSet result = getData("SELECT * FROM endfight_action WHERE map = "+map.getId());
			while (result.next()) {
				
				map.addEndFightAction(result.getInt("fighttype"),
						new Action(result.getInt("action"), result.getString("args"),
								result.getString("cond")));
			}
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MapData): "+e.getMessage());
		}
	}
	
	public Maps loadMapByPos(int x1, int y1, int cont1) {
		try {
			ResultSet result = getData("SELECT id, mappos FROM maps");
			Maps carte = null;
			while (result.next()) {
				String[] mappos = result.getString("mappos").split(",");
				int x2 = -1, y2 = -1, cont2 = -1;
				try {
					x2 = Integer.parseInt(mappos[0]);
					y2 = Integer.parseInt(mappos[1]); 
					cont2 = World.data.getSubArea(Integer.parseInt(mappos[2])).getArea().getContinent().getId();
				} catch(Exception e) {}
				if(x1 == x2 && y1 == y2 && cont1 == cont2) {
					carte = World.data.getCarte(result.getShort("id"));
					break;
				}
			}
			closeResultSet(result);
			return carte;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MapData): "+e.getMessage());
		}
		return null;
	}
}
