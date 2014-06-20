package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.map.MountPark;

public class MountparkData extends AbstractDAO<MountPark>{

	public MountparkData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(MountPark obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(MountPark obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(MountPark obj) {
		try {
			String baseQuery = "REPLACE INTO `mountpark_data`( `mapid` , `cellid`, `size` , `owner` , `guild` , `price` , `data` )"
					+ " VALUES (?,?,?,?,?,?,?);";
			
			PreparedStatement statement = connection.prepareStatement(baseQuery);
			
			statement.setInt(1, obj.getMap().getId());
			statement.setInt(2, obj.getCellid());
			statement.setInt(3, obj.getSize());
			statement.setInt(4, obj.getOwner());
			statement.setInt(5, (obj.getGuild() == null ? -1 : obj.getGuild().getId()));
			statement.setInt(6, obj.getPrice());
			statement.setString(7, obj.parseDataToDb());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MountparkData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public MountPark load(int id) {
		MountPark park = null;
		try {
			ResultSet result = getData("SELECT * FROM mountpark_data WHERE mapid ="+id);
			while(result.next()) {
				Maps map = World.data.getCarte(result.getShort("mapid"));
				
				if (map == null)
					continue;
				
				park = new MountPark(result.getInt("owner"), map,
						result.getInt("cellid"), result.getInt("size"), result
								.getString("data"), result.getInt("guild"), result
								.getInt("price"));
				World.data.addMountPark(park);
			}
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MountparkData): "+e.getMessage());
		}
		return null;
	}
}
