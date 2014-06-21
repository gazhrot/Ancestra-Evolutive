package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.area.Area;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AreaData extends AbstractDAO<Area>{

	public AreaData(HikariDataSource source) {
		super(source);
		logger = (Logger) LoggerFactory.getLogger("factory.Area");
	}

	@Override
	public boolean create(Area obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Area obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Area obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Area load(int id) {
		Area area = null;
		try {
			Result result = super.getData("SELECT * FROM area_data WHERE id = "+id);
			area = loadFromResultSet(result.resultSet);
            close(result);
			logger.debug("Area {} successfully loaded",id);
		} catch(Exception e) {
			logger.error("Can't load area {}", id, e);
		}
		return area;
	}

    public void load() {
        try {
            Result result = super.getData("SELECT * FROM area_data");
            while(loadFromResultSet(result.resultSet)!=null);
            close(result);
            logger.debug("Areas successfully loaded");
        } catch(Exception e) {
            logger.error("Can't load areas",e);
        }
    }

    protected Area loadFromResultSet(ResultSet resultSet) throws SQLException {
        Area area = null;
        if(resultSet.next()) {
            area = new Area(resultSet.getInt("id"), resultSet.getInt("superarea"), resultSet.getString("name"));
            World.data.addArea(area);
            area.getContinent().addArea(area);
        }
        return area;
    }
}
