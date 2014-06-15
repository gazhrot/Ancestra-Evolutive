package org.ancestra.evolutive.database.data;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.guild.GuildMember;
import org.ancestra.evolutive.object.Objet;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

public class CharacterData extends AbstractDAO<Player>{

	public CharacterData(HikariDataSource source) {
		super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.Character");
	}

	@Override
	public boolean create(Player obj) {
		String baseQuery = "INSERT INTO personnages( `guid` ," +
				" `name` , `sexe` , `class` , `color1` , `color2` , `color3` , `kamas` ," +
				" `spellboost` , `capital` , `energy` , `level` , `xp` , `size` , `gfx` ," +
				" `account`,`cell`,`map`,`spells`,`objets`, `storeObjets`)" +
				" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'', '');";
		
		try {
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
			statement.setInt(1,obj.getId());
			statement.setString(2, obj.getName());
			statement.setInt(3,obj.getSex());
			statement.setInt(4,obj.getClasse().getId());
			statement.setInt(5,obj.getColor1());
			statement.setInt(6,obj.getColor2());
			statement.setInt(7,obj.getColor3());
			statement.setLong(8,obj.getKamas());
			statement.setInt(9,obj.getSpellPoints());
			statement.setInt(10,obj.getCapital());
			statement.setInt(11,obj.getEnergy());
			statement.setInt(12,obj.getLevel());
			statement.setLong(13,obj.getExperience());
			statement.setInt(14,obj.getSize());
			statement.setInt(15,obj.getGfx());
			statement.setInt(16,obj.getAccount().getUUID());
			statement.setInt(17,obj.getCell().getId());
			statement.setInt(18,obj.getMap().getId());
			statement.setString(19, obj.parseSpellsToDb());
            execute(statement);
            logger.info("A new player has been created named {}",obj.getName());
			return true;
		} catch (Exception e) {
			logger.error("Can't create player ",e);
		}
		return false;
	}

	@Override
	public boolean delete(Player obj) {
		try {
			String baseQuery = "DELETE FROM personnages WHERE guid = "+obj.getId();
			execute(baseQuery);
			logger.debug("Player {} has been deleted",obj.getName());
			if(!obj.getItemsIDSplitByChar(",").equals("")) {
				baseQuery = "DELETE FROM items WHERE guid IN ('"+obj.getItemsIDSplitByChar(",")+"');";
				execute(baseQuery);
                logger.debug("Object from {} has been deleted",obj.getName());
			}
			if(!obj.getStoreItemsIDSplitByChar(",").equals("")) {
				baseQuery = "DELETE FROM items WHERE guid IN ('"+obj.getStoreItemsIDSplitByChar(",")+"');";
				execute(baseQuery);
                logger.debug("Objects stored by {} has been deleted",obj.getName());
			}
			if(obj.getMount() != null) {
				baseQuery = "DELETE FROM mounts_data WHERE id = "+obj.getMount().getId();
				execute(baseQuery);
				World.data.delDragoByID(obj.getMount().getId());
                logger.debug("Mount of {} has been deleted",obj.getName());
			}
			return true;
		} catch (Exception e) {
			logger.error("Can't complete erase of {}",obj.getName(),e);
		}
		return false;
	}

