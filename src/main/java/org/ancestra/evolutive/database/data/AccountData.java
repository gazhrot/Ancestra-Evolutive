package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AccountData extends AbstractDAO<Account>{
	
	public AccountData(HikariDataSource source) {
		super(source);
		logger = (Logger)LoggerFactory.getLogger("factory.Account");
	}

	@Override
	public boolean create(Account obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Account obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Account obj) {
		try {
			String baseQuery = "UPDATE accounts SET " +
								"`bankKamas` = ?,"+
								"`bank` = ?,"+
								"`level` = ?,"+
								"`banned` = ?,"+
								"`friends` = ?,"+
								"`enemy` = ?,"+
								"`lastIP` = ?," +
								"`lastConnectionDate` = ?," +
								"`logged` = ?" +
								" WHERE `guid` = ?;";
			
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
			statement.setLong(1, obj.getBankKamas());
			statement.setString(2, obj.parseBankObjetsToDB());
			statement.setInt(3, obj.getGmLvl());
			statement.setInt(4, (obj.isBanned()?1:0));
			statement.setString(5, obj.parseFriendToDb());
			statement.setString(6, obj.parseEnemyToDb());
			statement.setString(7, obj.getCurIp());
			statement.setString(8, obj.getLastConnection());
			statement.setInt(9, obj.isLogged() ? 1:0);
			statement.setInt(10, obj.getUUID());
			execute(statement);
			return true;
		} catch(Exception e) {
			logger.error("SQL ERROR, trying rollback", e);
		}
		return false;
	}

    @Override
	public Account load(int id) {
        Account account = null;
		try {
			String query = "SELECT * FROM accounts WHERE guid = "+id;
			Result result = super.getData(query);
            account = loadFromResultSet(result.resultSet);
            close(result);
            if(account != null) {
                query = "UPDATE accounts SET reload_needed = 0 WHERE guid = " + id;
                super.execute(query);
            }
            logger.debug("Account with id {} successfully loaded",id);
        } catch(Exception e) {
            logger.error("Can't load account with guid" + id, e);
		}
		return account;
	}
	
	public Account load(String name) {
        Account account = null;
		try {
			String query = "SELECT * FROM accounts WHERE account = '"+name+"'";
			Result result = super.getData(query);
			account = loadFromResultSet(result.resultSet);
            close(result);
            if(account!= null) {
                query = "UPDATE accounts SET reload_needed = 0 WHERE account = '"+name+"'";
                super.execute(query);
            }
            logger.debug("Account with name {} successfully loaded",name);
        } catch(Exception e) {
			logger.error("Can't load account with name {}",name,e);
		}
		return account;
	}

    public void load(){
        try {
            String query = "SELECT * FROM accounts";
            Result result = super.getData(query);
            while(loadFromResultSet(result.resultSet) != null) {}
            close(result);
            query = "UPDATE accounts SET reload_needed = 0";
            super.execute(query);
            logger.debug("Accounts successfully loaded");
        } catch(Exception e) {
            logger.error("Can't load accounts ",e);
        }
    }
	
	public void updateState(boolean online) {
		int state = online ? 1 : 0;
		String baseQuery = "UPDATE accounts SET logged = "+state;
		
		super.execute(baseQuery);
	}
	
	public void updateState(Account account, boolean online) { 
		int state = online ? 1 : 0;
		String baseQuery = "UPDATE accounts SET logged = "+state+" WHERE account = '"+account.getName()+"';";
		execute(baseQuery);
	}

    protected Account loadFromResultSet(ResultSet resultSet) throws SQLException {
        if(resultSet.next()) {
            Account account = new Account(
                    resultSet.getInt("guid"),
                    resultSet.getString("account").toLowerCase(),
                    resultSet.getString("pass"),
                    resultSet.getString("pseudo"),
                    resultSet.getString("question"),
                    resultSet.getString("reponse"),
                    resultSet.getInt("level"),
                    resultSet.getInt("vip"),
                    resultSet.getInt("banned") == 1,
                    resultSet.getString("lastIP"),
                    resultSet.getString("lastConnectionDate"),
                    resultSet.getString("bank"),
                    resultSet.getInt("bankKamas"),
                    resultSet.getString("friends"),
                    resultSet.getString("enemy"),
                    resultSet.getInt("logged") == 1);
            World.data.addAccount(account);
            World.database.getCharacterData().load(account);
            return account;
        }
        return null;
    }
}
