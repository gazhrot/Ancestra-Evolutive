package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.npc.NpcTemplate;




public class NpcTemplateData extends AbstractDAO<NpcTemplate>{

	public NpcTemplateData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
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
			ResultSet result = getData("SELECT * FROM npc_template WHERE id = "+id);
			if(result.next()) {
				int bonusValue = result.getInt("bonusValue");
				int gfxID = result.getInt("gfxID");
				int scaleX = result.getInt("scaleX");
				int scaleY = result.getInt("scaleY");
				int sex = result.getInt("sex");
				int color1 = result.getInt("color1");
				int color2 = result.getInt("color2");
				int color3 = result.getInt("color3");
				String access = result.getString("accessories");
				int extraClip = result.getInt("extraClip");
				int customArtWork = result.getInt("customArtWork");
				int initQId = result.getInt("initQuestion");
				String ventes = result.getString("ventes");
				template = new NpcTemplate(id, bonusValue, gfxID,
						scaleX, scaleY, sex, color1, color2, color3, access,
						extraClip, customArtWork, initQId, ventes);
				World.data.addNpcTemplate(template);
						
			}
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(NpcTemplateData): "+e.getMessage());
		}
		return template;
	}
}