	@Override
	public boolean update(Player obj)  {
		try {
			String baseQuery = "UPDATE `personnages` SET `kamas`= ?,`spellboost`= ?,"+
							"`capital`= ?, `energy`= ?, `level`= ?, `xp`= ?, `size` = ?," +
							"`gfx`= ?,`alignement`= ?,`honor`= ?,`deshonor`= ?,`alvl`= ?,"+
							"`vitalite`= ?,`force`= ?,`sagesse`= ?,`intelligence`= ?,"+
							"`chance`= ?,`agilite`= ?,`seeSpell`= ?,`seeFriend`= ?,"+
							"`seeAlign`= ?,`seeSeller`= ?,`canaux`= ?,`map`= ?,"+
							"`cell`= ?,`pdvper`= ?,`spells`= ?,`objets`= ?,`storeObjets`= ?,"+
							"`savepos`= ?,`zaaps`= ?,`jobs`= ?,`mountxpgive`= ?,`mount`= ?,"+
							"`title`= ?,`wife`= ?"+
							" WHERE `personnages`.`guid` = ? LIMIT 1 ;";
			
			PreparedStatement statement = getPreparedStatement(baseQuery);
			
			statement.setLong(1,obj.getKamas());
			statement.setInt(2,obj.getSpellPoints());
			statement.setInt(3,obj.getCapital());
			statement.setInt(4,obj.getEnergy());
			statement.setInt(5,obj.getLevel());
			statement.setLong(6,obj.getExperience());
			statement.setInt(7,obj.getSize());
			statement.setInt(8,obj.getGfx());
			statement.setInt(9,obj.getAlign());
			statement.setInt(10,obj.getHonor());
			statement.setInt(11,obj.getDeshonor());
			statement.setInt(12,obj.getaLvl());
			statement.setInt(13,obj.getStats().getEffect(Constants.STATS_ADD_VITA));
			statement.setInt(14,obj.getStats().getEffect(Constants.STATS_ADD_FORC));
			statement.setInt(15,obj.getStats().getEffect(Constants.STATS_ADD_SAGE));
			statement.setInt(16,obj.getStats().getEffect(Constants.STATS_ADD_INTE));
			statement.setInt(17,obj.getStats().getEffect(Constants.STATS_ADD_CHAN));
			statement.setInt(18,obj.getStats().getEffect(Constants.STATS_ADD_AGIL));
			statement.setInt(19,(obj.isSeeSpell()?1:0));
			statement.setInt(20,(obj.isShowFriendConnection()?1:0));
			statement.setInt(21,(obj.isShowWings()?1:0));
			statement.setInt(22,(obj.isSeeSeller()?1:0));
			statement.setString(23,obj.getCanaux());
			statement.setInt(24,obj.getMap().getId());
			statement.setInt(25,obj.getCell().getId());
			statement.setInt(26,obj.getPdvPer());
			statement.setString(27,obj.parseSpellsToDb());
			statement.setString(28,obj.parseObjectsToDb());
			statement.setString(29, obj.parseStoreItemstoBD());
			statement.setString(30,obj.getSavePos());
			statement.setString(31,obj.parseZaaps());
			statement.setString(32,obj.parseJobData());
			statement.setInt(33,obj.getMountXp());
			statement.setInt(34, (obj.getMount()!=null?obj.getMount().getId():-1));
			statement.setByte(35,(obj.getTitle()));
			statement.setInt(36,obj.getWife());
			statement.setInt(37,obj.getId());
			
			execute(statement);
			
			if(obj.getGuildMember() != null)
				World.database.getGuildMemberData().update(obj.getGuildMember());
			if(obj.getMount() != null)
				World.database.getMountData().update(obj.getMount());
			updateItems(obj);
			logger.debug("Personnage "+obj.getName()+" sauvegarde");
			return true;
		} catch(Exception e) {
			logger.error("Can't save character {}",obj.getName(),e);
		}
		return false;
	}
	
	public boolean updateItems(Player obj) {
		try {
			for(Objet item: obj.getItems().values()) {
				World.database.getItemData().update(item);
			}
			
			for(String s : obj.getBankItemsIDSplitByChar(":").split(":")) {
				try {
					int guid = Integer.parseInt(s);
					Objet item = World.data.getObjet(guid);
					if(item == null)continue;
					
					World.database.getItemData().update(item);
				} catch(Exception e) { continue; }
			}
            logger.debug("Objects updated for player",obj.getName());
		} catch (Exception e) {
			logger.error("Can't update items for player {}",obj.getName(),e);
		}
		return false;
	}

	@Override
	public Player load(int id) {
		Player player = null;
		try {
			Result result = getData("SELECT * FROM personnages WHERE guid = "+id);
			player = loadFromResultSet(result.resultSet);
			close(result);
            logger.info("Player {} has been loaded");

		} catch(Exception e) {
			logger.debug("Player {} can't be loaded", id, e);
		}
		return player;
	}

    public void load(){
        try {
            Result result = getData("SELECT * FROM personnages");
            while(loadFromResultSet(result.resultSet) != null);
            close(result);
            logger.info("Players loaded ");
        }
        catch(Exception e) {
            logger.error("Can't load players ",e);
        }
    }
	
