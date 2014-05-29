package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.object.Objet.ObjTemplate;
import org.ancestra.evolutive.other.Action;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;




public class ItemTemplateData extends AbstractDAO<ObjTemplate>{

	public ItemTemplateData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("Item Template factory");
	}

	@Override
	public boolean create(ObjTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(ObjTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(ObjTemplate obj) {
		try {
			String baseQuery = "UPDATE `item_template`"
					+ " SET sold = ?, avgPrice = ?" + " WHERE id = ?";
			PreparedStatement statement = getPreparedStatement(baseQuery);

			statement.setLong(1, obj.getSold());
			statement.setInt(2, obj.getAvgPrice());
			statement.setInt(3, obj.getID());
			
			execute(statement);
			
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(ItemTemplateData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public ObjTemplate load(int id) {
		ObjTemplate template = null;
		try {
			Result result = getData("SELECT * FROM item_template WHERE id = "+id);
			
			if(result.resultSet.next()) {
				template = new ObjTemplate(result.resultSet.getInt("id"), result.resultSet
						.getString("statsTemplate"), result.resultSet.getString("name"), result.resultSet
						.getInt("type"), result.resultSet.getInt("level"), result.resultSet.getInt("pod"),
						result.resultSet.getInt("prix"), result.resultSet.getInt("panoplie"), result.resultSet
								.getString("condition"), result.resultSet
								.getString("armesInfos"), result.resultSet.getInt("sold"), result.resultSet
								.getInt("avgPrice"));
				World.data.addObjTemplate(template);
				loadUseAction(id);
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(ItemTemplateData): "+e.getMessage());
		}
		return template;
	}
	
	public void loadUseAction(int item) {
		try {
			Result result = getData("SELECT * FROM use_item_actions WHERE template = "+item);
			while (result.resultSet.next()) {
				int id = result.resultSet.getInt("template");
				int type = result.resultSet.getInt("type");
				String args = result.resultSet.getString("args");
				if (World.data.getObjTemplate(id) == null)
					continue;
				World.data.getObjTemplate(id).addAction(
						new Action(type, args, ""));
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(ItemTemplateData): "+e.getMessage());
		}
	}
}
