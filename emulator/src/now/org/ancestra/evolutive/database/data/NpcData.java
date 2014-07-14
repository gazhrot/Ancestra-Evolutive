package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.npc.Npc;
import org.ancestra.evolutive.entity.npc.NpcTemplate;
import org.ancestra.evolutive.map.Maps;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;

public class NpcData extends AbstractDAO<Npc>{

	public NpcData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.NPC");
	}

	@Override
	public boolean create(Npc obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean create(int mapid, int npcid, int cellid, int orientation) {
		String baseQuery = "INSERT INTO `npcs`" + " VALUES (?,?,?,?);";
		try {
			PreparedStatement statement = getPreparedStatement(baseQuery);
			statement.setInt(1, mapid);
			statement.setInt(2, npcid);
			statement.setInt(3, cellid);
			statement.setInt(4, orientation);

			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(NpcData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public boolean delete(Npc obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean delete(int mapid, int cellid) {
		String baseQuery = "DELETE FROM npcs WHERE mapid = "+mapid+" AND cellid = "+cellid;
		execute(baseQuery);
		return true;
	}

	@Override
	public boolean update(Npc obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Npc load(int mapId) {
		Npc npc = null;
        Maps map = World.data.getMap(mapId);
        if(map == null)
            return null;
		try {
			Result result = getData("SELECT * FROM npcs WHERE mapid = "+mapId);
			while(result.resultSet.next()) {
                int templateId = result.resultSet.getInt("npcid");
                NpcTemplate template = World.data.getNpcTemplate(templateId);
                if(template == null){
                    logger.error("Impossible de creer le pnj avec le template {} car le template n existe pas",templateId);
                    break;
                }
                npc = new Npc(template,map,
                        map.getCases().get(result.resultSet.getInt("cellid")),
                        (byte)result.resultSet.getInt("orientation"));
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(NpcData): "+e.getMessage());
		}
		return npc;
	}
}
