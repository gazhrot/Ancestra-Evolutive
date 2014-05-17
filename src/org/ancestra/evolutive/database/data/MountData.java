package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.Mount;




public class MountData extends AbstractDAO<Mount>{

	public MountData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Mount obj) {
		try {
			String baseQuery = "REPLACE INTO `mounts_data`(`id`,`color`,`sexe`,`name`,`xp`,`level`,"
					+ "`endurance`,`amour`,`maturite`,`serenite`,`reproductions`,`fatigue`,`items`,"
					+ "`ancetres`,`energie`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
			
			PreparedStatement statement = connection.prepareStatement(baseQuery);
			
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
		String baseQuery = "DELETE FROM `mounts_data` WHERE `id` = "+obj.getId();
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
			
			PreparedStatement statement = connection.prepareStatement(baseQuery);
			
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
			ResultSet result = getData("SELECT * FROM mounts_data WHERE id = "+id);
			
			if(result.next()) {
				mount = new Mount(result.getInt("id"), result
						.getInt("color"), result.getInt("sexe"),
						result.getInt("amour"), result.getInt("endurance"), result
								.getInt("level"), result.getLong("xp"), result
								.getString("name"), result.getInt("fatigue"), result
								.getInt("energie"), result.getInt("reproductions"),
						result.getInt("maturite"), result.getInt("serenite"), result
								.getString("items"), result.getString("ancetres"));
				World.data.addDragodinde(mount);
			}
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(MountData): "+e.getMessage());
		}
		return mount;
	}
}
