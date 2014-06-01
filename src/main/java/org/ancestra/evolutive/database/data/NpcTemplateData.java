package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.npc.NpcTemplate;
import org.slf4j.LoggerFactory;

public class NpcTemplateData extends AbstractDAO<NpcTemplate>{

	public NpcTemplateData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("NPC Template factory");
	}

	@Override
	public boolean create(NpcTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(NpcTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(NpcTemplate obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NpcTemplate load(int id) {
		NpcTemplate template = null;
		try {
			Result result = getData("SELECT * FROM npc_template WHERE id = "+id);
			if(result.resultSet.next()) {
				int bonusValue = result.resultSet.getInt("bonusValue");
				int gfxID = result.resultSet.getInt("gfxID");
				int scaleX = result.resultSet.getInt("scaleX");
				int scaleY = result.resultSet.getInt("scaleY");
				int sex = result.resultSet.getInt("sex");
				int color1 = result.resultSet.getInt("color1");
				int color2 = result.resultSet.getInt("color2");
				int color3 = result.resultSet.getInt("color3");
				String access = result.resultSet.getString("accessories");
				int extraClip = result.resultSet.getInt("extraClip");
				int customArtWork = result.resultSet.getInt("customArtWork");
				int initQId = result.resultSet.getInt("initQuestion");
				String ventes = result.resultSet.getString("ventes");
				template = new NpcTemplate(id, bonusValue, gfxID,
						scaleX, scaleY, sex, color1, color2, color3, access,
						extraClip, customArtWork, initQId, ventes);
				World.data.addNpcTemplate(template);
						
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(NpcTemplateData): "+e.getMessage());
		}
		return template;
	}
}
