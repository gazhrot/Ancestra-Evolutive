package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.guild.GuildMember;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;

public class GuildMemberData extends AbstractDAO<GuildMember>{

	public GuildMemberData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("Guild member factory");
	}

	@Override
	public boolean create(GuildMember obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(GuildMember obj) {
		String baseQuery = "DELETE FROM `guild_members` WHERE `guid` = "+obj.getUUID();
		execute(baseQuery);
		return true;
	}
	
	public boolean deleteAllByGuild(int guild) {
		String baseQuery = "DELETE FROM `guild_members` WHERE `guild` = "+guild;
		execute(baseQuery);
		return true;
	}

	@Override
	public boolean update(GuildMember obj) {
		String baseQuery = "REPLACE INTO `guild_members` VALUES(?,?,?,?,?,?,?,?,?,?,?);";

		try {
			PreparedStatement statement = getPreparedStatement(baseQuery);
			statement.setInt(1, obj.getUUID());
			statement.setInt(2, obj.getGuild().getId());
			statement.setString(3, obj.getPlayer().getName());
			statement.setInt(4, obj.getPlayer().getLevel());
			statement.setInt(5, obj.getPlayer().getGfx());
			statement.setInt(6, obj.getRank());
			statement.setLong(7, obj.getXpGave());
			statement.setInt(8, obj.getXpGive());
			statement.setInt(9, obj.getRight());
			statement.setInt(10, obj.getPlayer().getAlign());
			statement.setString(11, obj.getPlayer().getAccount().getLastConnection());

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(GuildMemberData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public GuildMember load(int guildid) {
		GuildMember member = null;
		try {
			Result result = getData("SELECT * FROM guild_members WHERE guild = "+guildid);
			
			while(result.resultSet.next()) {
				Guild guild = World.data.getGuild(guildid);
				
				if (guild == null)
					return null;
				
				member = guild.addMember(result.resultSet.getInt("guid"),
						result.resultSet.getInt("rank"), result.resultSet.getByte("pxp"),
						result.resultSet.getLong("xpdone"), result.resultSet.getInt("rights"));
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(GuildMemberData): "+e.getMessage());
		}
		return member;
	}
	
	public int getGuildByPlayer(int guid) {
		int guild = -1;
		try {
			Result result = getData("SELECT guid FROM `guild_members` WHERE guid = "+guid);
			if(result.resultSet.next())
				guild = result.resultSet.getInt("guid");
			close(result);
		} catch(Exception e) {
			Console.instance.writeln("SQL ERROR(GuildMemberData): "+e.getMessage());
		}
		return guild;
	}
	
	public int playerExistInGuild(int guid) {
		int guildID = 0;
		try {
			Result result = getData("SELECT guild FROM `guild_members` WHERE guid = "+ guid);
			if(result.resultSet.next())
				guildID = result.resultSet.getInt("guild");
			close(result);
		} catch(Exception e) {
			Console.instance.writeln("SQL ERROR(GuildMemberData): "+e.getMessage());
		}
		return guildID;
	}
	
	public int[] playerExistInGuild(String name) {
		int guildId = -1, guid = -1;
		try {
			Result result = getData("SELECT guild,guid FROM `guild_members` WHERE name = '" + name + "'");
			if(result.resultSet.next()) {
				guildId = result.resultSet.getInt("guild");
				guid = result.resultSet.getInt("guid");
			}
			close(result);
		} catch(Exception e) {
			Console.instance.writeln("SQL ERROR(GuildMemberData): "+e.getMessage());
		}
		return new int[] { guid, guildId };
	}

}
