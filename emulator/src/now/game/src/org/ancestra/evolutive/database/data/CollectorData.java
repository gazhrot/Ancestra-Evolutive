package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.creature.collector.Collector;
import org.ancestra.evolutive.map.Maps;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CollectorData extends AbstractDAO<Collector>{

	public CollectorData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.Collector");
	}

	@Override
	public boolean create(Collector obj) {
		String baseQuery = "INSERT INTO `percepteurs`" +
				" VALUES (?,?,?,?,?,?,?,?,?,?);";
		try {
			PreparedStatement statement = getPreparedStatement(baseQuery);
			statement.setInt(1, obj.getId());
			statement.setInt(2, obj.getMap().getId());
			statement.setInt(3, obj.getCell().getId());
			statement.setInt(4, obj.getOrientation());
			statement.setInt(5, obj.getGuild().getId());
			statement.setInt(6, obj.getFirstNameId());
			statement.setInt(7, obj.getLastNameId());
			statement.setString(8, "");
			statement.setLong(9, 0);
			statement.setLong(10, 0);
			
			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(CollectorData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public boolean delete(Collector obj) {
		String baseQuery = "DELETE FROM percepteurs WHERE guid = "+obj.getId();
		execute(baseQuery);
		return true;
	}

	@Override
	public boolean update(Collector obj) {
		String baseQuery = "UPDATE `percepteurs` SET " + "`objets` = ?,"
				+ "`kamas` = ?," + "`xp` = ?" + " WHERE guid = ?;";

		try {
			PreparedStatement statement = getPreparedStatement(baseQuery);
			statement.setString(1, obj.parseItemPercepteur());
			statement.setLong(2, obj.getKamas());
			statement.setLong(3, obj.getXp());
			statement.setInt(4, obj.getId());
			
			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(CollectorData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public Collector load(int id) {
		Collector collector = null;
		try {
			Result result = getData("SELECT * FROM percepteurs WHERE guid = "+id);
			collector = loadFromResultSet(result.resultSet);
			close(result);
            logger.debug("Collector {} have been loaded",id);
		} catch(Exception e) {
			logger.error("Collector {} can't be loaded ", id, e);
		}
		return collector;
	}
	
	public Collector loadByMap(int id) {
		Collector collector = null;
		try {
			Result result = getData("SELECT * FROM percepteurs WHERE mapid = "+id);
			collector = loadFromResultSet(result.resultSet);
			close(result);
            logger.debug("Collectors of map {} have been loaded",id);
		} catch(Exception e) {
            logger.error("Collectors on map {} can't be loaded ",id,e);
		}
		return collector;
	}

    public void load() {
        try {
            Result result = getData("SELECT * FROM percepteurs");
            while(loadFromResultSet(result.resultSet)!=null);
            close(result);
            logger.debug("Collectors have been loaded");
        } catch(Exception e) {
            logger.error("Collectors can't be loaded ", e);
        }
    }

    public int nextId() {
        int guid = -1;

        try {
            String query = "SELECT MAX(guid) AS max FROM percepteurs;";
            Result result = getData(query);
            while(result.resultSet.next())
                guid = result.resultSet.getInt("max")+1;
            close(result);
            logger.trace("Last id in personnage is {}", guid);
        } catch (Exception e) {
            logger.info("Can t find last id in personnages", e);
        }
        return guid;
    }

    protected Collector loadFromResultSet(ResultSet resultSet) throws SQLException {
        Collector collector = null;
        if(resultSet.next()) {
            Maps map = World.data.getMap(resultSet.getShort("mapid"));
           
            if(map == null) 
            	return null;

            collector = new Collector(
                    resultSet.getInt("guid"),
                    resultSet.getShort("mapid"),
                    resultSet.getInt("cellid"),
                    resultSet.getByte("orientation"),
                    resultSet.getInt("guild_id"),
                    resultSet.getShort("N1"),
                    resultSet.getShort("N2"),
                    resultSet.getString("objets"),
                    resultSet.getLong("kamas"),
                    resultSet.getLong("xp"));
            World.data.addPerco(collector);
        }
        return collector;
    }
}
