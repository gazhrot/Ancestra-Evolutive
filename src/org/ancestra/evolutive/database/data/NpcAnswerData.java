package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.objects.Action;
import org.ancestra.evolutive.objects.NPC_tmpl.NPC_reponse;




public class NpcAnswerData extends AbstractDAO<NPC_reponse>{

	public NpcAnswerData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(NPC_reponse obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(NPC_reponse obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(NPC_reponse obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NPC_reponse load(int id) {
		NPC_reponse answer = null;
		try {
			ResultSet result = getData("SELECT * FROM npc_reponses_actions WHERE ID = "+id);
			if(result.next()) {
				int type = result.getInt("type");
				String args = result.getString("args");
				
				answer = new NPC_reponse(id);
				answer.addAction(new Action(type, args, ""));
				World.data.addNPCreponse(answer);
			}
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(NpcAnswerData): "+e.getMessage());
		}
		return answer;
	}
}
