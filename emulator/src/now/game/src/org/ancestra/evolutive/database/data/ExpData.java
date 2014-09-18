package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.other.ExpLevel;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ExpData extends AbstractDAO<ExpLevel>{

	public ExpData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.Experience");
	}

	@Override
	public boolean create(ExpLevel obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(ExpLevel obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(ExpLevel obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExpLevel load(int id) {
		ExpLevel exp = null;
		try {
			Result result = getData("SELECT * FROM experience WHERE lvl = "+id);
			exp = loadFromResultSet(result.resultSet);
			close(result);
            logger.debug("Level {} has been loaded",id);
		} catch (Exception e) {
			logger.error("Can't load level {}", id, e);
		}
		return exp;
	}
	
	public ArrayList<ExpLevel> loadAll() {
        ArrayList<ExpLevel> levels = null;
		try {
			Result result = getData("SELECT * FROM experience");
            ExpLevel current = null;
			while((current = loadFromResultSet(result.resultSet)) != null) {
                if(levels == null) levels = new ArrayList<>();
                levels.add(current);
            }
			close(result);
            logger.debug("Levels have been loaded");
		} catch (Exception e) {
			logger.error("Can't load levels ",e);
		}
        return levels;
	}

    protected ExpLevel loadFromResultSet(ResultSet resultSet) throws SQLException {
        ExpLevel expLevel = null;
        if(resultSet.next()) {
            expLevel = new ExpLevel(resultSet.getLong("perso"), resultSet.getInt("metier"),
                    resultSet.getInt("dinde"), resultSet.getInt("pvp"));
            World.data.addExpLevel(resultSet.getInt("lvl"),expLevel);
        }
        return expLevel;
    }

}
