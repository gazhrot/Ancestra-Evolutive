package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.guild.Guild;

public class GuildData extends AbstractDAO<Guild>{

	public GuildData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Guild obj) {
		String baseQuery = "INSERT INTO `guilds` VALUES (?,?,?,1,0,0,0,?,?);";
		try {
			PreparedStatement statement = connection.prepareStatement(baseQuery);
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
			PreparedStatement statement = connection.prepareStatement(baseQuery);
			
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
			ResultSet result = getData("SELECT * FROM guilds WHERE id = "+id);
			
			if (result.next()) {
				guild = new Guild(result.getInt("id"), result.getString("name"), result
								.getString("emblem"), result.getInt("lvl"), result
								.getLong("xp"), result.getInt("capital"), result
								.getInt("nbrmax"), result.getString("sorts"), result
								.getString("stats"));
				World.data.addGuild(guild, false);
				World.database.getGuildMemberData().load(id);
			}
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(GuildData): "+e.getMessage());
		}
		return guild;
	}
	
	public boolean exist(String name) {
		boolean exist = false;
		try {
			ResultSet result = getData("SELECT * FROM guilds WHERE name = '"+name+"'");
			exist = result.next();
			closeResultSet(result);
		} catch(Exception e) {
			Console.instance.writeln("SQL ERROR(GuildData): "+e.getMessage());
		}
		return exist;
	}

}
