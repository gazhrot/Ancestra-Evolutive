package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.map.MountPark;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;

public class MountparkData extends AbstractDAO<MountPark>{

	public MountparkData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.MountPark");
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
			
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
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
			Result result = getData("SELECT * FROM mountpark_data WHERE mapid ="+id);
			while(result.resultSet.next()) {
				Maps map = World.data.getCarte(result.resultSet.getShort("mapid"));
				
				if (map == null)
					continue;
				
				park = new MountPark(result.resultSet.getInt("owner"), map,
						result.resultSet.getInt("cellid"), result.resultSet.getInt("size"),
                        result.resultSet.getString("data"), result.resultSet.getInt("guild"),
                        result.resultSet.getInt("price"));
				World.data.addMountPark(park);
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MountparkData): "+e.getMessage());
		}
		return null;
	}
}
