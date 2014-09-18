package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.entity.creature.npc.NpcAnswer;
import org.ancestra.evolutive.other.Action;
import org.slf4j.LoggerFactory;

public class NpcAnswerData extends AbstractDAO<NpcAnswer>{

	public NpcAnswerData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.NPCAnswer");
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
			Result result = getData("SELECT * FROM npc_reponses_actions WHERE ID = "+id);
			if(result.resultSet.next()) {
				int type = result.resultSet.getInt("type");
				String args = result.resultSet.getString("args");
				
				answer = new NpcAnswer(id);
				answer.addAction(new Action(type, args, ""));
				World.data.addNpcAnswer(answer);
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(NpcAnswerData): "+e.getMessage());
		}
		return answer;
	}
}
