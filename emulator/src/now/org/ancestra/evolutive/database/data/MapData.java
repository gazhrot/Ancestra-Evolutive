package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.other.Action;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;

public class MapData extends AbstractDAO<Maps>{

	public MapData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.Map");
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
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
			statement.setString(1, obj.getPlaces());
			statement.setInt(2, obj.getMaxGroup());
			statement.setInt(3, obj.getId());

			execute(statement);
			return true;
		} catch (Exception e) {
			logger.error("Error while updating map {} ",obj.getId(),e);
		}
		return false;
	}

	@Override
	public Maps load(int id) {
		Maps map = null;
		try {
			Result result = getData("SELECT * FROM maps WHERE id = "+id);
			
			if(result.resultSet.next()) {
				map = new Maps(result.resultSet.getShort("id"), 
                        result.resultSet.getString("date"), result.resultSet.getByte("width"), 
                        result.resultSet.getByte("heigth"), result.resultSet.getString("key"), 
                        result.resultSet.getString("places"), result.resultSet.getString("mapData"), 
                        result.resultSet.getString("cells"), result.resultSet.getString("monsters"), 
                        result.resultSet.getString("mappos"), result.resultSet.getByte("numgroup"), 
                        result.resultSet.getByte("groupmaxsize"));
				World.data.addMap(map);
				World.database.getCollectorData().loadByMap(id);
				World.database.getHouseData().load(map);
				World.database.getHdvData().load(id);
				World.database.getMountparkData().load(id);
				World.database.getNpcData().load(id);
				World.database.getScriptedCellData().load(id);
				World.database.getTrunkData().load(id);
				loadFightActions(map);
			}
			close(result);
			
			result = getData("SELECT * from mobgroups_fix WHERE mapid = "+id);
			
			while (result.resultSet.next()) {
				if (map == null)
					continue;
				if (map.getCases().get(result.resultSet.getInt("cellid")) == null)
					continue;
				map.addStaticGroup(result.resultSet.getInt("cellid"), result.resultSet.getString("groupData"));
			}
			close(result);
		} catch (Exception e) {
            logger.error("Error while creating map {} ",id,e);
		}
		return map;
	}
	
	public void loadFightActions(Maps map) {
		try {
			Result result = getData("SELECT * FROM endfight_action WHERE map = "+map.getId());
			while (result.resultSet.next()) {
				
				map.addEndFightAction(result.resultSet.getInt("fighttype"),
						new Action(result.resultSet.getInt("action"), result.resultSet.getString("args"),
								result.resultSet.getString("cond")));
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MapData): "+e.getMessage());
		}
	}
	
	public Maps loadMapByPos(int x1, int y1, int cont1) {
		try {
			Result result = getData("SELECT id, mappos FROM maps");
			Maps carte = null;
			while (result.resultSet.next()) {
				String[] mappos = result.resultSet.getString("mappos").split(",");
				int x2 = -1, y2 = -1, cont2 = -1;
				try {
					x2 = Integer.parseInt(mappos[0]);
					y2 = Integer.parseInt(mappos[1]); 
					cont2 = World.data.getSubArea(Integer.parseInt(mappos[2])).getArea().getContinent().getId();
				} catch(Exception e) {}
				if(x1 == x2 && y1 == y2 && cont1 == cont2) {
					carte = World.data.getMap(result.resultSet.getShort("id"));
					break;
				}
			}
			close(result);
			return carte;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MapData): "+e.getMessage());
		}
		return null;
	}
}
