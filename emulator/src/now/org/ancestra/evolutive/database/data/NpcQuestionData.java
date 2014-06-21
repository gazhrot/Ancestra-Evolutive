package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.npc.NpcQuestion;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class NpcQuestionData extends AbstractDAO<NpcQuestion>{

	public NpcQuestionData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.NPCQuestion");
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
			Result result = getData("SELECT * FROM npc_questions WHERE ID = "+id);
			ResultSet statement = result.resultSet;
			if(statement.next()) {
				question = new NpcQuestion(statement.getInt("ID"), statement
						.getString("responses"), statement.getString("params"), statement
						.getString("cond"), statement.getInt("ifFalse"));
				World.data.addNpcQuestion(question);
			}
			close(result);
		} catch (Exception e) {
			Console.instance.println("SQL ERROR(NpcQuestionData): "+e.getMessage());
		}
		return question;
	}
}
