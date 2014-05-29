package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.house.Trunk;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;





public class HouseData extends AbstractDAO<House>{

	public HouseData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("House factory");
	}

	@Override
	public boolean create(House obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(House obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean reset(int guild) {
		String query = "UPDATE `houses` SET `guild_rights`='0', `guild_id`='0' WHERE `guild_id`= "+guild;
		execute(query);
		return true;
	}

	@Override
	public boolean update(House obj) {
		try {
			String baseQuery = "UPDATE `houses` SET " + "`owner_id` = ?,"
					+ "`sale` = ?," + "`guild_id` = ?," + "`access` = ?,"
					+ "`key` = ?," + "`guild_rights` = ?" + " WHERE id = ?;";
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
			statement.setInt(1, obj.get_owner_id());
			statement.setInt(2, obj.get_sale());
			statement.setInt(3, obj.get_guild_id());
			statement.setInt(4, obj.get_access());
			statement.setString(5, obj.get_key());
			statement.setInt(6, obj.get_guild_rights());
			statement.setInt(7, obj.get_id());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HouseData): "+e.getMessage());
		}
		return false;
	}
	
	public boolean update(Player player, House house) {
		try {
			String query = "UPDATE `houses` SET `sale`='0', " +
					"`owner_id`= "+player.getAccount().getUUID()+", `guild_id`='0', `access`='0'," +
					" `key`='-', `guild_rights`='0' WHERE `id` = "+house.get_id();
			execute(query);
			
			house.set_sale(0);
			house.set_owner_id(player.getAccount().getUUID());
			house.set_guild_id(0);
			house.set_access(0);
			house.set_key("-");
			house.set_guild_rights(0);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HouseData): "+e.getMessage());
		}

		
		ArrayList<Trunk> trunks = Trunk.getTrunksByHouse(house);
		for (Trunk trunk : trunks) {
			trunk.set_owner_id(player.getAccount().getUUID());
			trunk.set_key("-");
		}

		try {
			String query = "UPDATE `coffres` SET `owner_id`=?, `key`='-' WHERE `id_house` = ?;";
			PreparedStatement statement = getPreparedStatement(query);
			
			statement.setInt(1, player.getAccount().getUUID());
			statement.setInt(2, house.get_id());
			
			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HouseData): "+e.getMessage());
		}
		return false;
	}
	
	public boolean update(House house, int price) {
		house.set_sale(price);
		
		String query = "UPDATE `houses` SET `sale`=? WHERE `id` = ?;";
		try {
			PreparedStatement statement = getPreparedStatement(query);
			statement.setInt(1, price);
			statement.setInt(2, house.get_id());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HouseData): "+e.getMessage());
		}
		return false;
	}
	
	public boolean update(Player player, House house, String packet) {
		try {
			String query = "UPDATE `houses` SET `key`=? WHERE `id`=? AND owner_id=?;";
			PreparedStatement statement = getPreparedStatement(query);
			
			statement.setString(1, packet);
			statement.setInt(2, house.get_id());
			statement.setInt(3, player.getAccount().getUUID());

			execute(statement);

			house.set_key(packet);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HouseData): "+e.getMessage());
		}
		return false;
	}
	
	public boolean update(House house, int guild, int rights) {
		try {
			String query = "UPDATE `houses` SET `guild_id`=?, `guild_rights`=? WHERE `id`=?;";
			PreparedStatement statement = getPreparedStatement(query);
			statement.setInt(1, guild);
			statement.setInt(2, rights);
			statement.setInt(3, house.get_id());

			execute(statement);
			
			house.set_guild_id(guild);
			house.set_guild_rights(rights);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HouseData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public House load(int id) {
		House house = null;
		try {
			Result result = getData("SELECT * FROM houses WHERE id = "+id);
			
			if(result.resultSet.next()) {
				house = new House(result.resultSet.getInt("id"), result.resultSet
						.getShort("map_id"), result.resultSet.getInt("cell_id"), result.resultSet
						.getInt("owner_id"), result.resultSet.getInt("sale"), result.resultSet
						.getInt("guild_id"), result.resultSet.getInt("access"), result.resultSet
						.getString("key"), result.resultSet.getInt("guild_rights"), result.resultSet
						.getInt("mapid"), result.resultSet.getInt("caseid"));
				World.data.addHouse(house);
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HouseData): "+e.getMessage());
		}
		return house;
	}
}
