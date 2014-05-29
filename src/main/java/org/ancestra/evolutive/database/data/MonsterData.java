package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.monster.MobTemplate;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;




public class MonsterData extends AbstractDAO<MobTemplate>{

	public MonsterData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("Monster factory");
	}

	@Override
	public boolean create(MobTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(MobTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(MobTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MobTemplate load(int id) {
		MobTemplate monster = null;
		try {
			Result result = getData("SELECT * FROM monsters WHERE id = "+id);
			
			if(result.resultSet.next()) {
				int gfxID = result.resultSet.getInt("gfxID");
				int align = result.resultSet.getInt("align");
				String colors = result.resultSet.getString("colors");
				String grades = result.resultSet.getString("grades");
				String spells = result.resultSet.getString("spells");
				String stats = result.resultSet.getString("stats");
				String pdvs = result.resultSet.getString("pdvs");
				String pts = result.resultSet.getString("points");
				String inits = result.resultSet.getString("inits");
				int mK = result.resultSet.getInt("minKamas");
				int MK = result.resultSet.getInt("maxKamas");
				int IAType = result.resultSet.getInt("AI_Type");
				String xp = result.resultSet.getString("exps");
				
				boolean capturable;
				
				if (result.resultSet.getInt("capturable") == 1) 
					capturable = true;
				 else
					capturable = false;
				

				monster = new MobTemplate(id, gfxID, align,
						colors, grades, spells, stats, pdvs, pts, inits, mK,
						MK, xp, IAType, capturable);
				World.data.addMobTemplate(id, monster);
				World.database.getDropData().load(id);
			}
			
			close(result);
		} catch (Exception e) {
			Console.instance.println("SQL ERROR(MonsterData): "+e.getMessage());
		}
		return monster;
	}
	
	public boolean saveNewFixGroup(int mapID, int cellID, String groupData) {
		try {
			String baseQuery = "REPLACE INTO `mobgroups_fix` VALUES(?,?,?)";
			PreparedStatement statement = getPreparedStatement(baseQuery);

			statement.setInt(1, mapID);
			statement.setInt(2, cellID);
			statement.setString(3, groupData);

			execute(statement);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
