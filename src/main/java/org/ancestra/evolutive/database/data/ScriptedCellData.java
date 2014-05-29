package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.map.Case;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ScriptedCellData extends AbstractDAO<Case>{

	public ScriptedCellData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("ScriptedCell factory");
	}

	@Override
	public boolean create(Case obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Case obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean delete(int mapid, int cellid) {
		String baseQuery = "DELETE FROM `scripted_cells` WHERE `MapID` = "+mapid+" AND `CellID` = "+cellid;
		execute(baseQuery);
		return true;
	}

	@Override
	public boolean update(Case obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean update(int mapID1, int cellID1, int action, int event, String args, String cond) {
		try {
			String baseQuery = "REPLACE INTO `scripted_cells` VALUES (?,?,?,?,?,?);";
			PreparedStatement statement = getPreparedStatement(baseQuery);
			statement.setInt(1, mapID1);
			statement.setInt(2, cellID1);
			statement.setInt(3, action);
			statement.setInt(4, event);
			statement.setString(5, args);
			statement.setString(6, cond);

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(CellData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public Case load(int mapid) {
		//retourne null dans tous les cas
		Case cell = null;
		try {
			Result result = getData("SELECT * FROM scripted_cells WHERE mapid = "+mapid);
			while (result.resultSet.next()) {
				if (World.data.getCarte(result.resultSet.getShort("MapID")) == null)
					continue;
				if (World.data.getCarte(result.resultSet.getShort("MapID")).getCases().get(
						result.resultSet.getInt("CellID")) == null)
					continue;

				if(result.resultSet.getInt("EventID") == 1) {
					World.data.getCarte(result.resultSet.getShort("MapID"))
							.getCases().get(result.resultSet.getInt("CellID"))
							.addOnCellStopAction(result.resultSet.getInt("ActionID"),
							result.resultSet.getString("ActionsArgs"),
							result.resultSet.getString("Conditions"));
				}
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(CellData): "+e.getMessage());
		}
		return cell;
	}
}
