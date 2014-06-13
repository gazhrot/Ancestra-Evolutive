package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.monster.MobTemplate;
import org.ancestra.evolutive.other.Drop;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DropData extends AbstractDAO<ArrayList<Drop>>{

	public DropData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.Drop");
	}

    @Override
    public boolean create(ArrayList<Drop> obj) {
        return false;
    }

    @Override
    public boolean delete(ArrayList<Drop> obj) {
        return false;
    }

    @Override
    public boolean update(ArrayList<Drop> obj) {
        return false;
    }

    @Override
	public ArrayList<Drop> load(int mob) {
		ArrayList<Drop> drops = null;
		try {
			Result result = getData("SELECT * FROM drops WHERE mob = "+mob);
			drops = loadFromResultSet(result.resultSet);
			close(result);
            logger.debug("Drops from mob {} have been loaded",mob);
		} catch (Exception e) {
			logger.error("Can't load drops from {}",mob,e);
		}
		return drops;
	}

    protected ArrayList<Drop> loadFromResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<Drop> drops = new ArrayList<>();
        while (resultSet.next()) {
            MobTemplate MT = World.data.getMonstre(resultSet.getInt("mob"));
            Drop drop = new Drop(resultSet.getInt("item"), resultSet.getInt("seuil"), resultSet
                    .getFloat("taux"), resultSet.getInt("max"));
            MT.getDrops().add(drop);
            drops.add(drop);
        }
        return drops;
    }

}
