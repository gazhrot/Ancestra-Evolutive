package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.monster.MobTemplate;




public class MonsterData extends AbstractDAO<MobTemplate>{

	public MonsterData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
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
			ResultSet result = getData("SELECT * FROM monsters WHERE id = "+id);
			
			if(result.next()) {
				int gfxID = result.getInt("gfxID");
				int align = result.getInt("align");
				String colors = result.getString("colors");
				String grades = result.getString("grades");
				String spells = result.getString("spells");
				String stats = result.getString("stats");
				String pdvs = result.getString("pdvs");
				String pts = result.getString("points");
				String inits = result.getString("inits");
				int mK = result.getInt("minKamas");
				int MK = result.getInt("maxKamas");
				int IAType = result.getInt("AI_Type");
				String xp = result.getString("exps");
				
				boolean capturable;
				
				if (result.getInt("capturable") == 1) 
					capturable = true;
				 else
					capturable = false;
				

				monster = new MobTemplate(id, gfxID, align,
						colors, grades, spells, stats, pdvs, pts, inits, mK,
						MK, xp, IAType, capturable);
				World.data.addMobTemplate(id, monster);
				World.database.getDropData().load(id);
			}
			
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.println("SQL ERROR(MonsterData): "+e.getMessage());
		}
		return monster;
	}
	
	public boolean saveNewFixGroup(int mapID, int cellID, String groupData) {
		try {
			String baseQuery = "REPLACE INTO `mobgroups_fix` VALUES(?,?,?)";
			PreparedStatement statement = connection.prepareStatement(baseQuery);

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
