package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.hdv.HDV;
import org.ancestra.evolutive.hdv.HDV.HdvEntry;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class HdvData extends AbstractDAO<HDV>{

	public HdvData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.HDV");
	}

	@Override
	public boolean create(HDV obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(HDV obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(HDV obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public HDV load(int id) {
		HDV hdv = null;
		try {
			Result result = getData("SELECT * FROM hdvs WHERE map = "+id);
			while((hdv = loadFromResultSet(result.resultSet))!=null);
			close(result);
			result = getData("SELECT id MAX FROM `hdvs`;");
			
			if (result.resultSet.first())
				World.data.setNextHdvID(result.resultSet.getInt("MAX"));
			
			close(result);

		} catch (Exception e) {
            logger.error("SQL ERROR(HdvData): " + e.getMessage());
		}
		return hdv;
	}
	
	public void loadHdvItems(int map) {
		try {
			Result result = getData("SELECT * FROM hdvs_items WHERE map = "+map+";");

			while (result.resultSet.next()) {
				HDV tempHdv = World.data.getHdv(result.resultSet.getInt("map"));
				if (tempHdv == null)
					continue;

				tempHdv.addEntry(new HDV.HdvEntry(result.resultSet.getInt("price"),
                        result.resultSet.getByte("count"), result.resultSet.getInt("ownerGuid"), World.data
						.getObjet(result.resultSet.getInt("itemID"))));
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HdvData): "+e.getMessage());
		}
	}
	
	public void updateHdvItems(int map) {
		try {
			String baseQuery = "INSERT INTO `hdvs_items` "
					+ "(`map`,`ownerGuid`,`price`,`count`,`itemID`) "
					+ "VALUES(?,?,?,?,?);";
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
			for (HdvEntry curEntry : World.data.getHdv(map).getAllEntry()) {
				if (curEntry.getOwner() == -1)
					continue;
				
				statement.setInt(1, curEntry.getHdvID());
				statement.setInt(2, curEntry.getOwner());
				statement.setInt(3, curEntry.getPrice());
				statement.setInt(4, curEntry.getAmount(false));
				statement.setInt(5, curEntry.getObjet().getGuid());
				
				statement.execute();
				World.database.getItemTemplateData().update(curEntry.getObjet().getTemplate());
			}
			close(statement);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HdvData): "+e.getMessage());
		}
	}

    protected HDV loadFromResultSet(ResultSet result) throws SQLException {
        HDV hdv = null;
        if(result.next()) {
            hdv = new HDV(result.getInt("map"),
                    result.getFloat("sellTaxe"), result.getShort("sellTime"),
                    result.getShort("accountItem"), result.getShort("lvlMax"),
                    result.getString("categories"));
            World.data.addHdv(hdv);
            loadHdvItems(hdv.getHdvID());
        }
        return hdv;
    }
}
