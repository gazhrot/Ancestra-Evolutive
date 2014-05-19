package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.hdv.Hdv;
import org.ancestra.evolutive.hdv.HdvEntry;

public class HdvData extends AbstractDAO<Hdv>{

	public HdvData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Hdv obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Hdv obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Hdv obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Hdv load(int id) {
		Hdv hdv = null;
		try {
			ResultSet result = getData("SELECT * FROM hdvs WHERE map = "+id);

			while(result.next()) {
				hdv = new Hdv(result.getInt("map"), result
						.getFloat("sellTaxe"), result.getShort("sellTime"), result
						.getShort("accountItem"), result.getShort("lvlMax"), result
						.getString("categories"));
				World.data.addHdv(hdv);
				loadHdvItems(id);
			}
			closeResultSet(result);

			result = getData("SELECT id MAX FROM `hdvs`");
			
			if (result.first())
				World.data.setNextHdvID(result.getInt("MAX"));
			
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HdvData): "+e.getMessage());
		}
		return hdv;
	}
	
	public void loadHdvItems(int map) {
		try {
			ResultSet result = getData("SELECT * FROM hdvs_items WHERE map = "+map);

			while (result.next()) {
				Hdv tempHdv = World.data.getHdv(result.getInt("map"));
				if (tempHdv == null)
					continue;

				tempHdv.addEntry(new HdvEntry(result.getInt("price"), result
						.getByte("count"), result.getInt("ownerGuid"), World.data
						.getObjet(result.getInt("itemID"))));
			}
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HdvData): "+e.getMessage());
		}
	}
	
	public void updateHdvItems(int map) {
		try {
			String baseQuery = "INSERT INTO `hdvs_items` "
					+ "(`map`,`ownerGuid`,`price`,`count`,`itemID`) "
					+ "VALUES(?,?,?,?,?);";
			PreparedStatement statement = connection.prepareStatement(baseQuery);
			
			for (HdvEntry curEntry : World.data.getHdv(map).getAllEntry()) {
				if (curEntry.getOwner() == -1)
					continue;
				
				statement.setInt(1, curEntry.getHdv());
				statement.setInt(2, curEntry.getOwner());
				statement.setInt(3, curEntry.getPrice());
				statement.setInt(4, curEntry.getAmount(false));
				statement.setInt(5, curEntry.getObject().getId());
				
				statement.execute();
				World.database.getItemTemplateData().update(curEntry.getObject().getTemplate());
			}
			closeStatement(statement);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(HdvData): "+e.getMessage());
		}
	}
}
