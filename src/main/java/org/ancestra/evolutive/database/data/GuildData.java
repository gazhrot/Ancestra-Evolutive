package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.guild.Guild;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildData extends AbstractDAO<Guild>{

	public GuildData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("Guild factory");
	}

	@Override
	public boolean create(Guild obj) {
		String baseQuery = "INSERT INTO `guilds` VALUES (?,?,?,1,0,0,0,?,?);";
		try {
			PreparedStatement statement = getPreparedStatement(baseQuery);
			statement.setInt(1, obj.getId());
			statement.setString(2, obj.getName());
			statement.setString(3, obj.getEmblem());
			statement.setString(4, "462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0|");
			statement.setString(5, "176;100|158;1000|124;100|");

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(GuildData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public boolean delete(Guild obj) {
		String baseQuery = "DELETE FROM `guilds` WHERE `id` = "+obj.getId();
		execute(baseQuery);
		return true;
	}

	@Override
	public boolean update(Guild obj) {
		String baseQuery = "UPDATE `guilds` SET `lvl` = ?, `xp` = ?,"
				+ "`capital` = ?, `nbrmax` = ?, `sorts` = ?,"
				+ "`stats` = ? " 
				+ "WHERE id = ?;";

		try {
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
			statement.setInt(1, obj.getLevel());
			statement.setLong(2, obj.getExperience());
			statement.setInt(3, obj.getCapital());
			statement.setInt(4, obj.getNbrCollector());
			statement.setString(5, obj.compileSpells());
			statement.setString(6, obj.compileStats());
			statement.setInt(7, obj.getId());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(GuildData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public Guild load(int id) {
		Guild guild = null;
		try {
			Result result = getData("SELECT * FROM guilds WHERE id = "+id);
			guild = loadFromResultSet(result.resultSet);
			close(result);
            logger.debug("Guild {} has been loaded",id);
		} catch (Exception e) {
			logger.error("Can't load guild {}",id,e);
		}
		return guild;
	}
	
	public boolean exist(String name) {
		boolean exist = false;
		try {
			Result result = getData("SELECT * FROM guilds WHERE name = '"+name+"'");
			exist = result.resultSet.next();
			close(result);
            logger.debug("Guild named {} has been found",name);
		} catch(Exception e) {
			logger.error("Can't find guild {}",name,e);
		}
		return exist;
	}

    protected Guild loadFromResultSet(ResultSet resultSet) throws SQLException {
        Guild guild = null;
        if (resultSet.next()) {
            guild = new Guild(resultSet.getInt("id"), resultSet.getString("name"), resultSet
                    .getString("emblem"), resultSet.getInt("lvl"), resultSet
                    .getLong("xp"), resultSet.getInt("capital"), resultSet
                    .getInt("nbrmax"), resultSet.getString("sorts"), resultSet
                    .getString("stats"));
            World.data.addGuild(guild, false);
            World.database.getGuildMemberData().load(guild.getId());
        }
        return guild;
    }

}
