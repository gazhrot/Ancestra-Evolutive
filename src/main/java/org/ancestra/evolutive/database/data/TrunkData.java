package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.house.Trunk;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrunkData extends AbstractDAO<Trunk>{

	public TrunkData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.Trunk");
	}

	@Override
	public boolean create(Trunk obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Trunk obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Trunk obj) {
		try {
			String query = "UPDATE `coffres` SET `kamas`=?, `object`=? WHERE `id`=?";
			PreparedStatement statement = getPreparedStatement(query);
			
			statement.setLong(1, obj.getKamas());
			statement.setString(2, obj.parseTrunkObjetsToDB());
			statement.setInt(3, obj.getId());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(TrunkData): "+e.getMessage());
		}
		return false;
	}

	public boolean update(Player player, Trunk trunk, String packet) {
		try {
			String query = "UPDATE `coffres` SET `key`=? WHERE `id`=? AND owner_id=?;";
			PreparedStatement statement = getPreparedStatement(query);
			
			statement.setString(1, packet);
			statement.setInt(2, trunk.getId());
			statement.setInt(3, player.getAccount().getUUID());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(TrunkData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public Trunk load(int mapid) {//TODO A revoir
		Trunk trunk = null;
		try {
			Result result = getData("SELECT * FROM coffres WHERE mapid ="+mapid);
            while((trunk = loadFromResultSet(result.resultSet)) != null){
            }
			close(result);
            logger.debug("All trunks on map {} has been loaded",mapid);
		} catch (Exception e) {
			logger.error("Can't load all trunks on {}",mapid,e);
		}
		return trunk;
	}

    protected Trunk loadFromResultSet(ResultSet result) throws SQLException {
        Trunk trunk = null;
        if(result.next()) {
            trunk = new Trunk(result.getInt("id"), result
                    .getInt("id_house"), result.getShort("mapid"), result
                    .getInt("cellid"), result.getString("object"), result
                    .getInt("kamas"), result.getString("key"), result
                    .getInt("owner_id"));
            World.data.addTrunk(trunk);
        }
        return trunk;
    }

}
