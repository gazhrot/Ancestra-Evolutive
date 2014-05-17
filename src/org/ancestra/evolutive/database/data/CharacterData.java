package org.ancestra.evolutive.database.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.database.AbstractDAO;
import org.ancestra.evolutive.object.Objet;

public class CharacterData extends AbstractDAO<Player>{

	public CharacterData(Connection connection, ReentrantLock locker) {
		super(connection, locker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Player obj) {
		String baseQuery = "INSERT INTO personnages( `guid` ," +
				" `name` , `sexe` , `class` , `color1` , `color2` , `color3` , `kamas` ," +
				" `spellboost` , `capital` , `energy` , `level` , `xp` , `size` , `gfx` ," +
				" `account`,`cell`,`map`,`spells`,`objets`, `storeObjets`)" +
				" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'', '');";
		
		try {
			PreparedStatement statement = super.connection.prepareStatement(baseQuery);
			
			statement.setInt(1,obj.getUUID());
			statement.setString(2, obj.getName());
			statement.setInt(3,obj.getSex());
			statement.setInt(4,obj.getClasse());
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
			statement.setInt(17,obj.getCurCell().getId());
			statement.setInt(18,obj.getCurMap().getId());
			statement.setString(19, obj.parseSpellsToDb());
			
			execute(statement);
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(CharacterData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public boolean delete(Player obj) {
		try {
			String baseQuery = "DELETE FROM personnages WHERE guid = "+obj.getUUID();
			execute(baseQuery);
			
			if(!obj.getItemsIDSplitByChar(",").equals("")) {
				baseQuery = "DELETE FROM items WHERE guid IN ('"+obj.getItemsIDSplitByChar(",")+"');";
				execute(baseQuery);
			}
			if(!obj.getStoreItemsIDSplitByChar(",").equals("")) {
				baseQuery = "DELETE FROM items WHERE guid IN ('"+obj.getStoreItemsIDSplitByChar(",")+"');";
				execute(baseQuery);
			}
			if(obj.getMount() != null) {
				baseQuery = "DELETE FROM mounts_data WHERE id = "+obj.getMount().getId();
				execute(baseQuery);
				World.data.delDragoByID(obj.getMount().getId());
			}
			return true;
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(CharacterData): "+e.getMessage());
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
			
			PreparedStatement statement = connection.prepareStatement(baseQuery);
			
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
			statement.setInt(24,obj.getCurMap().getId());
			statement.setInt(25,obj.getCurCell().getId());
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
			statement.setInt(37,obj.getUUID());
			
			execute(statement);
			
			if(obj.getGuildMember() != null)
				World.database.getGuildMemberData().update(obj.getGuildMember());
			if(obj.getMount() != null)
				World.database.getMountData().update(obj.getMount());
			updateItems(obj);
			Console.instance.println("Personnage "+obj.getName()+" sauvegarde");
			return true;
		} catch(Exception e) {
			Console.instance.writeln("SQL ERROR(CharacterData): "+e.getMessage());
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
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(CharacterData): "+e.getMessage());
		}
		return false;
	}

	@Override
	public Player load(int id) {
		Player player = null;
		try {
			ResultSet result = getData("SELECT * FROM personnages WHERE guid = "+id);
			
			if(result.next()) {
				TreeMap<Integer,Integer> stats = new TreeMap<Integer,Integer>();
				stats.put(Constants.STATS_ADD_VITA, result.getInt("vitalite"));
				stats.put(Constants.STATS_ADD_FORC, result.getInt("force"));
				stats.put(Constants.STATS_ADD_SAGE, result.getInt("sagesse"));
				stats.put(Constants.STATS_ADD_INTE, result.getInt("intelligence"));
				stats.put(Constants.STATS_ADD_CHAN, result.getInt("chance"));
				stats.put(Constants.STATS_ADD_AGIL, result.getInt("agilite"));
				
				player = new Player(
						result.getInt("guid"),
						result.getString("name"),
						result.getInt("sexe"),
						result.getInt("class"),
						result.getInt("color1"),
						result.getInt("color2"),
						result.getInt("color3"),
						result.getLong("kamas"),
						result.getInt("spellboost"),
						result.getInt("capital"),
						result.getInt("energy"),
						result.getInt("level"),
						result.getLong("xp"),
						result.getInt("size"),
						result.getInt("gfx"),
						result.getByte("alignement"),
						result.getInt("account"),
						stats,
						result.getByte("seeFriend"),
						result.getByte("seeAlign"),
						result.getByte("seeSeller"),
						result.getString("canaux"),
						result.getShort("map"),
						result.getInt("cell"),
						result.getString("objets"),
						result.getString("storeObjets"),
						result.getInt("pdvper"),
						result.getString("spells"),
						result.getString("savepos"),
						result.getString("jobs"),
						result.getInt("mountxpgive"),
						result.getInt("mount"),
						result.getInt("honor"),
						result.getInt("deshonor"),
						result.getInt("alvl"),
						result.getString("zaaps"),
						result.getByte("title"),
						result.getInt("wife")
						);
				//Vérifications pré-connexion
				player.VerifAndChangeItemPlace();
				World.data.addPersonnage(player);
				
			}
			closeResultSet(result);
		} catch(Exception e) {
			Console.instance.writeln("SQL ERROR(CharacterData): "+e.getMessage());
		}
		return player;
	}
	
	public Player loadByAccount(Account obj) {
		Player player = null;
		try {
			ResultSet result = getData("SELECT * FROM personnages WHERE account = "+obj.getUUID());
			while(result.next()) {
				TreeMap<Integer,Integer> stats = new TreeMap<Integer,Integer>();
				stats.put(Constants.STATS_ADD_VITA, result.getInt("vitalite"));
				stats.put(Constants.STATS_ADD_FORC, result.getInt("force"));
				stats.put(Constants.STATS_ADD_SAGE, result.getInt("sagesse"));
				stats.put(Constants.STATS_ADD_INTE, result.getInt("intelligence"));
				stats.put(Constants.STATS_ADD_CHAN, result.getInt("chance"));
				stats.put(Constants.STATS_ADD_AGIL, result.getInt("agilite"));
				
				player = new Player(
						result.getInt("guid"),
						result.getString("name"),
						result.getInt("sexe"),
						result.getInt("class"),
						result.getInt("color1"),
						result.getInt("color2"),
						result.getInt("color3"),
						result.getLong("kamas"),
						result.getInt("spellboost"),
						result.getInt("capital"),
						result.getInt("energy"),
						result.getInt("level"),
						result.getLong("xp"),
						result.getInt("size"),
						result.getInt("gfx"),
						result.getByte("alignement"),
						result.getInt("account"),
						stats,
						result.getByte("seeFriend"),
						result.getByte("seeAlign"),
						result.getByte("seeSeller"),
						result.getString("canaux"),
						result.getShort("map"),
						result.getInt("cell"),
						result.getString("objets"),
						result.getString("storeObjets"),
						result.getInt("pdvper"),
						result.getString("spells"),
						result.getString("savepos"),
						result.getString("jobs"),
						result.getInt("mountxpgive"),
						result.getInt("mount"),
						result.getInt("honor"),
						result.getInt("deshonor"),
						result.getInt("alvl"),
						result.getString("zaaps"),
						result.getByte("title"),
						result.getInt("wife"));
				
				//vérifications pré-connexion
				player.VerifAndChangeItemPlace();
				World.data.addPersonnage(player);
				
				//ajout de la guilde
				int guildId = World.database.getGuildMemberData().getGuildByPlayer(player.getUUID());
				if(guildId >= 0)
					player.setGuildMember(World.data.getGuild(guildId).getMember(result.getInt("guid")));
				
				
			}
			closeResultSet(result);
		} catch(Exception e) {
			Console.instance.writeln("SQL ERROR(CharacterData): "+e.getMessage());
		}
		return player;
	}
	
	public boolean exist(String name) {
		boolean exist = false;
		try {
			ResultSet result = getData("SELECT * FROM personnages WHERE name = '"+name+"'");
			exist = result.next();
			closeResultSet(result);
		} catch(Exception e) {
			Console.instance.writeln("SQL ERROR(CharacterData): "+e.getMessage());
		}
		return exist;
	}
	
	public int nextId() {
		int guid = -1;
		
		try {
			String query = "SELECT MAX(guid) AS max FROM personnages;";
			ResultSet result = getData(query);
			
			while(result.next())
				guid = result.getInt("max")+1;
			
			closeResultSet(result);
		} catch (Exception e) {
			Console.instance.writeln("SQL ERROR(CharacterData): "+e.getMessage());
		}
		return guid;
	}
}
