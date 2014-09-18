package org.ancestra.evolutive.database.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.object.Account;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.zaxxer.hikari.HikariDataSource;

public class AccountData extends AbstractDAO<Account>{
	
	public AccountData(HikariDataSource source) {
		super(source);
		logger = (Logger)LoggerFactory.getLogger("factory.Account");
	}

	@Override
	public Account load(Object id) {
		Account account = null;
		try {
			String query = "SELECT * FROM accounts WHERE guid = " + id;
			Result result = super.getData(query); 
            account = loadFromResultSet(result.resultSet);
            close(result);
            if(account != null) {
                query = "UPDATE accounts SET reload_needed = 0 WHERE guid = " + id;
                super.execute(query);
            }
            logger.debug("Account with id {} successfully loaded", id);
        } catch(Exception e) {
            logger.error("Can't load account with guid" + id, e);
		}
		return account;
	}
	
	public Account load(String name) {
		Account account = null;
		try {
			String query = "SELECT * FROM accounts WHERE account = '" + name + "';";
			Result result = super.getData(query);
            account = loadFromResultSet(result.resultSet);
            close(result);
            if(account != null) {
                query = "UPDATE accounts SET reload_needed = 0 WHERE guid = " + account.getUUID();
                super.execute(query);
            }
            logger.debug("Account with id {} successfully loaded", account.getUUID());
        } catch(Exception e) {
            logger.error("Can't load account with guid" + account.getUUID(), e);
		}
		return account;
	}

	@Override
	public boolean update(Account obj) {
		try {
			String baseQuery = "UPDATE accounts SET" +
					" account = '" + obj.getName() + "'," +
					" pass = '" + obj.getPass() + "'," +
					" pseudo = '" + obj.getPseudo() + "'," +
					" question = '" + obj.getQuestion() + "'," +
					" level = '" + obj.getRank() + "'," +
					" logged = '" + obj.getState() + "'," +
					" subscribe = '" + obj.getSubscribe() + "'" +
					" WHERE guid = " + obj.getUUID();
			
			PreparedStatement statement = getPreparedStatement(baseQuery);
			execute(statement);

			return true;
		} catch(Exception e) {
			logger.error("SQL ERROR, trying rollback", e);
		}
		return false;
	}
	
	public boolean update() {
		try {
			String baseQuery = "UPDATE accounts SET logged = '0';";
								
			PreparedStatement statement = getPreparedStatement(baseQuery);
			execute(statement);
			
			return true;
		} catch(Exception e) {
			logger.error("SQL ERROR, trying rollback", e);
		}
		return false;
	}
	
	public String exist(String nickname) {
		String name = null;
		try {
			String query = "SELECT * FROM accounts WHERE pseudo = '" + nickname + "';";
			Result result = super.getData(query);
            if(result.resultSet.next())
            	name = result.resultSet.getString("account");
            close(result);
            logger.debug("Account with pseudo {} exist", nickname);
        } catch(Exception e) {
            logger.error("Can't load account with pseudo like {}" + nickname, e);
		}
		return name;
	}
	
	public void resetLogged(int server) {
		try {
			String query = "SELECT * FROM players WHERE server = '" + server + "';";
			Result result = super.getData(query);
			ResultSet resultSet = result.resultSet;
			
			while(resultSet.next()) {
				String baseQuery = "UPDATE accounts SET" +
						" logged = '0'" +
						" WHERE guid = " + resultSet.getInt("account");
				
				PreparedStatement statement = getPreparedStatement(baseQuery);
				execute(statement);
			}
			
			String baseQuery = "UPDATE players SET" +
					" logged = '0'" +
					" WHERE server = " + server;
			
			PreparedStatement statement = getPreparedStatement(baseQuery);
			execute(statement);

		} catch(Exception e) {
			logger.error("SQL ERROR, trying rollback", e);
		}
	}
	
	public boolean isBanned(String ip) {
		boolean banned = false;
		try {
			String query = "SELECT * FROM banip WHERE 'ip' LIKE '" + ip + "';";
			Result result = super.getData(query);
            ResultSet resultSet = result.resultSet;
            
            if(resultSet.next())
            	banned = true;
            
            close(result);
        } catch(Exception e) {
            logger.error("Can't know if ip {} is banned", ip);
		}
		return banned;
	}

	protected Account loadFromResultSet(ResultSet resultSet) throws SQLException {
        if(resultSet.next()) {
            Account account = new Account(
                    resultSet.getInt("guid"),
                    resultSet.getString("account").toLowerCase(),
                    resultSet.getString("pass"),
                    resultSet.getString("pseudo"),
                    resultSet.getString("question"),
                    resultSet.getByte("level"),
                    resultSet.getByte("logged"),
                    resultSet.getLong("subscribe"),
                    resultSet.getByte("banned"));
            return account;
        }
        return null;
    }
}
