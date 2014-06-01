package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.job.Job;
import org.slf4j.LoggerFactory;

public class JobData extends AbstractDAO<Job>{

	public JobData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("Job factory");
	}

	@Override
	public boolean create(Job obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Job obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Job obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Job load(int id) {
		Job job = null;
		try {
			Result result = getData("SELECT * FROM jobs_data WHERE id = "+id);
			
			if(result.resultSet.next()) {
				job = new Job(result.resultSet.getInt("id"), result.resultSet
						.getString("tools"), result.resultSet.getString("crafts"));
				World.data.addJob(job);
			}
			close(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(JobData): "+e.getMessage());
		}
		return job;
	}
}
