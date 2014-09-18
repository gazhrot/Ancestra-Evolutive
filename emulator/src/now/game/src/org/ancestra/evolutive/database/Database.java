package org.ancestra.evolutive.database;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.database.data.*;
import org.ancestra.evolutive.database.data.PlayerData;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class Database {
	//connection
	private HikariDataSource gameDataSource;
	private HikariDataSource loginDataSource;
    private static Logger logger = (Logger) LoggerFactory.getLogger(Database.class);
	//data
	private AccountData accountData;
	private AnimationData animationData;
	private AreaData areaData;
	private AreaSubData areaSubData;
	private PlayerData playerData;
	private CollectorData collectorData;
	private GuildData guildData;
	private GuildMemberData guildMemberData;
	private HouseData houseData;
	private ObjectData itemData;
	private ObjectSetData objectSetData;
	private ObjectTemplateData itemTemplateData;
	private JobData jobData;
	private MapData mapData;
	private MonsterData monsterData;
	private MountData mountData;
	private MountparkData mountparkData;
	private NpcAnswerData npcAnswerData;
	private NpcData npcData;
	private NpcQuestionData npcQuestionData;
	private NpcTemplateData npcTemplateData;
	private ScriptedCellData scriptedCellData;
	private SpellData spellData;
	private HdvData hdvData;
	private InteractiveObjectTemplateData ioTemplates;
	private TrunkData trunkData;
	private ExpData expData;
	private LoginOtherData loginOtherData;
	private GameOtherData gameOtherData;
	private DropData dropData;
	
	public void initializeData() {
		this.accountData = new AccountData(loginDataSource);
		this.animationData = new AnimationData(gameDataSource);
		this.areaData = new AreaData(gameDataSource);
		this.areaSubData = new AreaSubData(gameDataSource);
		this.playerData = new PlayerData(loginDataSource);
		this.collectorData = new CollectorData(gameDataSource);
		this.guildData = new GuildData(gameDataSource);
		this.guildMemberData = new GuildMemberData(gameDataSource);
		this.houseData = new HouseData(gameDataSource);
		this.itemData = new ObjectData(gameDataSource);
		this.objectSetData = new ObjectSetData(gameDataSource);
		this.itemTemplateData = new ObjectTemplateData(gameDataSource);
		this.jobData = new JobData(gameDataSource);
		this.mapData = new MapData(gameDataSource);
		this.monsterData = new MonsterData(gameDataSource);
		this.npcAnswerData = new NpcAnswerData(gameDataSource);
		this.npcData = new NpcData(gameDataSource);
		this.npcQuestionData = new NpcQuestionData(gameDataSource);
		this.npcTemplateData = new NpcTemplateData(gameDataSource);
		this.scriptedCellData = new ScriptedCellData(gameDataSource);
		this.spellData = new SpellData(gameDataSource);
		this.hdvData = new HdvData(gameDataSource);
		this.ioTemplates = new InteractiveObjectTemplateData(gameDataSource);
		this.trunkData = new TrunkData(gameDataSource);
		this.expData = new ExpData(gameDataSource);
		this.loginOtherData = new LoginOtherData(loginDataSource);
		this.gameOtherData = new GameOtherData(gameDataSource);
		this.dropData = new DropData(gameDataSource);
		this.mountData = new MountData(gameDataSource);
		this.mountparkData = new MountparkData(gameDataSource);
	}
	
	public boolean initializeConnection() {
        logger.trace("Reading database config");
        
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", Server.config.getLHost());
        config.addDataSourceProperty("port",Server.config.getLPort());
        config.addDataSourceProperty("databaseName",Server.config.getLDatabaseName());
        config.addDataSourceProperty("user",Server.config.getLUser());
        config.addDataSourceProperty("password", Server.config.getLPass());

        loginDataSource = new HikariDataSource(config);
        
        if(!testConnection(loginDataSource)){
            logger.error("Pleaz check your username and password and database login connection");
            System.exit(0);
        }
        logger.info("Login database connection established");
        
        config = new HikariConfig();
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", Server.config.getHost());
        config.addDataSourceProperty("port",Server.config.getPort());
        config.addDataSourceProperty("databaseName",Server.config.getDatabaseName());
        config.addDataSourceProperty("user",Server.config.getUser());
        config.addDataSourceProperty("password", Server.config.getPass());

        gameDataSource = new HikariDataSource(config);
        
        if(!testConnection(gameDataSource)){
            logger.error("Pleaz check your username and password and database game connection");
            System.exit(0);
        }
        logger.info("Game database connection established");
               
        this.initializeData();
        return true;
	}
	
	public HikariDataSource getGameDataSource() {
		return gameDataSource;
	}
	
	public HikariDataSource getLoginDataSource() {
		return loginDataSource;
	}
	
	public AccountData getAccountData() {
		return accountData;
	}
	
	public AnimationData getAnimationData() {
		return animationData;
	}
	
	public AreaData getAreaData() {
		return areaData;
	}
	
	public AreaSubData getAreaSubData() {
		return areaSubData;
	}
	
	public PlayerData getPlayerData() {
		return playerData;
	}
	
	public CollectorData getCollectorData() {
		return collectorData;
	}
	
	public HouseData getHouseData() {
		return houseData;
	}
	
	public ObjectData getItemData() {
		return itemData;
	}
	
	public ObjectSetData getObjectSetData() {
		return objectSetData;
	}
	
	public ObjectTemplateData getItemTemplateData() {
		return itemTemplateData;
	}
	
	public JobData getJobData() {
		return jobData;
	}
	
	public MapData getMapData() {
		return mapData;
	}
	
	public MonsterData getMonsterData() {
		return monsterData;
	}
	
	public MountData getMountData() {
		return mountData;
	}
	
	public MountparkData getMountparkData() {
		return mountparkData;
	}
	
	public NpcAnswerData getNpcAnswerData() {
		return npcAnswerData;
	}
	
	public NpcData getNpcData() {
		return npcData;
	}
	
	public NpcQuestionData getNpcQuestionData() {
		return npcQuestionData;
	}
	
	public NpcTemplateData getNpcTemplateData() {
		return npcTemplateData;
	}
	
	public ScriptedCellData getScriptedCellData() {
		return scriptedCellData;
	}
	
	public SpellData getSpellData() {
		return spellData;
	}

	public GuildData getGuildData() {
		return guildData;
	}

	public GuildMemberData getGuildMemberData() {
		return guildMemberData;
	}

	public HdvData getHdvData() {
		return hdvData;
	}

	public void setHdvData(HdvData hdvData) {
		this.hdvData = hdvData;
	}

	public InteractiveObjectTemplateData getIoTemplates() {
		return ioTemplates;
	}

	public void setIoTemplates(InteractiveObjectTemplateData ioTemplates) {
		this.ioTemplates = ioTemplates;
	}

	public TrunkData getTrunkData() {
		return trunkData;
	}

	public void setTrunkData(TrunkData trunkData) {
		this.trunkData = trunkData;
	}

	public ExpData getExpData() {
		return expData;
	}

	public void setExpData(ExpData expData) {
		this.expData = expData;
	}
	
	public LoginOtherData getLoginOtherData() {
		return loginOtherData;
	}

	public void setLoginOtherData(LoginOtherData loginOtherData) {
		this.loginOtherData = loginOtherData;
	}

	public GameOtherData getGameOtherData() {
		return gameOtherData;
	}

	public void setGameOtherData(GameOtherData gameOtherData) {
		this.gameOtherData = gameOtherData;
	}

	public DropData getDropData() {
		return dropData;
	}

	public void setDropData(DropData dropData) {
		this.dropData = dropData;
	}

    private boolean testConnection(HikariDataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
