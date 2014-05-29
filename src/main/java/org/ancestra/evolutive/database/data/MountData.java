package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.Mount;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;




public class MountData extends AbstractDAO<Mount>{

	public MountData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("Mount factory");
	}

	@Override
	public boolean create(Mount obj) {
		try {
			String baseQuery = "REPLACE INTO `mounts_data`(`id`,`color`,`sexe`,`name`,`xp`,`level`,"
					+ "`endurance`,`amour`,`maturite`,`serenite`,`reproductions`,`fatigue`,`items`,"
					+ "`ancetres`,`energie`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
			
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
			statement.setInt(1, obj.getId());
			statement.setInt(2, obj.getColor());
			statement.setInt(3, obj.getSex());
			statement.setString(4, obj.getName());
			statement.setLong(5, obj.getExperience());
			statement.setInt(6, obj.getLevel());
			statement.setInt(7, obj.getEndurance());
			statement.setInt(8, obj.getAmour());
			statement.setInt(9, obj.getMaturite());
			statement.setInt(10, obj.getSerenite());
			statement.setInt(11, obj.getReproduction());
			statement.setInt(12, obj.getFatigue());
			statement.setString(13, obj.getObjectsId());
			statement.setString(14, obj.getAncestor());
			statement.setInt(15, obj.getEnergy());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MountData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public boolean delete(Mount obj) {
		String baseQuery = "DELETE FROM `mounts_data` WHERE `id` = "+obj.getId()+";";
		execute(baseQuery);
		return true;
	}

	@Override
	public boolean update(Mount obj) {
		try {
			String baseQuery = "UPDATE mounts_data SET " + "`name` = ?,"
					+ "`xp` = ?," + "`level` = ?," + "`endurance` = ?,"
					+ "`amour` = ?," + "`maturite` = ?," + "`serenite` = ?,"
					+ "`reproductions` = ?," + "`fatigue` = ?," + "`energie` = ?,"
					+ "`ancetres` = ?," + "`items` = ?" + " WHERE `id` = ?;";
			
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
			statement.setString(1, obj.getName());
			statement.setLong(2, obj.getExperience());
			statement.setInt(3, obj.getLevel());
			statement.setInt(4, obj.getEndurance());
			statement.setInt(5, obj.getAmour());
			statement.setInt(6, obj.getMaturite());
			statement.setInt(7, obj.getSerenite());
			statement.setInt(8, obj.getReproduction());
			statement.setInt(9, obj.getFatigue());
			statement.setInt(10, obj.getEnergy());
			statement.setString(11, obj.getAncestor());
			statement.setString(12, obj.getObjectsId());
			statement.setInt(13, obj.getId());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MountData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public Mount load(int id) {
		Mount mount = null;
		try {
			Result result = getData("SELECT * FROM mounts_data WHERE id = "+id+";");
			
			if(result.resultSet.next()) {
				mount = new Mount(result.resultSet.getInt("id"), result.resultSet.getInt("color"), result.resultSet.getInt("sexe"),
						result.resultSet.getInt("amour"), result.resultSet.getInt("endurance"), 
                        result.resultSet.getInt("level"), result.resultSet.getLong("xp"), 
                        result.resultSet.getString("name"), result.resultSet.getInt("fatigue"), 
                        result.resultSet.getInt("energie"), result.resultSet.getInt("reproductions"),
						result.resultSet.getInt("maturite"), result.resultSet.getInt("serenite"), 
                        result.resultSet.getString("items"), result.resultSet.getString("ancetres"));
				World.data.addDragodinde(mount);
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MountData): "+e.getMessage());
		}
		return mount;
	}
}
