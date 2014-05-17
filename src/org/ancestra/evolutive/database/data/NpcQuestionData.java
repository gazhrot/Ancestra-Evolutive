package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.npc.NpcQuestion;

public class NpcQuestionData extends AbstractDAO<NpcQuestion>{

	public NpcQuestionData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(NpcQuestion obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(NpcQuestion obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(NpcQuestion obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NpcQuestion load(int id) {
		NpcQuestion question = null;
		try {
			ResultSet statement = getData("SELECT * FROM npc_questions WHERE ID = "+id);
			
			if(statement.next()) {
				question = new NpcQuestion(statement.getInt("ID"), statement
						.getString("responses"), statement.getString("params"), statement
						.getString("cond"), statement.getInt("ifFalse"));
				World.data.addNpcQuestion(question);
			}
			closeResultSet(statement);
		} catch (Exception e) {
			Console.instance.println("SQL ERROR(NpcQuestionData): "+e.getMessage());
		}
		return question;
	}
}
