package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.area.SubArea;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AreaSubData extends AbstractDAO<SubArea> {

	public AreaSubData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("subarea factory");
	}

	@Override
	public boolean create(SubArea obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(SubArea obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(SubArea obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SubArea load(int id) {
		SubArea subArea = null;
		try {
			Result result = super.getData("SELECT * FROM subarea_data WHERE id = "+id);
			subArea = loadFromResultSet(result.resultSet);
			close(result);
            logger.debug("SubArea {} has been loaded",id);
		} catch(Exception e) {
			logger.error("Can't load subarea {} ",e);
		}
		return subArea;
	}

    public void load() {
        try {
            Result result = super.getData("SELECT * FROM subarea_data");
            while(loadFromResultSet(result.resultSet)!=null);
            close(result);
            logger.debug("SubAreas has been loaded");
        } catch(Exception e) {
            logger.error("Can't load subarea {} ",e);
        }
    }

    public SubArea loadFromResultSet(ResultSet resultSet) throws SQLException {
        SubArea subArea = null;
        if(resultSet.next()) {
            subArea = new SubArea(resultSet.getInt("id"),
                    resultSet.getInt("area"),
                    resultSet.getInt("alignement"),
                    resultSet.getString("name"));
            World.data.addSubArea(subArea);
            //on ajoute la sous zone a la zone
            if(subArea.getArea() != null)
                subArea.getArea().addSubArea(subArea);
        }
        return subArea;
    }
}