	public ArrayList<Player> load(Account obj) {
		ArrayList<Player> players = null;
		try {
			Result result = getData("SELECT * FROM personnages WHERE account = "+obj.getUUID());
            Player player;
            while((player = loadFromResultSet(result.resultSet)) != null) {
                if(players == null) players = new ArrayList<>();
                players.add(player);
            }
            close(result);
            logger.info("Players load for account {}",obj.getUUID());
        }
        catch(Exception e) {
			logger.error("Can't load players for account {}",obj.getUUID(),e);
		}
		return players;
	}
	
	public boolean exist(String name) {
		boolean exist = false;
		try {
			Result result = getData("SELECT * FROM personnages WHERE name = '"+name+"'");
			exist = result.resultSet.next();
			close(result);
            logger.trace("Player {} existe : {}",name,exist);
		} catch(Exception e) {
			logger.error("Can't ask database if {} exist ", name, e);
		}
		return exist;
	}
	
	public int nextId() {
		int guid = -1;
		
		try {
			String query = "SELECT MAX(guid) AS max FROM personnages;";
			Result result = getData(query);
			while(result.resultSet.next())
				guid = result.resultSet.getInt("max")+1;
			close(result);
            logger.trace("Last id in personnage is {}",guid);
		} catch (Exception e) {
            logger.info("Can t find last id in personnages",e);
		}
		return guid;
	}

    protected Player loadFromResultSet(ResultSet resultSet) throws SQLException {
        Player player = null;
        if (resultSet.next()) {
            TreeMap<Integer, Integer> stats = new TreeMap<Integer, Integer>();
            stats.put(Constants.STATS_ADD_VITA, resultSet.getInt("vitalite"));
            stats.put(Constants.STATS_ADD_FORC, resultSet.getInt("force"));
            stats.put(Constants.STATS_ADD_SAGE, resultSet.getInt("sagesse"));
            stats.put(Constants.STATS_ADD_INTE, resultSet.getInt("intelligence"));
            stats.put(Constants.STATS_ADD_CHAN, resultSet.getInt("chance"));
            stats.put(Constants.STATS_ADD_AGIL, resultSet.getInt("agilite"));

            player = new Player(
                    resultSet.getInt("guid"),
                    resultSet.getString("name"),
                    resultSet.getInt("sexe"),
                    resultSet.getInt("class"),
                    resultSet.getInt("color1"),
                    resultSet.getInt("color2"),
                    resultSet.getInt("color3"),
                    resultSet.getLong("kamas"),
                    resultSet.getInt("spellboost"),
                    resultSet.getInt("capital"),
                    resultSet.getInt("energy"),
                    resultSet.getInt("level"),
                    resultSet.getLong("xp"),
                    resultSet.getInt("size"),
                    resultSet.getInt("gfx"),
                    resultSet.getByte("alignement"),
                    resultSet.getInt("account"),
                    stats,
                    resultSet.getByte("seeFriend"),
                    resultSet.getByte("seeAlign"),
                    resultSet.getByte("seeSeller"),
                    resultSet.getString("canaux"),
                    resultSet.getShort("map"),
                    resultSet.getInt("cell"),
                    resultSet.getString("objets"),
                    resultSet.getString("storeObjets"),
                    resultSet.getInt("pdvper"),
                    resultSet.getString("spells"),
                    resultSet.getString("savepos"),
                    resultSet.getString("jobs"),
                    resultSet.getInt("mountxpgive"),
                    resultSet.getInt("mount"),
                    resultSet.getInt("honor"),
                    resultSet.getInt("deshonor"),
                    resultSet.getInt("alvl"),
                    resultSet.getString("zaaps"),
                    resultSet.getByte("title"),
                    resultSet.getInt("wife")
            );
            //V�rifications pr�-connexion
            player.VerifAndChangeItemPlace();
            World.data.addPersonnage(player);
            int guildId = World.database.getGuildMemberData().getGuildByPlayer(player.getId());
            if(guildId >= 0) {
            	Guild guild =  World.data.getGuild(guildId);
            	if(guild != null) {
            		GuildMember member = guild.getMember(resultSet.getInt("guid"));
            		if(member != null)
            			player.setGuildMember(member);
            	}
            }  
        }
        return player;
    }
}
