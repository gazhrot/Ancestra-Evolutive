package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.fight.spell.Animation;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;


public class AnimationData extends AbstractDAO<Animation> {

	public AnimationData(HikariDataSource source) {
		super(source);
		logger = (Logger)LoggerFactory.getLogger("Animation factory");
	}

	@Override
	public boolean create(Animation obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Animation obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Animation obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Animation load(int id) {
		Animation animation = null;
		try {
			Result result = getData("SELECT * FROM animations WHERE guid = "+id);
			animation = loadFromResultSet(result.resultSet);
			close(result);
            logger.debug("Animation id {} successfully loaded",id);
		} catch(Exception e) {
			logger.error("Can't load animation with id {}",id,e);
		}
		return animation;
	}

    public void load(){
        try {
            Result result = getData("SELECT * FROM animations");
            while(loadFromResultSet(result.resultSet) != null);
            close(result);
            logger.debug("Animation successfully loaded");
        } catch(Exception e) {
            logger.error("Can't load animation",e);
        }
    }

    protected Animation loadFromResultSet(ResultSet resultSet) throws SQLException {
        if(resultSet.next()) {
            Animation animation = new Animation(
                    resultSet.getInt("guid"),
                    resultSet.getInt("id"),
                    resultSet.getString("nom"),
                    resultSet.getInt("area"),
                    resultSet.getInt("action"),
                    resultSet.getInt("size"));
            World.data.addAnimation(animation);
            return animation;
        }
        return null;
    }
}
