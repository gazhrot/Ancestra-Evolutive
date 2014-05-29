package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.fight.spell.Spell;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpellData extends AbstractDAO<Spell>{

	public SpellData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("Spell factory");
	}

	@Override
	public boolean create(Spell obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Spell obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Spell obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Spell load(int id) {
		Spell sort = null;
		try {
			String query = "SELECT * FROM sorts WHERE id = "+id; //TODO
			Result result = getData(query);
			sort = loadFromResultSet(result.resultSet);
			close(result);
            logger.debug("Spell {} has been loaded",id);
		} catch (Exception e) {
			logger.error("Can't load spell {}",id,e);
		}
		return sort;
	}

    protected Spell loadFromResultSet(ResultSet resultSet) throws SQLException {
        Spell sort = null;
        if(resultSet.next()) {
            sort = new Spell(resultSet.getInt("id"), resultSet.getInt("sprite"),
                    resultSet.getString("spriteInfos"),
                    resultSet.getString("effectTarget"));
            SpellStats l1 = sort.parseSpellStats(sort.getSpellID(), 1, resultSet.getString("lvl1"));
            SpellStats l2 = sort.parseSpellStats(sort.getSpellID(), 2, resultSet.getString("lvl2"));
            SpellStats l3 = sort.parseSpellStats(sort.getSpellID(), 3, resultSet.getString("lvl3"));
            SpellStats l4 = sort.parseSpellStats(sort.getSpellID(), 4, resultSet.getString("lvl4"));
            SpellStats l5 = null;
            if (!resultSet.getString("lvl5").equalsIgnoreCase("-1"))
                l5 = sort.parseSpellStats(sort.getSpellID(), 5, resultSet.getString("lvl5"));
            SpellStats l6 = null;
            if (!resultSet.getString("lvl6").equalsIgnoreCase("-1"))
                l6 = sort.parseSpellStats(sort.getSpellID(), 6, resultSet.getString("lvl6"));
            sort.addSpellStats(1, l1);
            sort.addSpellStats(2, l2);
            sort.addSpellStats(3, l3);
            sort.addSpellStats(4, l4);
            sort.addSpellStats(5, l5);
            sort.addSpellStats(6, l6);
            World.data.addSort(sort);
        }
        return sort;
    }
}
