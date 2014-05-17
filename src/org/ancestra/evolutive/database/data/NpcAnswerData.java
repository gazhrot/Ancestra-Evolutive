package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.npc.NpcAnswer;
import org.ancestra.evolutive.other.Action;

public class NpcAnswerData extends AbstractDAO<NpcAnswer>{

	public NpcAnswerData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(NpcAnswer obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(NpcAnswer obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(NpcAnswer obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NpcAnswer load(int id) {
		NpcAnswer answer = null;
		try {
			ResultSet result = getData("SELECT * FROM npc_reponses_actions WHERE ID = "+id);
			if(result.next()) {
				int type = result.getInt("type");
				String args = result.getString("args");
				
				answer = new NpcAnswer(id);
				answer.addAction(new Action(type, args, ""));
				World.data.addNpcAnswer(answer);
			}
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(NpcAnswerData): "+e.getMessage());
		}
		return answer;
	}
}
