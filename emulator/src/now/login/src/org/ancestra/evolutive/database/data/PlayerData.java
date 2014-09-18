package org.ancestra.evolutive.database.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.object.Account;
import org.ancestra.evolutive.object.Player;
import org.ancestra.evolutive.object.Server;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.zaxxer.hikari.HikariDataSource;

public class PlayerData extends AbstractDAO<Player> {

	public PlayerData(HikariDataSource dataSource) {
		super(dataSource);
		logger = (Logger) LoggerFactory.getLogger("factory.player");
	}

	public Player load(Object obj) {
		try {
			if(obj instanceof Account) {
				Account account = (Account) obj;
				Result result = getData("SELECT * FROM players WHERE account = " + account.getUUID());
				ResultSet resultSet = result.resultSet;
	            while(resultSet.next()) {
	            	account.addPlayer(new Player(resultSet.getInt("id"),
	                		resultSet.getInt("server")));
	            }
	            close(result);
	            logger.info("Players load for account {}", account.getUUID());
			}
        }
        catch(Exception e) {
			logger.error("Can't load players for account {}", ((Account) obj).getUUID(),e);
		}
		return null;
	}

	@Override
	public boolean update(Player obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Map<Server, ArrayList<Integer>> loadAllPlayersByAccountId(int notServer, int account) {
		Map<Server, ArrayList<Integer>> maps = new HashMap<>();
		try {
			Result result = getData("SELECT id,server FROM players WHERE account = '"+account+"' AND NOT server = '" + notServer + "';");
			ResultSet resultSet = result.resultSet;
			
			while(resultSet.next()) {
				Server server = Server.servers.get(resultSet.getInt("server"));
				int guid = resultSet.getInt("id");
				
				if(maps.get(server) == null) {
					ArrayList<Integer> array = new ArrayList<Integer>();
					array.add(guid);	
					maps.put(server, array);
				} else {
					maps.get(server).add(guid);
				}
			}
			
			close(result);
			logger.info("Players load for account {}", account);
		} catch(SQLException e) {
			logger.error("Can't load players for account {}", account,e);
		}
		return maps;
	}
	
	public int isLogged(Account account) {
		int logged = 0;
		try {
			Result result = getData("SELECT * FROM players WHERE account = " + account.getUUID());
			ResultSet resultSet = result.resultSet;
			while(resultSet.next()) {
				if(resultSet.getInt("logged") == 1)
					logged = resultSet.getInt("server");
			}
			close(result);
			logger.info("Players load for account {}", account.getUUID());
        }
        catch(Exception e) {
			logger.error("Can't load players for account {}", account.getUUID(),e);
		}
		return logged;
	}
}
