package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.house.Trunk;
import org.ancestra.evolutive.map.Maps;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HouseData extends AbstractDAO<House>{

	public HouseData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.House");
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
	
	public boolean reset(Guild guild) {
		String query = "UPDATE `houses` SET `guild_rights`='0', `guild_id`='0' WHERE `guild_id`= "+guild.getId();
		execute(query);
        logger.debug("house from guild {}(id : {}) has been reset",guild.getName(),guild.getId());
		return true;
	}

	@Override
	public boolean update(House house) {
		try {
			String baseQuery = "UPDATE `houses` SET " + "`owner_id` = ?,"
					+ "`sale` = ?," + "`guild_id` = ?," + "`access` = ?,"
					+ "`key` = ?," + "`guild_rights` = ?" + " WHERE id = ?;";
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
			statement.setInt(1, house.getOwner());
			statement.setInt(2, house.getSale());
			statement.setInt(3, house.getGuildId());
			statement.setInt(4, house.getAccess());
			statement.setString(5, house.getKey());
			statement.setInt(6, house.getGuildRights());
			statement.setInt(7, house.getId());

			execute(statement);
            logger.debug("house id {} has been updated",house.getId());
			return true;
		} catch (Exception e) {
            logger.error("can t update house id {}", house.getId(), e);
		}
		return false;
	}
	
	public boolean update(Player player, House house) {
		try {
			String query = "UPDATE `houses` SET `sale`='0', " +
					"`owner_id`= "+player.getAccount().getUUID()+", `guild_id`='0', `access`='0'," +
					" `key`='-', `guild_rights`='0' WHERE `id` = "+house.getId();
			execute(query);
			
			house.setSale(0);
			house.setOwner(player.getAccount().getUUID());
			house.setGuildId(0);
			house.setAccess(0);
			house.setKey("-");
			house.setGuildRights(0);
		} catch (Exception e) {
            logger.error("can t update house id {}", house.getId(), e);
		}

		
		ArrayList<Trunk> trunks = Trunk.getTrunksByHouse(house);
		for (Trunk trunk : trunks) {
			trunk.setOwner(player.getAccount().getUUID());
			trunk.setKey("-");
		}

		try {
			String query = "UPDATE `coffres` SET `owner_id`=?, `key`='-' WHERE `id_house` = ?;";
			PreparedStatement statement = getPreparedStatement(query);
			
			statement.setInt(1, player.getAccount().getUUID());
			statement.setInt(2, house.getId());
			
			execute(statement);
            logger.debug("house id {} has been updated",house.getId());
			return true;
		} catch (Exception e) {
            logger.error("can t update house id {}", house.getId(), e);
		}
		return false;
	}
	
	public boolean update(House house, int price) {
		house.setSale(price);
		
		String query = "UPDATE `houses` SET `sale`=? WHERE `id` = ?;";
		try {
			PreparedStatement statement = getPreparedStatement(query);
			statement.setInt(1, price);
			statement.setInt(2, house.getId());

			execute(statement);
            logger.debug("house id {} has been updated",house.getId());
			return true;
		} catch (Exception e) {
			logger.error("can t update house id {}", house.getId(), e);
		}
		return false;
	}
	
	public boolean update(Player player, House house, String packet) {
		try {
			String query = "UPDATE `houses` SET `key`=? WHERE `id`=? AND owner_id=?;";
			PreparedStatement statement = getPreparedStatement(query);
			
			statement.setString(1, packet);
			statement.setInt(2, house.getId());
			statement.setInt(3, player.getAccount().getUUID());

			execute(statement);

			house.setKey(packet);
            logger.debug("house id {} has been updated",house.getId());
			return true;
		} catch (Exception e) {
			logger.error("can t update house id {}", house.getId(), e);
		}
		return false;
	}
	
	public boolean update(House house, int guild, int rights) {
		try {
			String query = "UPDATE `houses` SET `guild_id`=?, `guild_rights`=? WHERE `id`=?;";
			PreparedStatement statement = getPreparedStatement(query);
			statement.setInt(1, guild);
			statement.setInt(2, rights);
			statement.setInt(3, house.getId());

			execute(statement);
			
			house.setGuildId(guild);
			house.setGuildRights(rights);
            logger.debug("house id {} has been updated",house.getId());
			return true;
		} catch (Exception e) {
			logger.error("can t update house id {} ", house.getId(), e);
		}
		return false;
	}

	@Override
	public House load(int id) {
		House house = null;
		try {
			Result result = getData("SELECT * FROM houses WHERE id = "+id);
			house = load(result.resultSet);
			close(result);
            logger.debug("house id {} has been loaded",id);
		} catch (Exception e) {
			logger.error("can t load house id {}", id, e);
		}
		return house;
	}

    /**
     * Charge l'ensemble des maisons se situant sur la carte
     * @param map Carte dont les maisons doivent etre chargee
     * @return
     */
    public ArrayList<House> load(Maps map) {
        ArrayList<House> houses = new ArrayList<>();
        try {
            Result result = getData("SELECT * FROM houses WHERE map_id = "+map.getId());
            House house;
            while((house = load(result.resultSet)) != null) houses.add(house);
            close(result);
            logger.debug("{} houses has been loaded,ids : ",houses.size()+1,houses);
        } catch (Exception e) {
            logger.error("can t load house id {}", map.getId(), e);
        }
        return houses;
    }

    protected House load(ResultSet result) throws SQLException {
        House house = null;
        if(result.next()) {
            house = new House(result.getInt("id"), result
                    .getShort("map_id"), result.getInt("cell_id"),
                    result.getInt("owner_id"), result.getInt("sale"),
                    result.getInt("guild_id"), result.getInt("access"),
                    result.getString("key"), result.getInt("guild_rights"),
                    result.getInt("mapid"), result.getInt("caseid"));
            World.data.addHouse(house);
        }
        return house;
    }
}
